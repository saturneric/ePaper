package org.codedream.epaper.repository.record;

import org.codedream.epaper.model.record.TaskRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRecordRepository extends JpaRepository<TaskRecord, Integer> {

}
