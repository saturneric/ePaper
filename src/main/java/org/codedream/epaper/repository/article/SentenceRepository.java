package org.codedream.epaper.repository.article;

import io.swagger.models.auth.In;
import org.codedream.epaper.model.article.Sentence;
import org.python.antlr.ast.Str;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Integer> {
    Optional<Sentence> findBySha512Hash(String hash);
}
