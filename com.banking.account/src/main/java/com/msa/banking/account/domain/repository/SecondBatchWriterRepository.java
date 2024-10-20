package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.SecondBatchWriter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecondBatchWriterRepository extends JpaRepository<SecondBatchWriter, Long> {
}
