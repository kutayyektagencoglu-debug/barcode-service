package com.migros.barcodeservice.controller;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.enums.BarcodeType;
import com.migros.barcodeservice.service.BarcodeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/barcode-service")
public class BarcodeController {

    private final BarcodeService barcodeService;

    public BarcodeController(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    //CREATE
    @PostMapping
    public List<BarcodeResponseDTO> createBarcode(BarcodeRequestDTO barcodeRequestDTO) {
        return barcodeService.createBarcode(barcodeRequestDTO);
    }

    //READ ALL
    @GetMapping
    public List<BarcodeResponseDTO> getAllBarcodes() {
        return barcodeService.getAllBarcodes();
    }

    //READ BY CODE
    @GetMapping("/code/{code}")
    public BarcodeResponseDTO getBarcodeByCode(@PathVariable String code) {
        return barcodeService.getBarcodeByCode(code);
    }

    //READ BY PRODUCT CODE
    @GetMapping("/productCode/{productCode}")
    public List<BarcodeResponseDTO> getBarcodeByProductCode(@PathVariable String productCode) {
        return barcodeService.getBarcodeByProductCode(productCode);
    }

    //READ BY TYPE
    @GetMapping("/type/{type}")
    public List<BarcodeResponseDTO> getBarcodeByType(@PathVariable BarcodeType type) {
        return barcodeService.getBarcodeByType(type);
    }

    //DELETE
    @DeleteMapping("/productCode/{productCode}")
    public void deleteBarcode(@PathVariable String productCode) {
        barcodeService.deleteBarcode(productCode);
    }

}
