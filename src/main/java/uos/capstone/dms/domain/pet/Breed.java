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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String breedName;
    @OneToOne
    private BreedImage image;

    @Builder
    public Breed(String breedName, BreedImage image) {
        this.breedName = breedName;
        this.image = image;
    }
}
