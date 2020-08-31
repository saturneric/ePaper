package org.codedream.epaper.repository.server;

import org.codedream.epaper.model.server.ChildServerPassport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChildServerPassportRepository extends JpaRepository<ChildServerPassport, Integer> {
    Optional<ChildServerPassport> findByIdentityCode(String identityCode);
    Iterable<ChildServerPassport> findByExpired(boolean expired);
}
