package com.migros.barcodeservice.service;

import com.migros.barcodeservice.client.ProductClient;
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
    private final ProductClient productClient;

    public BarcodeService(BarcodeRepository barcodeRepository, BarcodeMapper mapper, ProductClient productClient) {
        this.barcodeRepository = barcodeRepository;
        this.mapper = mapper;
        this.productClient = productClient;
    }

    //CREATE
    public BarcodeResponseDTO createBarcode(BarcodeRequestDTO dto) {
        Barcode barcode = mapper.toEntity(dto);
        String categoryCode = barcode.getProductCode().substring(0, 2);
        //Other barcode creation logic

        return mapper.toResponseDTO(barcode);
    }
}
