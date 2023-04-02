package uos.capstone.dms.domain.pet;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import uos.capstone.dms.domain.Image;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@ToString
public class PetImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    private PetDog petDog;
}
