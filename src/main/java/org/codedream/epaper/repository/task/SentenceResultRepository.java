package org.codedream.epaper.repository.task;

import org.codedream.epaper.model.task.SentenceResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SentenceResultRepository extends JpaRepository<SentenceResult, Integer> {
    Optional<SentenceResult> findBySentenceId(Integer sentenceId);
}
