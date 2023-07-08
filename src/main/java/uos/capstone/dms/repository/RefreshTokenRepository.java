package uos.capstone.dms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uos.capstone.dms.domain.token.RefreshToken;


public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> { }
