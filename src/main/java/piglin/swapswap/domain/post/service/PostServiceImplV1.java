package piglin.swapswap.domain.post.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import piglin.swapswap.domain.favorite.service.FavoriteService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.constant.Category;
import piglin.swapswap.domain.post.constant.PostConstant;
import piglin.swapswap.domain.post.dto.request.PostCreateRequestDto;
import piglin.swapswap.domain.post.dto.request.PostUpdateRequestDto;
import piglin.swapswap.domain.post.dto.response.PostGetListResponseDto;
import piglin.swapswap.domain.post.dto.response.PostGetResponseDto;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.post.event.DeleteImageUrlMapEvent;
import piglin.swapswap.domain.post.mapper.PostMapper;
import piglin.swapswap.domain.post.repository.PostRepository;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.s3.S3ImageServiceImplV1;

@Service
@RequiredArgsConstructor
public class PostServiceImplV1 implements PostService {

    private final FavoriteService favoriteService;
    private final PostRepository postRepository;
    private final S3ImageServiceImplV1 s3ImageServiceImplV1;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Long createPost(Member member, PostCreateRequestDto requestDto) {

        checkImageUrlListSize(requestDto.imageUrlList());

        if (member == null) {
            throw new BusinessException(ErrorCode.WRITE_ONLY_USER);
        }

        List<String> imageUrlList = saveAndGetImageUrlList(requestDto.imageUrlList());

        Post post = PostMapper.createPost(requestDto, createImageUrlMap(imageUrlList), member);
        postRepository.save(post);

        return post.getId();
    }

    @Override
    @Transactional
    public PostGetResponseDto getPost(Long postId, Member member) {

        PostGetResponseDto responseDto = postRepository.findPostWithFavorite(postId, member);

        IsNullPostResponseDto(responseDto);

        postRepository.updatePostViewCnt(postId);

        return responseDto;
    }

    @Override
    public List<PostGetListResponseDto> getPostList(Member member,
            LocalDateTime cursorTime) {

        return postRepository.findPostListWithFavoriteByCursor(member, cursorTime);
    }

    @Override
    public void getPostUpdateWriteForm(Member member, Long postId) {

        Post post = findPost(postId);

        checkPostWriter(member, post);
    }

    @Override
    @Transactional
    public void updatePost(Long postId, Member member, PostUpdateRequestDto requestDto) {

        Post post = findPost(postId);

        if (member == null) {
            throw new BusinessException(ErrorCode.WRITE_ONLY_USER);
        }

        checkPostWriter(member, post);
        checkImageUrlListSize(requestDto.imageUrlList());

        applicationEventPublisher.publishEvent(new DeleteImageUrlMapEvent(post.getImageUrl()));

        List<String> imageUrlList = saveAndGetImageUrlList(requestDto.imageUrlList());

        PostMapper.updatePost(post, requestDto, createImageUrlMap(imageUrlList));
    }

    @Override
    public void updatePostFavorite(Member member, Long postId) {

        Post post = findPost(postId);

        favoriteService.updateFavorite(member, post);
    }

    private void IsNullPostResponseDto(PostGetResponseDto responseDto) {
        if (responseDto.author() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_POST_EXCEPTION);
        }
    }

    @Override
    @Transactional
    public void deletePost(Member member, Long postId) {

        Post post = findPost(postId);

        checkPostWriter(member, post);
        if (post.getIsDeleted()) {
            throw new BusinessException(ErrorCode.POST_ALREADY_DELETED);
        }

        favoriteService.deleteFavoritesByPostId(postId);

        post.deletePost();
    }

    @Override
    public List<PostGetListResponseDto> searchPost(String title, String category, Member member,
            LocalDateTime cursorTime) {

        Category categoryCond = null;

        if(category != null) {
            categoryCond = Enum.valueOf(Category.class, category);
        }

        return postRepository.searchPostListWithFavorite(title, categoryCond, member, cursorTime);
    }

    @Override
    @Transactional
    public void upPost(Long postId, Member member) {

        Post post = findPost(postId);
        checkPostWriter(member, post);
        checkModifiedUpTime(post);

        post.upPost();
    }

    private boolean isMemberLoggedIn(Member member) {

        return member != null;
    }

    private Map<Integer, Object> createImageUrlMap(List<String> imageUrlList) {

        Map<Integer, Object> imageUrlMap = new HashMap<>();
        for (int i = 0; i < imageUrlList.size(); i++) {
            imageUrlMap.put(i, imageUrlList.get(i));
        }

        return imageUrlMap;
    }

    private List<String> saveAndGetImageUrlList(List<MultipartFile> imageUrlList) {

        return s3ImageServiceImplV1.saveImageUrlList(imageUrlList);
    }

    private void checkModifiedUpTime(Post post) {

        if(post.getModifiedUpTime().plusDays(1).isAfter(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.UP_IS_NEED_ONE_DAY);
        }
    }

    private void checkPostWriter(Member member, Post post) {

        if (!post.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.REJECT_MODIFIYING_POST_EXCEPTION);
        }
    }

    private Post findPost(Long postId) {

        return postRepository.findByIdAndIsDeletedIsFalse(postId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_POST_EXCEPTION)
        );
    }

    private void checkImageUrlListSize(List<MultipartFile> imageUrlList) {

        if (imageUrlList.size() < PostConstant.IMAGE_MIN_SIZE) {
            throw new BusinessException(ErrorCode.POST_IMAGE_MIN_SIZE);
        }
        if (imageUrlList.get(0).isEmpty()) {
            throw new BusinessException(ErrorCode.POST_IMAGE_MIN_SIZE);
        }
        if (imageUrlList.size() > PostConstant.IMAGE_MAX_SIZE) {
            throw new BusinessException(ErrorCode.POST_IMAGE_MAX_SIZE);
        }
    }
}
