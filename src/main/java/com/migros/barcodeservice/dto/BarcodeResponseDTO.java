package com.migros.barcodeservice.dto;

import com.migros.barcodeservice.enums.BarcodeType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BarcodeResponseDTO {
    private Long id;
    @Size(min = 4, max = 9)
    private int code;
    private BarcodeType type;
    private String productCode;
}
