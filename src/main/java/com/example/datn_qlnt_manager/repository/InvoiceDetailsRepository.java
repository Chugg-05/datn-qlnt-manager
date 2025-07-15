package com.example.datn_qlnt_manager.repository;

import com.example.datn_qlnt_manager.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetail, String> {
    List<InvoiceDetail> findByInvoiceId(String invoiceId);

}
