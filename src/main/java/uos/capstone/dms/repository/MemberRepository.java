package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.capstone.dms.domain.user.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);
    Optional<Member> findByEmail(String email);
    boolean existsByUserId(String userId);
}
