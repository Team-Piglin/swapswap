package piglin.swapswap.domain.bill.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import piglin.swapswap.domain.bill.billfacade.BillFacade;
import piglin.swapswap.domain.bill.service.BillService;
import piglin.swapswap.domain.bill.dto.request.BillUpdateRequestDto;
import piglin.swapswap.domain.deal.service.DealService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.domain.member.service.MemberService;
import piglin.swapswap.global.annotation.AuthMember;
import piglin.swapswap.global.annotation.HttpRequestLog;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/bills")
public class BillController {

    private final BillFacade billFacade;
    private final BillService billService;
    private final DealService dealService;
    private final MemberService memberService;

    @HttpRequestLog
    @PatchMapping("/{billId}/swap-pay")
    public ResponseEntity<?> updateUseSwapPay(
            @PathVariable Long billId,
            @AuthMember Member member
    ) {

        billService.updateUsedSwapPay(billId, member);

        return ResponseEntity.ok("스왑페이 사용 등록 성공");
    }

    @HttpRequestLog
    @PatchMapping("/{billId}/allow/no-swap-pay")
    public ResponseEntity<?> updateAllowWithoutSwapPay(
            @PathVariable Long billId,
            @AuthMember Member member
    ) {

        billService.updateBillAllowWithoutSwapPay(billId, member);
        dealService.bothAllowThenChangeDealing(billId);

        return ResponseEntity.ok("거래 수락 성공");
    }

    @GetMapping("/{billId}/allow/swap-pay/true")
    public String showUpdateAllowWithSwapPayForm(
            @PathVariable Long billId,
            @AuthMember Member member,
            Model model
    ) {

        billService.initialCommission(billId, member); // TODO 수수료 부분 추후에 로직 꼭 수정해주세요
        model.addAttribute("myBill", billService.getMyBillDto(billId));
        model.addAttribute("mySwapMoney", memberService.getMySwapMoney(member));
        model.addAttribute("dealId", dealService.getDealIdByBillId(billId));

        return "deal/dealAllowWithSwapPay";
    }

    @HttpRequestLog
    @PatchMapping("/{billId}/allow/swap-pay")
    public ResponseEntity<?> updateAllowTrueWithSwapPay(
            @PathVariable Long billId,
            @AuthMember Member member
    ) {

        billFacade.updateBillAllowTrueWithSwapPay(billId, member);
        dealService.bothAllowThenChangeDealing(billId);

        return ResponseEntity.ok("결제 성공!");
    }

    @HttpRequestLog
    @PatchMapping("/{billId}/allow/swap-pay/false")
    public ResponseEntity<?> updateBillAllowFalseWithSwapPay(
            @PathVariable Long billId,
            @AuthMember Member member
    ) {

        billFacade.updateBillAllowFalseWithSwapPay(billId, member);

        return ResponseEntity.ok("결제 취소 성공!");
    }

    @GetMapping("/{billId}/member/{memberId}")
    public String showUpdateBillAndBillPostForm(
            @PathVariable Long billId,
            @PathVariable Long memberId,
            @AuthMember Member member,
            Model model
    ) {

        billFacade.validateUpdateBill(billId, member);
        model.addAttribute("memberId", memberId);
        model.addAttribute("dealUpdateRequestDto", new BillUpdateRequestDto(
                null, null));
        model.addAttribute("dealId", dealService.getDealIdByBillId(billId));

        return "deal/dealUpdateForm";
    }

    @HttpRequestLog
    @PutMapping("/{billId}/member/{memberId}")
    public ResponseEntity<?> updateBillAndBillPost(
            @PathVariable Long billId,
            @PathVariable Long memberId,
            @RequestBody @Valid BillUpdateRequestDto requestDto,
            @AuthMember Member member
    ){

        billFacade.updateBill(member, billId, memberId, requestDto);

        return ResponseEntity.ok().build();
    }

    @HttpRequestLog
    @ResponseBody
    @PatchMapping("/{billId}/take")
    public ResponseEntity<?> takeDeal(
            @PathVariable Long billId,
            @AuthMember Member member
    ) {

        billService.updateBillTake(billId, member);
        dealService.bothTakeThenChangeCompleted(billId);

        return ResponseEntity.ok().build();
    }
}
