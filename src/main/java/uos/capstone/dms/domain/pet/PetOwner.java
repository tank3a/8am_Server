package uos.capstone.dms.domain.pet;

import jakarta.persistence.*;
import lombok.*;
import uos.capstone.dms.domain.user.Member;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = {"member", "petDog"})
@IdClass(PetOwnerId.class)
public class PetOwner {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @NonNull
    private Member member;

    @Id
    @ManyToOne(cascade = CascadeType.PERSIST)
    @NonNull
    private PetDog petDog;

    private LocalDateTime expireDateTime;
    private boolean isOwner;
}
