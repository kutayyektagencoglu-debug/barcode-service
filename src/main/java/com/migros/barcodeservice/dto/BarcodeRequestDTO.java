package com.migros.barcodeservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BarcodeRequestDTO {
    @NotBlank
    @Size(min = 5, max = 5)
    private String productCode;
    @NotBlank
    private String unit;
}
