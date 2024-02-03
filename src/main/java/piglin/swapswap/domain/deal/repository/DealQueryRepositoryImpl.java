package piglin.swapswap.domain.deal.repository;

import static piglin.swapswap.domain.deal.entity.QDeal.deal;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import piglin.swapswap.domain.bill.entity.QBill;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.member.entity.QMember;

public class DealQueryRepositoryImpl implements DealQueryRepository {

    private final EntityManager em;

    private final JPAQueryFactory queryFactory;

    public DealQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Deal> findAllMyRequestDeal(Long memberId) {

        QBill requestMemberBill = new QBill("requestMemberBill");
        QBill receiveMemberBill = new QBill("receiveMemberBill");
        QMember requestMember = new QMember("requestMember");
        QMember receiveMember = new QMember("receiveMember");

        return queryFactory
                .selectFrom(deal)
                .join(deal.requestMemberbill, requestMemberBill).fetchJoin()
                .join(deal.receiveMemberbill, receiveMemberBill).fetchJoin()
                .join(requestMemberBill.member, requestMember).fetchJoin()
                .join(receiveMemberBill.member, receiveMember).fetchJoin()
                .where(requestMember.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<Deal> findAllMyReceiveDeal(Long memberId) {

        QBill requestMemberBill = new QBill("requestMemberBill");
        QBill receiveMemberBill = new QBill("receiveMemberBill");
        QMember requestMember = new QMember("requestMember");
        QMember receiveMember = new QMember("receiveMember");

        return  queryFactory
                .selectFrom(deal)
                .join(deal.requestMemberbill, requestMemberBill).fetchJoin()
                .join(deal.receiveMemberbill, receiveMemberBill).fetchJoin()
                .join(requestMemberBill.member, requestMember).fetchJoin()
                .join(receiveMemberBill.member, receiveMember).fetchJoin()
                .where(receiveMember.id.eq(memberId))
                .fetch();
    }

    @Override
    public List<Deal> findAllMyDeal(Long memberId) {

        QBill requestMemberBill = new QBill("requestMemberBill");
        QBill receiveMemberBill = new QBill("receiveMemberBill");
        QMember requestMember = new QMember("requestMember");
        QMember receiveMember = new QMember("receiveMember");

        return queryFactory
                .selectFrom(deal)
                .join(deal.requestMemberbill, requestMemberBill).fetchJoin()
                .join(deal.receiveMemberbill, receiveMemberBill).fetchJoin()
                .join(requestMemberBill.member, requestMember).fetchJoin()
                .join(receiveMemberBill.member, receiveMember).fetchJoin()
                .where(
                        requestMember.id.eq(memberId)
                        .or(receiveMember.id.eq(memberId))
                )
                .fetch();
    }

    @Override
    public Optional<Deal> findDealByIdWithBillAndMember(Long dealId) {

        QBill requestMemberBill = new QBill("requestMemberBill");
        QBill receiveMemberBill = new QBill("receiveMemberBill");
        QMember requestMember = new QMember("requestMember");
        QMember receiveMember = new QMember("receiveMember");

        return Optional.ofNullable(queryFactory
                .selectFrom(deal)
                .where(deal.id.eq(dealId))
                .join(deal.requestMemberbill, requestMemberBill).fetchJoin()
                .join(deal.receiveMemberbill, receiveMemberBill).fetchJoin()
                .join(requestMemberBill.member, requestMember).fetchJoin()
                .join(receiveMemberBill.member, receiveMember).fetchJoin()
                .fetchOne());
    }

    @Override
    public Optional<Deal> findDealByBillId(Long billId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(deal)
                .where(billIdEq(billId))
                .fetchOne());
    }

    @Override
    public Optional<Deal> findByBillIdWithBillAndMember(Long billId) {

        QBill requestMemberBill = new QBill("requestMemberBill");
        QBill receiveMemberBill = new QBill("receiveMemberBill");
        QMember requestMember = new QMember("requestMember");
        QMember receiveMember = new QMember("receiveMember");

        return Optional.ofNullable(queryFactory
                .selectFrom(deal)
                .where(billIdEq(billId))
                .join(deal.requestMemberbill, requestMemberBill).fetchJoin()
                .join(deal.receiveMemberbill, receiveMemberBill).fetchJoin()
                .join(requestMemberBill.member, requestMember).fetchJoin()
                .join(receiveMemberBill.member, receiveMember).fetchJoin()
                .fetchFirst());
    }

    @Override
    public Optional<Deal> findByBillIdWithBill(Long billId) {

        QBill requestMemberBill = new QBill("requestMemberBill");
        QBill receiveMemberBill = new QBill("receiveMemberBill");

        return Optional.ofNullable(queryFactory.select(deal)
                .from(deal)
                .where(billIdEq(billId))
                .join(deal.requestMemberbill, requestMemberBill).fetchJoin()
                .join(deal.receiveMemberbill, receiveMemberBill).fetchJoin()
                .fetchFirst());
    }

    private BooleanExpression billIdEq(Long billId) {

        return deal.requestMemberbill.id.eq(billId).or(deal.receiveMemberbill.id.eq(billId));
    }
}
