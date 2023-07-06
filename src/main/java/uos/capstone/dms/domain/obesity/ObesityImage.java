package uos.capstone.dms.domain.obesity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uos.capstone.dms.domain.Image;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ObesityImage extends Image {

    @ManyToOne(fetch = FetchType.LAZY)
    private Obesity obesity;

    public void setObesity(Obesity obesity) {
        this.obesity = obesity;
    }
}
