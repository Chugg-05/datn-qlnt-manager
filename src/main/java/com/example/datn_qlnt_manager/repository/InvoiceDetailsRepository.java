package com.example.datn_qlnt_manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.datn_qlnt_manager.entity.InvoiceDetail;

@Repository
public interface InvoiceDetailsRepository extends JpaRepository<InvoiceDetail, String> {
    List<InvoiceDetail> findByInvoiceId(String invoiceId);

    @Query(
            """
	SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
	FROM InvoiceDetail d
	JOIN d.invoice i
	JOIN i.contract c
	JOIN c.room r
	WHERE d.oldIndex = :oldIndex
	AND d.newIndex = :newIndex
	AND i.month = :month
	AND i.year = :year
	AND r.id = (
		SELECT m.room.id FROM Meter m WHERE m.id = :meterId
	)
""")
    boolean existsByOldIndexAndNewIndexAndMonthAndYearAndMeterId(
            @Param("oldIndex") int oldIndex,
            @Param("newIndex") int newIndex,
            @Param("month") int month,
            @Param("year") int year,
            @Param("meterId") String meterId);
}
