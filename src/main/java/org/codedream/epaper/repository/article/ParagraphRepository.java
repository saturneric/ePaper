package org.codedream.epaper.repository.article;

import org.codedream.epaper.model.article.Paragraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Integer> {
    Optional<Paragraph> findBySha512Hash(String hash);
}
