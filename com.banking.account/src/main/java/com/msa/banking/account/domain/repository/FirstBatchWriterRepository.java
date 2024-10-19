package com.msa.banking.account.domain.repository;

import com.msa.banking.account.domain.model.FirstBatchWriter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FirstBatchWriterRepository extends JpaRepository<FirstBatchWriter, Long> {

}
