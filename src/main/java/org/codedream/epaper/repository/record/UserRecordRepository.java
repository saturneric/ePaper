package org.codedream.epaper.repository.record;

import org.codedream.epaper.model.record.UserRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRecordRepository extends JpaRepository<UserRecord, Integer> {

}
