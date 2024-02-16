package piglin.swapswap.domain.member.service;

import java.time.LocalDateTime;
import java.util.List;
import piglin.swapswap.domain.member.dto.MemberNicknameDto;
import piglin.swapswap.domain.member.dto.OtherMemberInfoDto;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.dto.response.PostListResponseDto;

public interface MemberService {

    void updateNickname(Member member, MemberNicknameDto requestDto);

    void deleteMember(Member loginMember);

    Long getMySwapMoney(Member member);

    Member getMemberWithWallet(Long memberId);

    Member getMember(Long memberId);

    boolean checkNicknameExists(String nickname);

    List<Member> getMembers(List<Long> memberIds);

    OtherMemberInfoDto getOtherMemberInfo(Long memberId, LocalDateTime cursorTime);

    PostListResponseDto getOtherMemberInfoMore(Long memberId, LocalDateTime cursorTime);
}
