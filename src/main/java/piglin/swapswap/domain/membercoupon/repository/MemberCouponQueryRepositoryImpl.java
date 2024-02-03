package piglin.swapswap.domain.membercoupon.repository;

import static piglin.swapswap.domain.coupon.entity.QCoupon.coupon;
import static piglin.swapswap.domain.member.entity.QMember.member;
import static piglin.swapswap.domain.membercoupon.entity.QMemberCoupon.memberCoupon;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import piglin.swapswap.domain.coupon.entity.QCoupon;
import piglin.swapswap.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;
import java.util.Optional;

import piglin.swapswap.domain.member.entity.QMember;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;
import piglin.swapswap.domain.membercoupon.entity.QMemberCoupon;

public class MemberCouponQueryRepositoryImpl implements MemberCouponQueryRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberCouponQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteAllMemberCouponByMember(Member loginMember) {
        queryFactory
                .update(memberCoupon)
                .set(memberCoupon.isUsed, true)
                .where(memberCoupon.member.eq(loginMember))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public void reRegisterCouponByMember(Member loginMember) {
        queryFactory
                .update(memberCoupon)
                .set(memberCoupon.isUsed, false)
                .where(memberCoupon.member.eq(loginMember))
                .execute();

        em.flush();
        em.clear();
    }

    public Optional<MemberCoupon> findByIdAndIsUsedIsFalseWithMember(Long memberCouponId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.member, member).fetchJoin()
                .where(memberCouponIdEq(memberCouponId).and(isUsedIsFalse()))
                .fetchOne());
    }

    @Override
    public Optional<MemberCoupon> findByMemberCouponIdAndIsUsedIsFalseWithCoupon(Long memberCouponId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.coupon, coupon).fetchJoin()
                .where(memberCouponIdEq(memberCouponId).and(isUsedIsFalse()))
                .fetchOne());
    }

    @Override
    public Optional<MemberCoupon> findByIdAndIsUsedIsFalseWithCoupon(Long memberCouponId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.coupon, QCoupon.coupon).fetchJoin()
                .where(memberCouponIdEq(memberCouponId).and(isUsedIsFalse()))
                .fetchOne());
    }

    @Override
    public Optional<MemberCoupon> findByIdAndIsUsedIsFalseWithCouponAndMember(Long memberCouponId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.coupon, coupon).fetchJoin()
                .join(memberCoupon.member, member).fetchJoin()
                .where(memberCouponIdEq(memberCouponId).and(isUsedIsFalse()))
                .fetchOne());
    }

    @Override
    public Long countByCouponId(Long couponId) {
        return queryFactory
                .select(memberCoupon.count())
                .from(memberCoupon)
                .where(couponIdEq(couponId))
                .fetchOne();
    }

    @Override
    public List<MemberCoupon> findAllByMemberIdAndIsUsedIsFalseWithCoupon(Long memberId) {
        return queryFactory
                .selectFrom(memberCoupon)
                .join(memberCoupon.coupon).fetchJoin()
                .where(memberIdEq(memberId).and(isUsedIsFalse()))
                .fetch();
    }

    private BooleanExpression memberCouponIdEq(Long memberCouponId) {

        return memberCoupon.id.eq(memberCouponId);
    }

    private BooleanExpression couponIdEq(Long couponId) {

        return memberCoupon.coupon.id.eq(couponId);
    }

    private BooleanExpression isUsedIsFalse() {
        return memberCoupon.isUsed.eq(false);
    }

    private BooleanExpression memberIdEq(Long memberId) {

        return memberCoupon.member.id.eq(memberId);
    }
}

