package com.migros.barcodeservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Long productId;
    private String productName;
    private String productCode;
    private String brand;
    private String unit;
    private String categoryCode;
}
