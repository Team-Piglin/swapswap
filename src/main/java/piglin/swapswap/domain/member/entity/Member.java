package piglin.swapswap.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import piglin.swapswap.domain.post.entity.Post;
import piglin.swapswap.domain.member.constant.MemberRoleEnum;
import piglin.swapswap.domain.wallet.entity.Wallet;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MemberRoleEnum role;

    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "member")
    private List<Post> postList;

    @JoinColumn(name = "wallet_id")
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    private Wallet wallet;

    @Column
    @LastModifiedDate
    private LocalDateTime modifiedDate;

    public void updateMember(String nickname) {
        this.nickname = nickname;
        this.modifiedDate = LocalDateTime.now();
    }

    public void deleteMember() {
        isDeleted = true;
        modifiedDate = LocalDateTime.now();
    }

    public void reRegisterMember() {
        isDeleted = false;
        modifiedDate = null;
    }
}
