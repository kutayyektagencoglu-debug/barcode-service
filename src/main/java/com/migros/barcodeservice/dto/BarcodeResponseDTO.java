package com.migros.barcodeservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("barcodeId")
    private Long id;
    @Size(min = 4, max = 9)
    @JsonProperty("barcodeCode")
    private String code;
    @JsonProperty("barcodeType")
    private BarcodeType type;
    private String productCode;
}
