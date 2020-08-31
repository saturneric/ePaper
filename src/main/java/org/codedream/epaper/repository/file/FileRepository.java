package org.codedream.epaper.repository.file;

import org.codedream.epaper.model.file.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    Iterable<File> findAllByHash(String hash);
}
