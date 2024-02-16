package piglin.swapswap.domain.post.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.favorite.service.FavoriteService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.constant.Category;
import piglin.swapswap.domain.post.constant.City;
import piglin.swapswap.domain.post.constant.PostConstant;
import piglin.swapswap.domain.post.dto.request.PostCreateRequestDto;
import piglin.swapswap.domain.post.dto.request.PostUpdateRequestDto;
import piglin.swapswap.domain.post.dto.response.PostGetResponseDto;
import piglin.swapswap.domain.post.dto.response.PostListDetailResponseDto;
import piglin.swapswap.domain.post.dto.response.PostListResponseDto;
import piglin.swapswap.domain.post.dto.response.PostSimpleResponseDto;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.post.event.DeleteImageUrlMapEvent;
import piglin.swapswap.domain.post.mapper.PostMapper;
import piglin.swapswap.domain.post.repository.PostRepository;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.exception.ajax.AjaxRequestException;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.exception.post.PostNotFoundException;
import piglin.swapswap.global.s3.S3ImageService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImplV1 implements PostService {

    private final FavoriteService favoriteService;
    private final PostRepository postRepository;
    private final S3ImageService s3ImageService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @SwapLog
    @Override
    public Long createPost(Member member, PostCreateRequestDto requestDto) {

        log.info("createPost - memberId: {} | memberEmail: {} | title: {} | content: {}",
                member.getId(), member.getEmail(), requestDto.title(), requestDto.content());

        checkImageUrlListSize(requestDto.imageUrlList());
        List<String> imageUrlList = saveAndGetImageUrlList(requestDto.imageUrlList());

        Post post = PostMapper.createPost(requestDto, createImageUrlMap(imageUrlList), member);
        postRepository.save(post);
        log.info("\npostId: {}", post.getId());

        return post.getId();
    }

    @Override
    @Transactional
    public PostGetResponseDto getPostWithFavorite(Long postId, Member member) {

        PostGetResponseDto responseDto = postRepository.findPostWithFavorite(postId, member);

        isNullPostResponseDto(responseDto);

        postRepository.updatePostViewCnt(postId);

        return responseDto;
    }

    @Override
    public PostListResponseDto getPostList(Member member,
            LocalDateTime cursorTime) {

        List<PostListDetailResponseDto> postList = postRepository.findPostListWithFavoriteByCursor(
                member, cursorTime);

        return PostMapper.createPostListResponseDtoWithIsLast(postList);
    }

    @Override
    public void getPostUpdateWriteForm(Member member, Long postId) {

        Post post = findPost(postId);

        checkPostWriter(member, post);
    }

    @Override
    @SwapLog
    @Transactional
    public void updatePost(Long postId, Member member, PostUpdateRequestDto requestDto) {

        log.info("updatePost - memberId: {} | memberEmail: {} | postId: {} | updateTitle: {} | updateContent: {}",
                member.getId(), member.getEmail(), postId, requestDto.title(), requestDto.content());
        Post post = findPost(postId);

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
    public PostListResponseDto searchPost(String title, String category, String city, Member member,
            LocalDateTime cursorTime) {

        Category categoryCond = null;
        City cityCond = null;
        String titleCond = title.replaceAll(" ", "");

        if (category != null) {
            categoryCond = Enum.valueOf(Category.class, category);
        }

        if (city != null) {
            cityCond = Enum.valueOf(City.class, city);
        }

        List<PostListDetailResponseDto> postList = postRepository.searchPostListWithFavorite(
                titleCond, categoryCond, cityCond, member, cursorTime);

        return PostMapper.createPostListResponseDtoWithIsLast(postList);
    }

    @Override
    @Transactional
    public void upPost(Long postId, Member member) {

        Post post = findPost(postId);
        checkPostWriter(member, post);
        checkModifiedUpTime(post);
        checkPostDealStatus(post);

        post.upPost();
    }

    @Override
    public List<PostSimpleResponseDto> getPostSimpleInfoList(Long memberId) {

        return PostMapper.getPostSimpleInfoListByPostList(
                postRepository.findAllByMemberIdAndIsDeletedIsFalse(memberId));
    }

    @Override
    public PostListResponseDto getMyFavoritePostList(Member member,
            LocalDateTime cursorTime) {

        List<PostListDetailResponseDto> postList = postRepository.findAllMyFavoritePost(
                member, cursorTime);

        return PostMapper.createPostListResponseDtoWithIsLast(postList);
    }

    @Override
    public void updatePostStatusByPostIdList(List<Long> postIdList, DealStatus dealStatus) {

        postRepository.updatePostListStatus(postIdList, dealStatus);
    }

    @Override
    public PostListResponseDto getPostListByMember(Member member, LocalDateTime cursorTime) {

        List<PostListDetailResponseDto> postList = postRepository.findAllMyPostList(member,
                cursorTime);

        return PostMapper.createPostListResponseDtoWithIsLast(postList);
    }

    private void checkPostDealStatus(Post post) {

        if (!post.getDealStatus().equals(DealStatus.REQUESTED)) {

            throw new AjaxRequestException(ErrorCode.CAN_NOT_UP_CAUSE_POST_DEAL_STATUS_IS_NOT_REQUESTED);
        }
    }

    @Override
    public Post getPost(Long postId){
        return postRepository.findByIdAndIsDeletedIsFalse(postId).orElseThrow(PostNotFoundException::new);
    }

    private Map<Integer, Object> createImageUrlMap(List<String> imageUrlList) {

        Map<Integer, Object> imageUrlMap = new HashMap<>();
        for (int i = 0; i < imageUrlList.size(); i++) {
            imageUrlMap.put(i, imageUrlList.get(i));
        }

        return imageUrlMap;
    }

    private List<String> saveAndGetImageUrlList(List<MultipartFile> imageUrlList) {

        return s3ImageService.saveImageUrlList(imageUrlList);
    }

    private void checkModifiedUpTime(Post post) {

        if (post.getModifiedUpTime().plusDays(1).isAfter(LocalDateTime.now())) {
            throw new AjaxRequestException(ErrorCode.UP_IS_NEED_ONE_DAY);
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

    private void isNullPostResponseDto(PostGetResponseDto responseDto) {

        if (responseDto.author() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_POST_EXCEPTION);
        }
    }

    @Override
    public List<Post> findByMemberId(Long memberId) {

        return postRepository.findByMemberId(memberId);
    }

    @Override
    public void deleteAllPostByMember(Member loginMember) {
        postRepository.deleteAllPostByMember(loginMember);
    }

    @Override
    public void reRegisterPostByMember(Member loginMember) {
        postRepository.reRegisterPostByMember(loginMember);
    }
}
