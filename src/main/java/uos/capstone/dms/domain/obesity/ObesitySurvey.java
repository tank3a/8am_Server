package uos.capstone.dms.domain.obesity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EntityListeners(AuditingEntityListener.class)
public class ObesitySurvey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyId;

    @OneToOne(fetch = FetchType.LAZY)
    private Obesity obesity;

    private int touchRip;
    private int seeRip;
    private int touchBelly;
    private int seeWeistUp;
    private int seeWeistSide;
    private int result;

    @Builder
    public ObesitySurvey(Obesity obesity, int touchRip, int seeRip, int touchBelly, int seeWeistUp, int seeWeistSide, int result) {
        this.obesity = obesity;
        this.touchRip = touchRip;
        this.seeRip = seeRip;
        this.touchBelly = touchBelly;
        this.seeWeistUp = seeWeistUp;
        this.seeWeistSide = seeWeistSide;
        this.result = result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setObesity(Obesity obesity) {
        this.obesity = obesity;
    }
}
