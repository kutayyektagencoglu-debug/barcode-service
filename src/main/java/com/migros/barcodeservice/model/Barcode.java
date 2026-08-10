package com.migros.barcodeservice.model;

import com.migros.barcodeservice.enums.BarcodeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Barcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BarcodeType type;

    @Column(nullable = false, length = 5)
    private String productCode;
}
