package piglin.swapswap.domain.coupon.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import piglin.swapswap.domain.coupon.constant.CouponType;
import piglin.swapswap.domain.coupon.dto.request.CouponCreateRequestDto;
import piglin.swapswap.domain.coupon.dto.response.CouponGetResponseDto;
import piglin.swapswap.domain.coupon.service.CouponService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.global.annotation.AuthMember;

@Controller
@RequiredArgsConstructor
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public String createCouponForm(Model model) {

        model.addAttribute("couponCreateRequestDto", new CouponCreateRequestDto(
                null, null, 0, null, 0));
        model.addAttribute("couponType", CouponType.values());

        return "coupon/createCouponForm";
    }

    @PostMapping
    public String createCoupon(
            @Valid @ModelAttribute("couponCreateRequestDto") CouponCreateRequestDto couponCreateRequestDto,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "coupon/createCouponForm";
        }

        try {
            Long couponId = couponService.createCoupon(couponCreateRequestDto);
            redirectAttributes.addAttribute("couponId", couponId);
        } catch (Exception e) {
            bindingResult.reject("쿠폰 등록 중 에러가 발생하였습니다.");
            return "coupon/createCouponForm";
        }

        return "redirect:/coupons/{couponId}";
    }

    @GetMapping("/{couponId}")
    public String getCouponDetail(@PathVariable Long couponId, Model model) {

        CouponGetResponseDto couponGetResponseDto = couponService.getCouponDetail(couponId);
        model.addAttribute("couponGetResponseDto", couponGetResponseDto);

        return "coupon/couponDetail";
    }

    @GetMapping("/{couponId}/event")
    public String couponEvent(@PathVariable Long couponId, Model model) {

        int couponCount = couponService.getCouponCount(couponId);

        model.addAttribute("couponCount", couponCount);

        return "coupon/issueEventCoupon";
    }

    @ResponseBody
    @PostMapping("/{couponId}/event")
    public ResponseEntity<?> issueEventCoupon(@PathVariable Long couponId, @AuthMember Member member) {

        couponService.issueEventCouponByPessimisticLock(couponId, member);

        return ResponseEntity.ok("쿠폰 발급 성공");
    }

}
