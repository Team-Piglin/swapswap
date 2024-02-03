package piglin.swapswap.domain.bill.repository;

import static piglin.swapswap.domain.bill.entity.QBill.bill;
import static piglin.swapswap.domain.member.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import piglin.swapswap.domain.bill.entity.Bill;


public class BillQueryRepositoryImpl implements  BillQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public BillQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Optional<Bill> findByIdWithMember(Long billId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(bill)
                .join(bill.member, member)
                .where(billIdEq(billId))
                .fetchOne());
    }

    private BooleanExpression billIdEq(Long billId) {

        return bill.id.eq(billId);
    }
}
