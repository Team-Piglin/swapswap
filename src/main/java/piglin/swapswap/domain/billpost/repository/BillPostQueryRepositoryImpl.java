package piglin.swapswap.domain.billpost.repository;

import static piglin.swapswap.domain.billpost.entity.QBillPost.billPost;
import static piglin.swapswap.domain.post.entity.QPost.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.post.entity.Post;

public class BillPostQueryRepositoryImpl implements BillPostQueryRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public BillPostQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> findPostFromBillPostByBill(Bill bill) {

        return queryFactory
                .select(post)
                .from(billPost)
                .where(billPost.bill.eq(bill))
                .fetch();
    }

    @Override
    public List<Long> findAllPostIdByBill(Bill bill) {

        return queryFactory
                .select(post.id)
                .from(billPost)
                .where(billPost.bill.eq(bill))
                .fetch();
    }

    @Override
    public void deleteAllByBill(Bill bill) {

        queryFactory
                .delete(billPost)
                .where(billEq(bill))
                .execute();

        em.flush();
        em.clear();
    }

    private BooleanExpression billEq(Bill bill) {

        return billPost.bill.eq(bill);
    }
}
