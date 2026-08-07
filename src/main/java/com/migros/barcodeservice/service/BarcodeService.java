package com.migros.barcodeservice.service;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.mapper.BarcodeMapper;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import org.springframework.stereotype.Service;

@Service
public class BarcodeService {
    private final BarcodeRepository barcodeRepository;
    private final BarcodeMapper mapper;

    public BarcodeService(BarcodeRepository barcodeRepository, BarcodeMapper mapper) {
        this.barcodeRepository = barcodeRepository;
        this.mapper = mapper;
    }

    //CREATE
    public BarcodeResponseDTO createBarcode(BarcodeRequestDTO dto) {
        Barcode barcode = mapper.toEntity(dto);
        String categoryCode = barcode.getProductCode().substring(0, 2);
        //Other barcode creation logic

        return mapper.toResponseDTO(barcode);
    }
}
