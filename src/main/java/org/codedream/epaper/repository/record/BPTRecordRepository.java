package org.codedream.epaper.repository.record;

import org.codedream.epaper.model.record.BPTRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BPTRecordRepository extends JpaRepository<BPTRecord, Integer> {

}
