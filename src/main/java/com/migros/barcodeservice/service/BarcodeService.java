package com.migros.barcodeservice.service;

import com.migros.barcodeservice.client.ProductClient;
import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.dto.ProductDTO;
import com.migros.barcodeservice.enums.BarcodeType;
import com.migros.barcodeservice.mapper.BarcodeMapper;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import com.migros.barcodeservice.repository.BarcodeSequenceRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BarcodeService {
    private final BarcodeRepository barcodeRepository;
    private final BarcodeSequenceRepository barcodeSequenceRepository;
    private final BarcodeMapper mapper;
    private final ProductClient productClient;

    public BarcodeService(BarcodeRepository barcodeRepository,BarcodeSequenceRepository barcodeSequenceRepository,
                          BarcodeMapper mapper, ProductClient productClient) {
        this.barcodeRepository = barcodeRepository;
        this.barcodeSequenceRepository = barcodeSequenceRepository;
        this.mapper = mapper;
        this.productClient = productClient;
    }

    //CREATE
    public List<BarcodeResponseDTO> createBarcode(BarcodeRequestDTO barcodeDTO) {
        ProductDTO productDTO = productClient.getProductByCode(barcodeDTO.getProductCode());
        Barcode barcode = mapper.toEntity(barcodeDTO);
        List<Barcode> barcodeList = new ArrayList<>();

        String categoryCode = productDTO.getCategoryCode();
        String unit = productDTO.getUnit();
        switch (categoryCode) {
            case "MY":
                if(unit.equals("KILOGRAM")) {
                    barcodeList.add(createProductBarcode(barcode));
                    barcodeList.add(createRegisterBarcode(barcode));
                } else {
                    barcodeList.add(createProductBarcode(barcode));
                }
                break;
            case "BL":
                if(unit.equals("KILOGRAM")) {
                    barcodeList.add(createProductBarcode(barcode));
                    barcodeList.add(createScaleBarcode(barcode));
                } else if(unit.equals("NUMBER")) {
                    barcodeList.add(createRegisterBarcode(barcode));
                } else {
                    barcodeList.add(createProductBarcode(barcode));
                }
                break;
            case "MT":
                barcodeList.add(createScaleBarcode(barcode));
                break;
            default:
                barcodeList.add(createProductBarcode(barcode));
        }
        return mapper.toResponseDTOList(barcodeList);
    }

    public Barcode createProductBarcode(Barcode barcode) {
        barcode.setType(BarcodeType.PRODUCT);

        Long sequence = barcodeSequenceRepository.nextProductBarcode();
        if(sequence > 999999999) {
            throw new IllegalStateException("No product barcodes remaining");
        }
        String code = String.format("%09d", sequence);
        barcode.setCode(code);
        return barcode;
    }

    public Barcode createRegisterBarcode(Barcode barcode) {
        barcode.setType(BarcodeType.REGISTER);
        Long sequence = barcodeSequenceRepository.nextProductBarcode();
        if(sequence > 9999) {
            throw new IllegalStateException("No register barcodes remaining");
        }
        String code = String.format("%04d", sequence);
        barcode.setCode(code);
        return barcode;
    }

    public Barcode createScaleBarcode(Barcode barcode) {
        barcode.setType(BarcodeType.SCALE);
        Long sequence = barcodeSequenceRepository.nextProductBarcode();
        if(sequence > 999) {
            throw new IllegalStateException("No scale barcodes remaining");
        }
        String codeEnd = String.format("%03d", sequence);
        String codeStart = barcode.getProductCode();
        String code = codeStart + codeEnd;
        barcode.setCode(code);
        return barcode;
    }

    //READ ALL
    public List<BarcodeResponseDTO> getAllBarcodes(){
        List<Barcode> barcodes = barcodeRepository.findAll();
        return mapper.toResponseDTOList(barcodes);
    }

    //READ BY CODE
    public BarcodeResponseDTO getBarcodeByCode(String code){
        Barcode barcode = barcodeRepository.findByCode(code)
                .orElseThrow(() ->  new IllegalArgumentException("Barcode not found: " + code));
        return mapper.toResponseDTO(barcode);
    }

    //READ BY PRODUCT CODE
    public List<BarcodeResponseDTO> getBarcodeByProductCode(String productCode) {
        List<Barcode> barcodes = barcodeRepository.findByProductCode(productCode);
        if(barcodes.isEmpty()) {
            throw new IllegalArgumentException("Barcode not found: " + productCode);
        }
        return mapper.toResponseDTOList(barcodes);
    }

    //READ BY TYPE
    public List<BarcodeResponseDTO> getBarcodeByType(BarcodeType type) {
        List<Barcode> barcodes = barcodeRepository.findByType(type);
        if(barcodes.isEmpty()) {
            throw new IllegalArgumentException("Barcode not found: " + type);
        }
        return mapper.toResponseDTOList(barcodes);
    }

    //DELETE
    public void deleteBarcode(String productCode) {
        barcodeRepository.deleteByProductCode(productCode);
    }
}
