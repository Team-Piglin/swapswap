package piglin.swapswap.domain.member.contorller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import piglin.swapswap.domain.member.dto.MemberNicknameDto;
import piglin.swapswap.domain.member.dto.OtherMemberInfoDto;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.member.service.KakaoServiceImpl;
import piglin.swapswap.domain.member.service.MemberService;
import piglin.swapswap.domain.membercoupon.dto.response.MyCouponGetResponseDto;
import piglin.swapswap.domain.membercoupon.service.MemberCouponService;
import piglin.swapswap.domain.post.dto.response.PostListResponseDto;
import piglin.swapswap.domain.post.service.PostService;
import piglin.swapswap.global.annotation.AuthMember;
import piglin.swapswap.global.annotation.HttpRequestLog;
import piglin.swapswap.global.jwt.JwtCookieManager;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final KakaoServiceImpl kakaoServiceImpl;

    private final MemberService memberService;

    private final PostService postService;

    private final MemberCouponService memberCouponService;

    @HttpRequestLog
    @GetMapping("/login/kakao/callback")
    public String kakaoLogin(
            @RequestParam String code,
            HttpServletResponse response
    ) {

        try {
            String accessToken = kakaoServiceImpl.kakaoLogin(code);
            JwtCookieManager.addJwtToCookie(accessToken, response);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "redirect:/";
    }

    @GetMapping("/members/logout")
    public String logout(
            HttpServletResponse response
    ) {

        JwtCookieManager.expireTokenCookie(response);

        return "redirect:/";
    }

    @HttpRequestLog
    @ResponseBody
    @PatchMapping("/members/nickname")
    public ResponseEntity<?> updateNickname(
            @AuthMember Member member,
            @Valid @RequestBody MemberNicknameDto requestDto
    ) {

        memberService.updateNickname(member, requestDto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/myPage")
    public String myPage() {

        return "member/myPage";
    }

    @GetMapping("/editProfile")
    public String editProfile() {

        return "member/editProfile";
    }

    @GetMapping("/unregister")
    public String unregister(@AuthMember Member member, Model model) {

        model.addAttribute("currentSwapMoney", memberService.getMySwapMoney(member.getId()));

        return "member/unregister";
    }

    @HttpRequestLog
    @ResponseBody
    @DeleteMapping("/members")
    public ResponseEntity<?> unregister(
            @AuthMember Member member,
            HttpServletResponse response
    ) {

        memberService.deleteMember(member);
        JwtCookieManager.expireTokenCookie(response);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/members/coupon")
    public String getMyCouponList(
            @AuthMember Member member,
            Model model
    ) {

        List<MyCouponGetResponseDto> myCouponGetResponseDtoList = memberCouponService.getMycouponList(
                member);

        model.addAttribute("myCouponGetResponseDtoList", myCouponGetResponseDtoList);

        return "member/myCouponList";
    }

    @GetMapping("/members/swap-money")
    public String getMySwapMoney(
            @AuthMember Member member,
            Model model
    ) {

        Long mySwapMoney = memberService.getMySwapMoney(member.getId());

        model.addAttribute("mySwapMoney", mySwapMoney);

        return "member/mySwapMoney";
    }

    @GetMapping("/members/favorites")
    public String getMyFavoriteList(
            @AuthMember Member member,
            @RequestParam(required = false) LocalDateTime cursorTime,
            Model model
    ) {

        PostListResponseDto responseDtoList = postService.getMyFavoritePostList(
                member, cursorTime);

        model.addAttribute("postListResponseDto", responseDtoList);

        return "post/postFavoriteList";
    }

    @GetMapping("/members/favorites/more")
    public String getMyFavoriteListMore(
            @AuthMember Member member,
            LocalDateTime cursorTime,
            Model model
    ) {

        PostListResponseDto responseDtoList = postService.getMyFavoritePostList(
                member, cursorTime);

        model.addAttribute("postListResponseDto", responseDtoList);

        return "post/postListFragment";
    }

    @GetMapping("/members/posts")
    public String getMyPostList(
            @AuthMember Member member,
            @RequestParam(required = false) LocalDateTime cursorTime,
            Model model
    ) {

        PostListResponseDto responseDtoList = postService.getMyPostList(member,
                cursorTime);

        model.addAttribute("postListResponseDto", responseDtoList);

        return "member/myPostList";
    }

    @GetMapping("/members/{memberId}")
    public String getOtherMemberInfo(@PathVariable Long memberId, Model model,@RequestParam(required = false) LocalDateTime cursorTime) {

        OtherMemberInfoDto otherMemberInfo = memberService.getOtherMemberInfo(memberId, cursorTime);
        model.addAttribute("otherMemberInfo", otherMemberInfo);

        return "member/otherMemberInfo";
    }

    @GetMapping("/members/posts/more")
    public String getMyPostListMore(
            @AuthMember Member member,
            LocalDateTime cursorTime,
            Model model
    ) {

        PostListResponseDto responseDtoList = postService.getMyPostList(member,
                cursorTime);

        model.addAttribute("postListResponseDto", responseDtoList);

        return "post/postListFragment";
    }

    @ResponseBody
    @GetMapping("/members/checkNickname")
    public ResponseEntity<?> checkNickname(
            @RequestParam String nickname
    ) {

        boolean nicknameExists = memberService.checkNicknameExists(nickname);

        return ResponseEntity.ok(nicknameExists);
    }
}