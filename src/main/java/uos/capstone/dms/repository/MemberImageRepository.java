package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.user.MemberImage;

import java.util.Optional;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    void deleteById(Long id);
    Optional<MemberImage> findByUuid(String uuid);
}
