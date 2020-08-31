package org.codedream.epaper.repository.article;

import io.swagger.models.auth.In;
import org.codedream.epaper.model.article.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Integer> {
    boolean existsByText(String text);
    Optional<Phrase> findByText(String text);
}
