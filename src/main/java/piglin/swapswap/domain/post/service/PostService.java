package piglin.swapswap.domain.post.service;

import java.time.LocalDateTime;
import java.util.List;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.dto.request.PostCreateRequestDto;
import piglin.swapswap.domain.post.dto.request.PostUpdateRequestDto;
import piglin.swapswap.domain.post.dto.response.PostGetResponseDto;
import piglin.swapswap.domain.post.dto.response.PostListResponseDto;
import piglin.swapswap.domain.post.dto.response.PostSimpleResponseDto;
import piglin.swapswap.domain.post.entity.Post;

public interface PostService {

    /**
     * 게시글을 생성하는 메소드입니다.
     * @param member   Post 생성 시 연관 관계에 들어갈 member 입니다.
     * @param requestDto Post 를 생성하기 위해서 받는 Request Dto 입니다. City, Category, Title, Content, ImageUrlList
     *                   로 이루어져 있으며, ImageUrlList 는 다중 업로드가 가능합니다.
     */
    Long createPost(Member member, PostCreateRequestDto requestDto);

    /**
     * 게시글 단 건 조회 메소드입니다.
     * @param postId Post를 검색할 때 쓰이는 PostId 입니다. 이 PostId를 이용하여 예외 처리를 합니다.
     * @param member 익명사용자와 로그인 사용자의 화면을 나누기 위한 member 매개변수 입니다.
     * @return PostGetResponseDto 를 반환하여 Model 로 화면에 그려줍니다.
     */
    PostGetResponseDto getPostWithFavorite(Long postId, Member member);

    /**
     * 게시글 목록 조회 메소드입니다.
     * @param member     favorite 상태를 나타내기 위한 member 매개변수 입니다.
     * @param cursorTime
     * @return List 형식의 PostGetListResponseDto 가 들어갑니다.
     */
    PostListResponseDto getPostList(Member member, LocalDateTime cursorTime);

    /**
     * 게시글 찜 기능입니다.
     * favorite 은 토글로 작동합니다.
     * @param member Favorite 은 Member 와 Post 다대다 관계로, Favorite 에 member 를 등록하기 위해 사용합니다.
     * @param postId 위와 같은 맥락입니다.
     */
    void updatePostFavorite(Member member, Long postId);

    /**
     * 게시글 수정 기능입니다.
     * @param postId 게시글의 고유번호입니다.
     * @param member 게시글 수정 시 작성자 확인을 위해 받는 멤버 매개변수 입니다.
     * @param requestDto 게시글 수정 시 받는 RequestDto 입니다. City, Category, Title, Content, ImageUrlList
     *                   로 이루어져 있으며, ImageUrlList 는 다중 업로드가 가능합니다.
     */
    void updatePost(Long postId, Member member, PostUpdateRequestDto requestDto);

    /**
     * 게시글 수정 폼 불러오는 기능입니다. 예외 처리를 위해 만들었습니다.
     * @param member 게시글 수정 폼 불러올 시 작성자 확인을 위해 받는 멤버 매개변수 입니다.
     * @param postId 게시글의 고유번호입니다.
     */
    void getPostUpdateWriteForm(Member member, Long postId);

    /**
     * 게시글 삭제 기능입니다.
     * 게시글 삭제는 softDelete 로 작동합니다.
     * @param member 게시글 삭제 시 게시글 작성자인지 예외처리를 해주기 위해 받는 멤버입니다.
     * @param postId 삭제할 게시글의 고유 번호입니다.
     */
    void deletePost(Member member, Long postId);

    /**
     * 게시글 검색 기능입니다.
     * @param title 게시글 제목으로 검색할 때 받는 매개변수입니다. null 도 입력 받을 수 있습니다.
     * @param category 게시글 카테고리로 검색할 때 받는 매개변수입니다. null 도 입력 받을 수 있습니다.
     * @param member 게시글 검색 시 자신의 게시글 찜 상태를 확인하기 위해 받는 멤버 매개변수입니다.
     * @param cursorTime 커서 기반 페이지네이션을 위해 받는 커서입니다.
     * @return PostListResponseDto 를 반환하여 Model 로 화면을 그립니다.
     */
    PostListResponseDto searchPost(String title, String category, String city, Member member,
            LocalDateTime cursorTime);

    /**
     * 게시글 업 기능입니다.
     * @param postId 매개변수로 받는 업 할 게시글의 고유 번호입니다.
     * @param member 업 할 게시글의 작성자인지 예외처리 해주기 위해 받는 멤버입니다.
     */

    void upPost(Long postId, Member member);

    /**
     * 내 게시글 목록 조회 기능입니다.
     * @param member 자신의 게시글 목록을 불러오기 위한 UserDetails 에서 받아온 매개변수입니다.
     * @param cursorTime 커서 기반 페이지네이션을 위해 받는 커서입니다.
     * @return PostListResponseDto 를 반환하여 Model 로 화면을 그립니다.
     */
    PostListResponseDto getMyPostList(Member member, LocalDateTime cursorTime);

    /**
     * 내 게시글 찜 목록 조회 기능입니다.
     * @param member 자신의 게시글 찜 목록을 불러오기 위한 UserDetails 에서 받아온 매개변수입니다.
     * @param cursorTime 커서 기반 페이지네이션을 위해 받는 커서입니다.
     * @return PostListResponseDto 를 반환하여 Model 로 화면을 그립니다.
     */
    PostListResponseDto getMyFavoritePostList(Member member, LocalDateTime cursorTime);

    List<PostSimpleResponseDto> getPostSimpleInfoList(Long memberId);

    void updatePostStatusByPostIdList(List<Long> postIdList, DealStatus dealStatus);

    List<Post> findByMemberId(Long memberId);

    void deleteAllPostByMember(Member loginMember);

    void reRegisterPostByMember(Member loginMember);

    Post getPost(Long postId);
}
