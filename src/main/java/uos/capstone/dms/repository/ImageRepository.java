package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
