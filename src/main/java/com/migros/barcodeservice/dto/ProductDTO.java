package com.migros.barcodeservice.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private String code;
    private String brand;
    private String unit;
    private String categoryCode;
}
