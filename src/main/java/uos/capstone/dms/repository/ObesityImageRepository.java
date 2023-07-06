package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.obesity.Obesity;
import uos.capstone.dms.domain.obesity.ObesityImage;

import java.util.List;

public interface ObesityImageRepository extends JpaRepository<ObesityImage, Long> {

    List<ObesityImage> findAllByObesity(Obesity obesity);
}
