package piglin.swapswap.domain.deal.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseBody;
import piglin.swapswap.domain.deal.dto.request.DealCreateRequestDto;
import piglin.swapswap.domain.deal.dto.response.DealDetailResponseDto;
import piglin.swapswap.domain.deal.service.DealService;
import piglin.swapswap.domain.member.entity.Member;
import piglin.swapswap.global.annotation.AuthMember;
import piglin.swapswap.global.annotation.HttpRequestLog;

@Controller
@RequiredArgsConstructor
@RequestMapping("/deals")
public class DealController {

    private final DealService dealService;

    @GetMapping("/request")
    public String createDealForm(
            @RequestParam Long receiveMemberId,
            @RequestParam String memberName,
            @AuthMember Member member,
            Model model
    ) {

            dealService.isDifferentMember(member, receiveMemberId);

        model.addAttribute("receiveMemberId", receiveMemberId);
        model.addAttribute("receiveMemberName", memberName);
        model.addAttribute("memberId", member.getId());
        model.addAttribute("dealCreateRequestDto", new DealCreateRequestDto(
                null, null, null, null, null));

        return "deal/dealCreateForm";
    }

    @HttpRequestLog
    @ResponseBody
    @PostMapping
    public ResponseEntity<?> createDeal(
            @AuthMember Member member,
            @Valid @RequestBody DealCreateRequestDto requestDto
    ) {

        Long dealId = dealService.createDeal(member, requestDto);

        return ResponseEntity.ok().body(dealId);
    }

    @GetMapping("/request/list")
    public String getRequestDealList(
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("dealGetListResponseDto",
                dealService.getMyRequestDealList(member.getId()));
        model.addAttribute("memberNickname", member.getNickname());

        return "deal/dealRequestDealListForm";
    }

    @GetMapping("response/list")
    public String getResponseDealList(
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("dealGetListResponseDto",
                dealService.getMyReceiveDealList(member.getId()));
        model.addAttribute("memberNickname", member.getNickname());

        return "deal/dealResponseDealListForm";
    }

    @GetMapping("/{dealId}")
    public String getDeal(
            @PathVariable Long dealId,
            @AuthMember Member member,
            Model model
    ) {

        DealDetailResponseDto responseDto = dealService.getDeal(dealId, member);

        model.addAttribute("dealDetailResponseDto", responseDto);
        model.addAttribute("memberId", member.getId());

        return "deal/dealRequestDeal";
    }

    @GetMapping("/history")
    public String getDealHistory(
            @AuthMember Member member,
            Model model
    ) {

        model.addAttribute("dealHistoryResponseDto", dealService.getDealHistoryList(member.getId()));
        model.addAttribute("memberNickname", member.getNickname());

        return "deal/dealHistory";
    }
}
