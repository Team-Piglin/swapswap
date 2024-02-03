package piglin.swapswap.domain.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import piglin.swapswap.domain.coupon.service.CouponService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.global.annotation.AuthMember;
import piglin.swapswap.global.annotation.HttpRequestLog;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    static final Long EVENT_COUPON_ID = 1L;

    private final CouponService couponService;

    @GetMapping("/event")
    public String couponEvent(Model model) {

        int couponCount = couponService.getCouponCount(EVENT_COUPON_ID);

        model.addAttribute("couponCount", couponCount);

        return "coupon/issueEventCoupon";
    }

    @ResponseBody
    @HttpRequestLog
    @PostMapping("/event")
    public ResponseEntity<?> issueEventCoupon(@AuthMember Member member) {

        couponService.issueEventCoupon(EVENT_COUPON_ID, member);

        return ResponseEntity.ok("쿠폰 발급 성공");
    }

}
