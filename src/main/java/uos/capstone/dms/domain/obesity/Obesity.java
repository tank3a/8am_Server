package uos.capstone.dms.domain.obesity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uos.capstone.dms.domain.pet.PetDog;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Obesity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long obesityId;

    @ManyToOne(fetch = FetchType.LAZY)
    private PetDog petDog;

    @CreatedDate
    private LocalDate createdDate;
    private double weight;
    private int obesity;

    @OneToMany(mappedBy = "obesity", cascade = CascadeType.ALL)
    private List<ObesityImage> images;

    @OneToOne(mappedBy = "obesity", cascade = CascadeType.ALL)
    private ObesitySurvey survey;

    @Builder
    public Obesity(PetDog petDog, double weight, int obesity) {
        this.petDog = petDog;
        this.weight = weight;
        this.obesity = obesity;
    }

    public void setObesity(int obesity) {
        this.obesity = obesity;
    }
}
