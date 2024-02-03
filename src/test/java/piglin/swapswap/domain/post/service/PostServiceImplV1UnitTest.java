package piglin.swapswap.domain.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.favorite.service.FavoriteServiceImplV1;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.constant.Category;
import piglin.swapswap.domain.post.constant.City;
import piglin.swapswap.domain.post.constant.PostConstant;
import piglin.swapswap.domain.post.dto.request.PostCreateRequestDto;
import piglin.swapswap.domain.post.dto.request.PostUpdateRequestDto;
import piglin.swapswap.domain.post.dto.response.PostGetResponseDto;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.post.repository.PostRepository;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.s3.S3ImageServiceImplV1;

@ExtendWith(MockitoExtension.class)
class PostServiceImplV1UnitTest {

    @Mock
    private S3ImageServiceImplV1 s3ImageServiceImplV1;

    @Mock
    private PostRepository postRepository;

    @Mock
    private FavoriteServiceImplV1 favoriteService;

    @InjectMocks
    private PostServiceImplV1 postService;

    private Member member;
    private Long memberId = 1L;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .id(memberId)
                .nickname("nickname")
                .build();
    }

    @Nested
    @DisplayName("createPost 메소드 테스트 모음")
    class CreatePostTestList {

        @Test
        @DisplayName("게시글 저장 - 성공 / 이미지 1장 업로드")
        void createPost_Success_Image_One() {
            // Given
            List<MultipartFile> imageUrlList = new ArrayList<>();
            imageUrlList.add(Mockito.mock(MultipartFile.class));
            PostCreateRequestDto requestDto = new PostCreateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);

            when(s3ImageServiceImplV1.saveImageUrlList(any())).thenReturn(new ArrayList<>());
            // When
            postService.createPost(member, requestDto);
            // Then
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("게시글 저장 - 실패 / 이미지를 최소 1장 업로드 해야합니다.")
        void createPost_Fail_Image_None() {
            // Given
            List<MultipartFile> imageUrlList = new ArrayList<>();
            PostCreateRequestDto requestDto = new PostCreateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);

            // When - Then
            assertThatThrownBy(() -> postService.createPost(member, requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(ErrorCode.POST_IMAGE_MIN_SIZE.getMessage());
        }

        @Test
        @DisplayName("게시글 저장 - 실패 / 이미지는 최대 n장만 업로드 할 수 있습니다.")
        void createPost_Fail_Image_Over() {
            // Given
            List<MultipartFile> imageUrlList = new ArrayList<>();

            for (int i = 0; i < PostConstant.IMAGE_MAX_SIZE + 1; i++) {
                imageUrlList.add(Mockito.mock(MultipartFile.class));
            }

            PostCreateRequestDto requestDto = new PostCreateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);

            // When - Then
            assertThatThrownBy(() -> postService.createPost(member, requestDto))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(ErrorCode.POST_IMAGE_MAX_SIZE.getMessage());
        }
    }

    @Nested
    @DisplayName("게시글 단 건 조회 테스트 모음")
    class GetPostTestList {


        private Long memberId = 1L;
        private String author = "작성자";
        private String title = "제목";
        private String content = "내용";
        private Long upCnt = 1L;
        private Long viewCnt = 1L;
        private Category category = Category.ELECTRONICS;
        private Long favoriteCnt = 1L;
        private Boolean favoriteStatus = false;
        private LocalDateTime modifiedUpTime = LocalDateTime.now();

        private PostGetResponseDto normalResponseDto;
        private PostGetResponseDto nullResponseDto;

        private Map<Integer, Object> imageUrl;

        @Nested
        @DisplayName("게시글 단 건 조회 테스트 성공 모음")
        class getPostTestList_SuccessList {

            @BeforeEach
            void setUp() {
                // Map 초기화
                imageUrl = new HashMap<>();
                imageUrl.put(0, "testImageServer.com");

                normalResponseDto = PostGetResponseDto.builder()
                        .memberId(memberId)
                        .author(author)
                        .title(title)
                        .content(content)
                        .category(category)
                        .imageUrl(imageUrl)
                        .viewCnt(viewCnt)
                        .upCnt(upCnt)
                        .favoriteCnt(favoriteCnt)
                        .modifiedUpTime(modifiedUpTime)
                        .favoriteStatus(favoriteStatus)
                        .build();
            }

            @Test
            @DisplayName("게시글 단 건 조회 - 성공 / 로그인 한 사용자")
            void getPost_Success() {
                // Given
                Long postId = 1L;
                when(postRepository.findPostWithFavorite(postId, member)).thenReturn(
                        normalResponseDto);
                // When
                PostGetResponseDto result = postService.getPostWithFavorite(postId, member);
                // Then
                assertThat(result).isNotNull();
                assertThat(result.author()).isEqualTo(author);
                assertThat(result.title()).isEqualTo(title);
                assertThat(result.content()).isEqualTo(content);
                assertThat(result.category()).isEqualTo(category);
                assertThat(result.favoriteCnt()).isEqualTo(favoriteCnt);
                assertThat(result.favoriteStatus()).isEqualTo(favoriteStatus);
            }

            @Test
            @DisplayName("게시글 단 건 조회 - 성공 / 로그인 안 한 사용자")
            void getPost_Success_Not_Login() {
                // Given
                Member notLoginMember = null;
                Long postId = 1L;
                when(postRepository.findPostWithFavorite(postId, notLoginMember)).thenReturn(
                        normalResponseDto);
                // When
                PostGetResponseDto result = postService.getPostWithFavorite(postId, notLoginMember);
                // Then
                assertThat(result).isNotNull();
                assertThat(result.author()).isEqualTo(author);
                assertThat(result.title()).isEqualTo(title);
                assertThat(result.content()).isEqualTo(content);
                assertThat(result.category()).isEqualTo(category);
                assertThat(result.favoriteCnt()).isEqualTo(favoriteCnt);
                assertThat(result.favoriteStatus()).isEqualTo(favoriteStatus);
            }
        }

        @Test
        @DisplayName("게시글 단 건 조회 - 실패 / 존재하지 않는 게시글")
        void getPost_Fail_Not_Found_Post() {
            // Given
            Long postId = 1L;

            // setUp 에서 적용이 안 돼서 여기에 넣어놨습니다...
            nullResponseDto = new PostGetResponseDto(
                    null, null, null, null, null, null,
                    null, null, null, null,null, null,
                    false
            );

            when(postRepository.findPostWithFavorite(postId, member)).thenReturn(nullResponseDto);

            // When - Then
            assertThatThrownBy(() -> postService.getPostWithFavorite(postId, member))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining(ErrorCode.NOT_FOUND_POST_EXCEPTION.getMessage());
        }
    }

    @Nested
    @DisplayName("게시글 수정 테스트 목록")
    class updatePostTestList {

        @Mock
        private Post post;

        @BeforeEach
        void setUp() {
            post = Post.builder().member(member).build();
        }

        @Test
        @DisplayName("게시글 수정 - 실패 / 이미지는 최소 n장 업로드 해야합니다.")
        void updatePost_Fail_Not_Upload_Image() {
            // Given
            List<MultipartFile> imageUrlList = new ArrayList<>();
            PostUpdateRequestDto requestDto = new PostUpdateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);
            Long postId = 1L;
            when(postRepository.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.of(post));
            // When - Then
            assertThatThrownBy(() -> postService.updatePost(postId, member, requestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.POST_IMAGE_MIN_SIZE.getMessage());
        }

        @Test
        @DisplayName("게시글 수정 - 실패 / 이미지는 최소 n장 업로드 해야합니다.")
        void updatePost_Fail_Image_Min() {
            // Given
            List<MultipartFile> imageUrlList = new ArrayList<>();

            for (int i = 0; i < PostConstant.IMAGE_MIN_SIZE - 1; i++) {
                imageUrlList.add(Mockito.mock(MultipartFile.class));
            }

            PostUpdateRequestDto requestDto = new PostUpdateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);
            Long postId = 1L;
            when(postRepository.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.of(post));
            // When - Then
            assertThatThrownBy(() -> postService.updatePost(postId, member, requestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.POST_IMAGE_MIN_SIZE.getMessage());
        }

        @Test
        @DisplayName("게시글 수정 - 실패 / 이미지는 최대 n장 업로드 해야합니다.")
        void updatePost_Fail_Image_Max() {
            // Given
            List<MultipartFile> imageUrlList = new ArrayList<>();

            for (int i = 0; i < PostConstant.IMAGE_MAX_SIZE + 1; i++) {
                imageUrlList.add(Mockito.mock(MultipartFile.class));
            }

            PostUpdateRequestDto requestDto = new PostUpdateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);
            Long postId = 1L;
            when(postRepository.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.of(post));
            // When - Then
            assertThatThrownBy(() -> postService.updatePost(postId, member, requestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.POST_IMAGE_MAX_SIZE.getMessage());
        }

        @Test
        @DisplayName("게시글 수정 - 실패 / 자신의 게시글만 수정할 수 있습니다")
        void updatePost_Fail_Your_Not_Host() {
            // Given
            post = Post.builder().member(member).build();
            Member anotherMember = Member.builder().id(2L).build();

            List<MultipartFile> imageUrlList = new ArrayList<>();
            imageUrlList.add(Mockito.mock(MultipartFile.class));

            PostUpdateRequestDto requestDto = new PostUpdateRequestDto(City.ANDONG, Category.ELECTRONICS, "제목",
                    "내용",
                    imageUrlList);
            Long postId = 1L;
            when(postRepository.findByIdAndIsDeletedIsFalse(postId)).thenReturn(Optional.of(post));
            // When - Then
            assertThatThrownBy(() -> postService.updatePost(postId, anotherMember, requestDto))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REJECT_MODIFIYING_POST_EXCEPTION.getMessage());
        }
    }

    @Nested
    @DisplayName("게시글 삭제 테스트 모음")
    class DeletePostTestList {

        @Mock
        private Post post;

        @Test
        @DisplayName("게시글 삭제 - 성공")
        void deletePost_success() {
            // Given
            post = Post.builder().id(1L).member(member).isDeleted(false).build();
            when(postRepository.findByIdAndIsDeletedIsFalse(memberId)).thenReturn(Optional.of(post));

            // When
            postService.deletePost(member, post.getId());

            // Then
            assertThat(post.getIsDeleted()).isTrue();
        }

        @Test
        @DisplayName("게시글 삭제 - 실패 / 게시글이 이미 지워진 상태입니다.")
        void deletePost_Fail_PostAlreadyDeleted() {
            // Given
            post = Post.builder().id(1L).member(member).isDeleted(true).build();
            when(postRepository.findByIdAndIsDeletedIsFalse(memberId)).thenReturn(Optional.of(post));

            // When - Then
            assertThatThrownBy(() -> postService.deletePost(member, post.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.POST_ALREADY_DELETED.getMessage());
        }

        @Test
        @DisplayName("게시글 삭제 - 실패 / 작성자만 수정 할 수 있습니다.")
        void deletePost_Fail_PostWriterNotMatched() {
            // Given
            Member anotherMember = Member.builder().id(2L).build();
            post = Post.builder().id(1L).member(member).isDeleted(true).build();
            when(postRepository.findByIdAndIsDeletedIsFalse(post.getId())).thenReturn(Optional.of(post));

            // When - Then
            assertThatThrownBy(() -> postService.deletePost(anotherMember, post.getId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REJECT_MODIFIYING_POST_EXCEPTION.getMessage());
        }
    }

    @Nested
    @DisplayName("게시글 업 테스트")
    class UpPostTestList {

        @Test
        @DisplayName("게시글 업 - 성공")
        void upPost_Success() {
            // Given
            Post post = Post.builder()
                    .member(member)
                    .dealStatus(DealStatus.REQUESTED)
                    .modifiedUpTime(LocalDateTime.now().minusDays(1L))
                    .id(1L)
                    .upCnt(1L)
                    .build();
            when(postRepository.findByIdAndIsDeletedIsFalse(post.getId())).thenReturn(Optional.of(post));

            // When
            postService.upPost(post.getId(), member);

            // Then
            assertThat(post.getModifiedUpTime().getDayOfYear()).isEqualTo(LocalDateTime.now().getDayOfYear());
            assertThat(post.getModifiedUpTime().getDayOfMonth()).isEqualTo(LocalDateTime.now().getDayOfMonth());
            assertThat(post.getModifiedUpTime().getHour()).isEqualTo(LocalDateTime.now().getHour());
            assertThat(post.getModifiedUpTime().getMinute()).isEqualTo(LocalDateTime.now().getMinute());
            assertThat(post.getUpCnt()).isEqualTo(2L);
        }

        @Test
        @DisplayName("게시글 업 - 실패 / 작성한 멤버랑 업하려는 멤버랑 다름")
        void upPost_Fail_PostWriterNotMatched() {
            // Given
            Member anotherMember = Member.builder().id(memberId + 1).build();
            Post post = Post.builder()
                    .member(member)
                    .id(1L)
                    .build();
            when(postRepository.findByIdAndIsDeletedIsFalse(post.getId())).thenReturn(Optional.of(post));

            // When - Then
            assertThatThrownBy(() -> postService.upPost(post.getId(), anotherMember))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.REJECT_MODIFIYING_POST_EXCEPTION.getMessage());
        }

        @Test
        @DisplayName("게시글 업 - 실패 / 게시글 등록 및 업 한 지 하루도 지나지 않음")
        void upPost_Fail_PostUpNeedOneDay() {
            // Given
            Post post = Post.builder()
                    .member(member)
                    .dealStatus(DealStatus.REQUESTED)
                    .modifiedUpTime(LocalDateTime.now())
                    .id(1L)
                    .upCnt(1L)
                    .build();
            when(postRepository.findByIdAndIsDeletedIsFalse(post.getId())).thenReturn(Optional.of(post));

            // When - Then
            assertThatThrownBy(() -> postService.upPost(post.getId(), member))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.UP_IS_NEED_ONE_DAY.getMessage());
        }

        @Test
        @DisplayName("게시글 업 - 실패 / 게시글이 거래 진행중임")
        void upPost_Fail_PostIsDealing() {
            // Given
            Post post = Post.builder()
                    .member(member)
                    .dealStatus(DealStatus.DEALING)
                    .modifiedUpTime(LocalDateTime.now().minusDays(1))
                    .id(1L)
                    .upCnt(1L)
                    .build();
            when(postRepository.findByIdAndIsDeletedIsFalse(post.getId())).thenReturn(Optional.of(post));

            // When - Then
            assertThatThrownBy(() -> postService.upPost(post.getId(), member))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.CAN_NOT_UP_CAUSE_POST_DEAL_STATUS_IS_NOT_REQUESTED.getMessage());
        }

        @Test
        @DisplayName("게시글 업 - 실패 / 게시글이 거래 완료 됨")
        void upPost_Fail_PostIsCompleted() {
            // Given
            Post post = Post.builder()
                    .member(member)
                    .dealStatus(DealStatus.COMPLETED)
                    .modifiedUpTime(LocalDateTime.now().minusDays(1))
                    .id(1L)
                    .upCnt(1L)
                    .build();
            when(postRepository.findByIdAndIsDeletedIsFalse(post.getId())).thenReturn(Optional.of(post));

            // When - Then
            assertThatThrownBy(() -> postService.upPost(post.getId(), member))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining(ErrorCode.CAN_NOT_UP_CAUSE_POST_DEAL_STATUS_IS_NOT_REQUESTED.getMessage());
        }
    }

}