package com.example.workWithDB.repository;

import com.example.workWithDB.model.ReversedRecordModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReversedRecordRepository extends JpaRepository<ReversedRecordModel, Long> {

}
