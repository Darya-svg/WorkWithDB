package com.example.workWithDB.repository;

import com.example.workWithDB.model.RecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordRepository extends JpaRepository<RecordModel, Long> {

}
