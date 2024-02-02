package piglin.swapswap.domain.billpost.service;

import java.util.List;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.billpost.dto.BillPostResponseDto;

public interface BillPostService {

    void createBillPost(Bill bill, List<Long> postIdList);

    List<BillPostResponseDto> getBillPostDtoList(Bill bill);

    void deleteAllByBill(Bill bill);

    List<Long> getPostIdListByBill(Bill bill);

    void updatePostListDealStatusByBill(Bill bill);
}
