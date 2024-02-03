package piglin.swapswap.domain.billpost.mapper;

import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.billpost.dto.BillPostResponseDto;
import piglin.swapswap.domain.billpost.entity.BillPost;
import piglin.swapswap.domain.post.entity.Post;

public class BillPostMapper {

    public static BillPost createBillPost(Bill bill, Post post) {

        return BillPost.builder()
                .bill(bill)
                .post(post)
                .build();
    }

    public static List<BillPostResponseDto> toBillPostResponseDto(List<Post> postList) {

        return postList.stream().map(post -> BillPostResponseDto.builder()
                .postId(post.getId())
                .postTile(post.getTitle())
                .imageUrl(post.getImageUrl().get(0).toString())
                .postStatus(post.getDealStatus())
                .build()).toList();
    }
}
