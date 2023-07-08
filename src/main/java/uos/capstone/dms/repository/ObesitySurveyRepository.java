package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.obesity.Obesity;
import uos.capstone.dms.domain.obesity.ObesitySurvey;

import java.util.Optional;

public interface ObesitySurveyRepository extends JpaRepository<ObesitySurvey, Long> {

    Optional<ObesitySurvey> findByObesity(Obesity obesity);
}
