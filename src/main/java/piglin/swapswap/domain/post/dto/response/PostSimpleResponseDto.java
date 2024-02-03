package piglin.swapswap.domain.post.dto.response;

import lombok.Builder;

@Builder
public record PostSimpleResponseDto(Long postId, String postTitle, String imageUrl) {

}
