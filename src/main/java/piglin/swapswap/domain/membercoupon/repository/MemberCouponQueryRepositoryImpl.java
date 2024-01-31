package piglin.swapswap.domain.membercoupon.repository;

import static piglin.swapswap.domain.membercoupon.entity.QMemberCoupon.memberCoupon;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import piglin.swapswap.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Optional;
import piglin.swapswap.domain.member.entity.QMember;
import piglin.swapswap.domain.membercoupon.entity.MemberCoupon;
import piglin.swapswap.domain.membercoupon.entity.QMemberCoupon;

public class MemberCouponQueryRepositoryImpl implements MemberCouponQueryRepository{

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public MemberCouponQueryRepositoryImpl(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public void deleteAllMemberCouponByMember(Member loginMember){
        queryFactory
                .update(memberCoupon)
                .set(memberCoupon.isDeleted, true)
                .where(memberCoupon.member.eq(loginMember))
                .execute();

        em.flush();
        em.clear();
    }

    @Override
    public void reRegisterCouponByMember(Member loginMember) {
        queryFactory
                .update(memberCoupon)
                .set(memberCoupon.isDeleted, false)
                .where(memberCoupon.member.eq(loginMember))
                .execute();

        em.flush();
        em.clear();
    }
    public Optional<MemberCoupon> findByIdAndIsUserIsFalseWithMember(Long memberCouponId) {

        return Optional.ofNullable(queryFactory
                .selectFrom(QMemberCoupon.memberCoupon)
                .join(QMemberCoupon.memberCoupon.member, QMember.member).fetchJoin()
                .where(memberCouponIdEq(memberCouponId))
                .fetchOne());
    }

    private BooleanExpression memberCouponIdEq(Long memberCouponId) {

        return QMemberCoupon.memberCoupon.id.eq(memberCouponId);
    }
}
