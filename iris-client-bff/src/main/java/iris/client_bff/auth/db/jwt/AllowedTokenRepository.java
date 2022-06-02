package iris.client_bff.auth.db.jwt;

import java.time.Instant;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(
		value = "security.auth",
		havingValue = "db")
interface AllowedTokenRepository extends JpaRepository<AllowedToken, String> {

	Optional<AllowedToken> findByJwtTokenDigest(String token);

	@Transactional
	void deleteByUserName(String userName);

	@Transactional
	void deleteByExpirationTimeBefore(Instant expirationTime);
}
