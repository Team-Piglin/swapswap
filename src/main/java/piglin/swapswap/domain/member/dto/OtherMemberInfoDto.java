package piglin.swapswap.domain.member.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import piglin.swapswap.domain.post.dto.response.PostListResponseDto;

@Builder
public record OtherMemberInfoDto(
        Long memberId,

        String nickname,

        LocalDateTime createdTime,

        PostListResponseDto postList
) {

}
