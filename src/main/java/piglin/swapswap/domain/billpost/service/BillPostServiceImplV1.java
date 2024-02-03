package piglin.swapswap.domain.billpost.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import piglin.swapswap.domain.bill.entity.Bill;
import piglin.swapswap.domain.billpost.dto.BillPostResponseDto;
import piglin.swapswap.domain.billpost.mapper.BillPostMapper;
import piglin.swapswap.domain.billpost.repository.BillPostRepository;
import piglin.swapswap.domain.deal.constant.DealStatus;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.post.service.PostService;
import piglin.swapswap.global.annotation.SwapLog;
import piglin.swapswap.global.exception.common.BusinessException;
import piglin.swapswap.global.exception.common.ErrorCode;
import piglin.swapswap.global.exception.post.AlreadyDealingPostException;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillPostServiceImplV1 implements BillPostService {

    private final PostService postService;
    private final BillPostRepository billPostRepository;

    @Override
    @SwapLog
    public void createBillPost(Bill bill, List<Long> postIdList) {

        log.info("createBillPost - billId: {}", bill.getId());
        for (Long postId : postIdList) {
            Post post = postService.getPost(postId);
            log.info("postId: {} | postDealStatus: {}", post.getId(), post.getDealStatus());

            checkPostDealStatus(post);

            billPostRepository.save(BillPostMapper.createBillPost(bill, post));
        }
    }

    public void checkPostDealStatus(Post post) {

        if (!post.getDealStatus().equals(DealStatus.REQUESTED)) {
            throw new AlreadyDealingPostException();
        }
    }

    @Override
    public List<BillPostResponseDto> getBillPostDtoList(Bill bill) {

        List<Post> postList = billPostRepository.findPostFromBillPostByBill(bill);

        return BillPostMapper.toBillPostResponseDto(postList);
    }

    @Override
    @SwapLog
    public void deleteAllByBill(Bill bill) {

        log.info("billId: {}", bill.getId());
        billPostRepository.deleteAllByBill(bill);
    }

    @Override
    public List<Long> getPostIdListByBill(Bill bill) {

        return billPostRepository.findAllPostIdByBill(bill);
    }

    @Override
    @SwapLog
    public void updatePostListDealStatusByBill(Bill bill) {

        log.info("billId: {} | billIsAllowed: {} | billIsTaken: {}", bill.getId(),
                bill.getIsAllowed(), bill.getIsTaked());
        List<Long> postIdList = getPostIdListByBill(bill);
        log.info("postIdList: {}", postIdList);

        if (bill.getIsAllowed() && !bill.getIsTaked()) {
            checkPostDealStatusInBill(bill);
            log.info("billPostListWillBe - DEALING");
            postService.updatePostStatusByPostIdList(postIdList, DealStatus.DEALING);
        }
        if (!bill.getIsAllowed()) {
            log.info("billPostListWillBe - REQUESTED");
            postService.updatePostStatusByPostIdList(postIdList, DealStatus.REQUESTED);
        }
        if (bill.getIsTaked()) {
            log.info("billPostListWillBe - COMPLETED");
            postService.updatePostStatusByPostIdList(postIdList, DealStatus.COMPLETED);
        }
    }

    private void checkPostDealStatusInBill(Bill bill) {

        List<BillPostResponseDto> billPostDtoList = getBillPostDtoList(bill);

        for (BillPostResponseDto billPostResponseDto : billPostDtoList) {
            log.info("postId: {} | postStatus: {}", billPostResponseDto.postId(), billPostResponseDto.postStatus());
            if (!billPostResponseDto.postStatus().equals(DealStatus.REQUESTED)) {
                throw new BusinessException(ErrorCode.CAN_NOT_UPDATE_POST_STATUS);
            }
        }
    }
}
