package piglin.swapswap.domain.bill.service;

import java.util.List;
import piglin.swapswap.domain.bill.dto.response.BillSimpleResponseDto;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.bill.dto.request.BillUpdateRequestDto;
import piglin.swapswap.domain.deal.entity.Deal;
import piglin.swapswap.domain.member.entity.Member;

public interface BillService {

    Bill createBill(Member member, Long extraFee, List<Long> postIdList);

    BillSimpleResponseDto getMyBillDto(Long billId);

    Bill getMyBill(Long billId);

    void updateBillAllowWithoutSwapPay(Long billId, Member member);

    void updateBillAllowTrueWithSwapPay(Long billId, Member member);

    void updateBillAllowFalseWithSwapPay(Long billId, Member member);

    void updateUsedSwapPay(Long billId, Member member);

    void initialCommission(Long billId, Member member);

    void validateUpdateBill(Deal deal, Long billId, Member member);

    void updateBill(Member member, Long billId, Long memberId, BillUpdateRequestDto requestDto);

    Long getTotalFee(Long billId);

    void updateBillTake(Long billId, Member member);
}
