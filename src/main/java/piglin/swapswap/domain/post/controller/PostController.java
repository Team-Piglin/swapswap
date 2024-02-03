package piglin.swapswap.domain.post.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.post.dto.request.PostCreateRequestDto;
import piglin.swapswap.domain.post.dto.request.PostUpdateRequestDto;
import piglin.swapswap.domain.post.dto.response.PostGetResponseDto;
import piglin.swapswap.domain.post.dto.response.PostSimpleResponseDto;
import piglin.swapswap.domain.post.service.PostService;
import piglin.swapswap.global.annotation.AuthMember;
import piglin.swapswap.global.annotation.HttpRequestLog;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @HttpRequestLog
    @PostMapping("/posts/write")
    public String createPost(
            @Valid @ModelAttribute PostCreateRequestDto requestDto,
            @AuthMember Member member
    ) {

        return "redirect:/posts/" + postService.createPost(member, requestDto);
    }

    @GetMapping("/posts/write")
    public String getPostWriteForm(
            Model model
    ) {

        model.addAttribute("PostCreateRequestDto",
                new PostCreateRequestDto(null, null, null, null, null));

        return "post/postWrite";
    }

    @GetMapping("/posts/{postId}")
    public String getPost(
            @PathVariable Long postId,
            @AuthMember Member member,
            Model model
    ) {

        PostGetResponseDto responseDto = postService.getPostWithFavorite(postId, member);

        model.addAttribute("postGetResponseDto", responseDto);
        model.addAttribute("postId", postId);

        String seeMemberNickname;
        if(member == null) {
            seeMemberNickname = "";
        } else {
            seeMemberNickname = member.getNickname();
        }

        model.addAttribute("isMemberLogged", member != null);
        model.addAttribute("isWriter", responseDto.author().equals(seeMemberNickname));

        return "post/post";
    }

    @GetMapping("/")
    public String getPostList(
            @RequestParam(required = false) LocalDateTime cursorTime,
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("postListResponseDto", postService.getPostList(member, cursorTime));
        model.addAttribute("isMemberLogged", member != null);

        return "post/postList";
    }

    @GetMapping("/posts/more")
    public String getPostListMore(
            @RequestParam(required = false) LocalDateTime cursorTime,
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("postListResponseDto", postService.getPostList(member, cursorTime));

        return "post/postListFragment";
    }

    @ResponseBody
    @PatchMapping("/posts/{postId}/favorite")
    public ResponseEntity<?> updatePostFavorite(
            @PathVariable Long postId,
            @AuthMember Member member
    ) {

        if (member == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        postService.updatePostFavorite(member, postId);

        return ResponseEntity.ok().build();
    }

    @HttpRequestLog
    @PutMapping("/posts/{postId}/write")
    public String updatePost(
            @PathVariable Long postId,
            @Valid @ModelAttribute PostUpdateRequestDto requestDto,
            @AuthMember Member member
    ) {

        postService.updatePost(postId, member, requestDto);

        return "redirect:/posts/" + postId;
    }

    @GetMapping("/posts/{postId}/write")
    public String getPostUpdateWriteForm(
            @PathVariable Long postId,
            @AuthMember Member member,
            Model model
    ) {

        postService.getPostUpdateWriteForm(member, postId);

        if (member == null) {
            return "redirect:/";
        }

        model.addAttribute("PostUpdateRequestDto",
                new PostUpdateRequestDto(null, null, null, null, null));

        model.addAttribute("PostId", postId);

        return "post/postUpdateWrite";
    }

    @ResponseBody
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long postId,
            @AuthMember Member member
    ) {

        postService.deletePost(member, postId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/search/posts")
    public String searchPost(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) LocalDateTime cursorTime,
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("postListResponseDto",
                postService.searchPost(title, category, city, member, cursorTime));
        model.addAttribute("isMemberLogged", member != null);

        return "post/postSearchList";
    }

    @GetMapping("/search/posts/more")
    public String searchPostMore(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) LocalDateTime cursorTime,
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("postListResponseDto", postService.searchPost(title, category, city, member,
                cursorTime));

        return "post/postListFragment";
    }

    @ResponseBody
    @PatchMapping("/posts/{postId}/up")
    public ResponseEntity<?> upPost(
            @PathVariable Long postId,
            @AuthMember Member member
    ) {

        postService.upPost(postId, member);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/member/{memberId}")
    public ResponseEntity<?> getPostListByMemberId(
            @PathVariable Long memberId
    ) {

        List<PostSimpleResponseDto> responseDtoList = postService.getPostSimpleInfoList(memberId);

        return ResponseEntity.ok(responseDtoList);
    }
}
