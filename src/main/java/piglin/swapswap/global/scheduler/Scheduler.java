package piglin.swapswap.global.scheduler;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import piglin.swapswap.domain.chatroom.mongorepository.ChatRoomRepository;
import piglin.swapswap.domain.favorite.repository.FavoriteRepository;
import piglin.swapswap.domain.member.repository.MemberRepository;
import piglin.swapswap.domain.membercoupon.repository.MemberCouponRepository;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.post.event.DeleteImageUrlListEvent;
import piglin.swapswap.domain.post.repository.PostRepository;
import piglin.swapswap.domain.wallet.repository.WalletRepository;
import piglin.swapswap.domain.wallethistory.repository.WalletHistoryRepository;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final FavoriteRepository favoriteRepository;
    private final WalletRepository walletRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final WalletHistoryRepository walletHistoryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void deleteExpiredWalletHistory() {

        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);

        walletHistoryRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
        memberCouponRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
        chatRoomRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);
        favoriteRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);

        List<String> postImageUrlListToDelete = new ArrayList<>();


        List<Post> postListToDelete = postRepository.findByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);

        for(Post post : postListToDelete) {

            for (int i = 0; i < post.getImageUrl().size(); i++) {
                postImageUrlListToDelete.add((String) post.getImageUrl().get(i));
            }
        }

        applicationEventPublisher.publishEvent(new DeleteImageUrlListEvent(postImageUrlListToDelete));

        postRepository.deleteAll(postListToDelete);

        memberRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);

        walletRepository.deleteAllByIsDeletedIsTrueAndModifiedTimeBefore(fourteenDaysAgo);

    }
}
