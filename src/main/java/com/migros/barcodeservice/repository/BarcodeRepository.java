package com.migros.barcodeservice.repository;

import com.migros.barcodeservice.enums.BarcodeType;
import com.migros.barcodeservice.model.Barcode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    Optional<Barcode> findByCode(String code);
    List<Barcode> findByProductCode(String productCode);
    List<Barcode> findByType(BarcodeType type);

    void deleteByProductCode(String productCode);
}
