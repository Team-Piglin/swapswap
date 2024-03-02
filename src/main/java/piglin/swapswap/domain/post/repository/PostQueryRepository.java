package piglin.swapswap.domain.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.constant.Category;
import piglin.swapswap.domain.post.constant.City;
import piglin.swapswap.domain.post.dto.response.PostListDetailResponseDto;
import piglin.swapswap.domain.post.dto.response.PostGetResponseDto;

public interface PostQueryRepository {

    List<PostListDetailResponseDto> findPostListWithFavoriteByCursor(
            Member member, LocalDateTime cursorTime);

    List<PostListDetailResponseDto> searchPostListWithFavorite(String titleCond, Category categoryCond,
            City cityCond, Member member, LocalDateTime cursorTime);

    PostGetResponseDto findPostWithFavorite(Long postId, Member member);

    void updatePostViewCnt(Long postId);

    List<PostListDetailResponseDto> findAllMyFavoritePost(Member member,
            LocalDateTime cursorTime);

    List<PostListDetailResponseDto> findAllMyPostList(Member member, LocalDateTime cursorTime);

    void updatePostListStatus(List<Long> postIdList, DealStatus dealStatus);

    void deleteAllPostByMember(Member loginMember);

    void reRegisterPostByMember(Member loginMember);
}
