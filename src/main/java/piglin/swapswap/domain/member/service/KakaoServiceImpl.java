package piglin.swapswap.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import piglin.swapswap.domain.favorite.service.FavoriteService;
import piglin.swapswap.domain.member.constant.AnimalAdjective;
import piglin.swapswap.domain.member.constant.AnimalName;
import piglin.swapswap.domain.member.dto.SocialUserInfo;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.member.mapper.MemberMapper;
import piglin.swapswap.domain.member.repository.MemberRepository;
import piglin.swapswap.domain.membercoupon.service.MemberCouponService;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.post.service.PostService;
import piglin.swapswap.domain.wallet.entity.Wallet;
import piglin.swapswap.domain.wallet.service.WalletService;
import piglin.swapswap.domain.wallethistory.service.WalletHistoryService;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.jwt.JwtUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoServiceImpl implements SocialService {

    @Value("${kakao.client.id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final WalletService walletService;
    private final MemberRepository memberRepository;
    private final PostService postService;
    private final MemberCouponService memberCouponService;
    private final WalletHistoryService walletHistoryService;
    private final FavoriteService favoriteService;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    @SwapLog
    @Transactional
    public String kakaoLogin(String code) throws Exception {

        log.info("\nkakaoLogin");
        String accessToken = getToken(code);
        SocialUserInfo kakaoUserInfo = getUser(accessToken);
        Member kakaoMember = registerUserIfNeeded(kakaoUserInfo);

        return jwtUtil.createToken(kakaoMember.getEmail(), kakaoMember.getRole());
    }

    @Override
    public String getToken(String code) throws JsonProcessingException {

        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", KAKAO_REDIRECT_URI);
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());

        return jsonNode.get("access_token").asText();
    }

    @Override
    public SocialUserInfo getUser(String accessToken) {

        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readTree(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();

        AnimalAdjective randomAdjective = getRandomEnum(AnimalAdjective.class);
        AnimalName randomAnimalName = getRandomEnum(AnimalName.class);

        String nickname = randomAdjective.getAdjective() + randomAnimalName.getName() + UUID.randomUUID().toString().substring(0, 4);

        while (memberRepository.existsByNickname(nickname)) {
            nickname = randomAdjective.getAdjective() + randomAnimalName.getName() + UUID.randomUUID().toString().substring(0, 4);
        }

        log.info("\n사용자 닉네임: {} | 사용자 이메일: {}", nickname, email);

        return SocialUserInfo.createSocialUserInfo(id, nickname, email);
    }

    private <T extends Enum<?>> T getRandomEnum(Class<T> enumClass) {
        T[] enumConstants = enumClass.getEnumConstants();
        int randomIndex = (int) (Math.random() * enumConstants.length);
        return enumConstants[randomIndex];
    }

    public Member registerUserIfNeeded(SocialUserInfo kakaoUserInfo) {

        String kakaoEmail = kakaoUserInfo.email();

        return memberRepository.findByEmail(kakaoEmail)
                .map(existingMember -> {
                    if (isWithdrawnMember(existingMember)) {
                        log.info("\nreRegisterMember");
                        reRegisterAssociatedEntities(existingMember);
                        return existingMember;
                    }
                    log.info("\nnormalMember");
                    return existingMember;
                })
                .orElseGet(() -> {
                    log.info("\nnewMember");
                    Wallet savedWallet = walletService.createWallet();
                    return memberRepository.save(
                            MemberMapper.createMember(kakaoUserInfo, savedWallet));
                });
    }

    private void reRegisterAssociatedEntities(Member member) {

        member.reRegisterMember();

        member.getWallet().reRegisterWallet();

        walletHistoryService.reRegisterWalletHistoryByWallet(member.getWallet());

        memberCouponService.reRegisterCouponByMember(member);

        favoriteService.reRegisterFavoriteByMember(member);

        List<Post> post = postService.findByMemberId(member.getId());

        favoriteService.reRegisterFavoriteByPost(post);

        postService.reRegisterPostByMember(member);
    }

    public boolean isWithdrawnMember(Member member) {

        return member.getIsDeleted();
    }
}
