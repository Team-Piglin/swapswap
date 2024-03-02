package piglin.swapswap.domain.member.mapper;

import java.time.LocalDateTime;
import javax.sound.midi.MetaMessage;
import piglin.swapswap.domain.member.constant.MemberRole;
import piglin.swapswap.domain.member.dto.OtherMemberInfoDto;
import piglin.swapswap.domain.member.dto.SocialUserInfo;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.dto.response.PostListResponseDto;
import piglin.swapswap.domain.wallet.entity.Wallet;

public class MemberMapper {

    public static Member createMember(SocialUserInfo socialUserInfo, Wallet wallet) {

        return Member.builder()
                .email(socialUserInfo.email())
                .nickname(socialUserInfo.nickname())
                .role(MemberRole.USER)
                .isDeleted(false)
                .wallet(wallet)
                .build();
    }

    public static OtherMemberInfoDto createOtherMemberInfoDto(Member member,
            PostListResponseDto postList) {

        return OtherMemberInfoDto.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .createdTime(member.getCreatedTime())
                .postList(postList)
                .build();
    }

}
