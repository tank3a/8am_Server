package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.user.MemberImage;

public interface MemberImageRepository extends JpaRepository<MemberImage, Long> {
    void deleteById(Long id);
}
