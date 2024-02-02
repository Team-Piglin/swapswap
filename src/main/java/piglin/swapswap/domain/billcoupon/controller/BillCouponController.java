package piglin.swapswap.domain.billcoupon.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import piglin.swapswap.domain.billcoupon.service.BillCouponService;
import piglin.swapswap.domain.billcoupon.dto.RedeemCouponRequestDto;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.membercoupon.dto.response.MyCouponGetResponseDto;
import piglin.swapswap.domain.membercoupon.service.MemberCouponService;
import piglin.swapswap.global.annotation.AuthMember;
import piglin.swapswap.global.annotation.HttpRequestLog;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bills/coupons")
public class BillCouponController {

    private final BillCouponService billCouponService;
    private final MemberCouponService memberCouponService;

    @GetMapping("/{billId}")
    public String showMyCouponList(
            @PathVariable Long billId,
            @RequestParam Long dealId,
            @AuthMember Member member, Model model
    ) {

        billCouponService.initialBillCouponList(billId);
        List<MyCouponGetResponseDto> myCouponDtoList = memberCouponService.getMycouponList(member);

        model.addAttribute("dealId", dealId);
        model.addAttribute("myCouponDtoList", myCouponDtoList);
        model.addAttribute("billId",billId);

        return "deal/dealCouponList";
    }

    @HttpRequestLog
    @PostMapping("/{billId}")
    public ResponseEntity<?> redeemCoupons(
            @PathVariable Long billId,
            @AuthMember Member member,
            @Valid @RequestBody RedeemCouponRequestDto dealRedeemCouponRequestDto
    ) {

        billCouponService.redeemCoupons(billId, member, dealRedeemCouponRequestDto);

        return ResponseEntity.ok("쿠폰 사용 성공");
    }
}
