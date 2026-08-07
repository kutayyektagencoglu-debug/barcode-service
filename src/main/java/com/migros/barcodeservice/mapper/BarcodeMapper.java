package com.migros.barcodeservice.mapper;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.model.Barcode;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BarcodeMapper {
    BarcodeResponseDTO toResponseDTO(Barcode barcode);
    Barcode toEntity(BarcodeRequestDTO requestDTO);
    List<BarcodeResponseDTO> toResponseDTOList(List<Barcode> barcodes);
}
