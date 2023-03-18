package uos.capstone.dms.domain.pet;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uos.capstone.dms.domain.Image;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BreedImage extends Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
