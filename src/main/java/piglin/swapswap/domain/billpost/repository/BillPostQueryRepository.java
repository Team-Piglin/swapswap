package piglin.swapswap.domain.billpost.repository;

import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.post.entity.Post;

public interface BillPostQueryRepository {

    List<Post> findPostFromBillPostByBill(Bill bill);

    List<Long> findAllPostIdByBill(Bill bill);

    void deleteAllByBill(Bill bill);

}
