package org.codedream.epaper.repository.record;

import org.codedream.epaper.model.record.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Integer> {

}
