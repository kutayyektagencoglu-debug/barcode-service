package com.migros.barcodeservice.repository;

import com.migros.barcodeservice.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BarcodeSequenceRepository extends JpaRepository<Barcode, Long> {

    @Query(value = "SELECT NEXTVAL('product_barcode_sequence')", nativeQuery = true)
    Long nextProductBarcode();

    @Query(value = "SELECT NEXTVAL('scale_barcode_sequence')", nativeQuery = true)
    Long nextScaleBarcode();

    @Query(value = "SELECT NEXTVAL('register_barcode_sequence')", nativeQuery = true)
    Long nextRegisterBarcode();
}