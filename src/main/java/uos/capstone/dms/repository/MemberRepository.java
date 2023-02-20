package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.user.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEntityId(Long entityId);
    Optional<Member> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
