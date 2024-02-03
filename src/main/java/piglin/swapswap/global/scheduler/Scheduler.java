package piglin.swapswap.global.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {

//    private final MemberRepository memberRepository;
//    private final PostRepository postRepository;
//    private final FavoriteRepository favoriteRepository;
//    private final WalletRepository walletRepository;
//    private final MemberCouponRepository memberCouponRepository;
//    private final ChatRoomRepository chatRoomRepository;
//    private final WalletHistoryRepository walletHistoryRepository;
//    private final ApplicationEventPublisher applicationEventPublisher;
//
//    @Transactional
//    @Scheduled(cron = "0 0 0 * * *")
//    public void deleteExpiredWalletHistory() {
//
//        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);
//
//        walletHistoryRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//        memberCouponRepository.deleteAllByIsUsedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//        chatRoomRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//        favoriteRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//
//        List<String> postImageUrlListToDelete = new ArrayList<>();
//
//
//        List<Post> postListToDelete = postRepository.findByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//
//        for(Post post : postListToDelete) {
//
//            for (int i = 0; i < post.getImageUrl().size(); i++) {
//                postImageUrlListToDelete.add((String) post.getImageUrl().get(i));
//            }
//        }
//
//        applicationEventPublisher.publishEvent(new DeleteImageUrlListEvent(postImageUrlListToDelete));
//
//        postRepository.deleteAll(postListToDelete);
//
//        memberRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//
//        walletRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
//
//    }
    // TODO 탈퇴한 멤버 중요 정보 NULL 값으로 바꾸기
}
