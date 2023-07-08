package uos.capstone.dms.domain.pet;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Breed {

    @Id
    @NonNull
    private Long id;
    @NonNull
    private String breedName;

    @Builder
    public Breed(String breedName) {
        this.breedName = breedName;
    }
}
