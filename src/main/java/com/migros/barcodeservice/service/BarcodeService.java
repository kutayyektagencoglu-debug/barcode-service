package com.migros.barcodeservice.service;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
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

    public BarcodeService(BarcodeRepository barcodeRepository,BarcodeSequenceRepository barcodeSequenceRepository,
                          BarcodeMapper mapper) {
        this.barcodeRepository = barcodeRepository;
        this.barcodeSequenceRepository = barcodeSequenceRepository;
        this.mapper = mapper;
    }

    //CREATE
    public List<BarcodeResponseDTO> createBarcode(BarcodeRequestDTO barcodeRequestDTO) {
        String unit = barcodeRequestDTO.getUnit();
        Barcode barcode = mapper.toEntity(barcodeRequestDTO);
        List<Barcode> barcodeList = new ArrayList<>();

        String productCode = barcode.getProductCode();
        if(productCode == null || productCode.isEmpty()) {
            throw new IllegalArgumentException("product code is null or empty");
        }
        String categoryCode = productCode.substring(0, 2);
        switch (categoryCode) {
            case "MY":
                handleMyCategory(barcodeList, barcode, unit);
                break;
            case "BL":
                handleBlCategory(barcodeList, barcode, unit);
                break;
            case "ET":
                barcodeList.add(createScaleBarcode(barcode));
                break;
            default:
                barcodeList.add(createProductBarcode(barcode));
        }
        barcodeRepository.saveAll(barcodeList);

        return mapper.toResponseDTOList(barcodeList);
    }
    private void handleMyCategory(List<Barcode> barcodeList, Barcode barcode, String unit) {
        if(unit.equals("KILOGRAM")) {
            barcodeList.add(createProductBarcode(barcode));
            barcodeList.add(createRegisterBarcode(barcode));
        } else {
            barcodeList.add(createProductBarcode(barcode));
        }
    }
    private void handleBlCategory(List<Barcode> barcodeList, Barcode barcode, String unit) {
        if(unit.equals("KILOGRAM")) {
            barcodeList.add(createProductBarcode(barcode));
            barcodeList.add(createScaleBarcode(barcode));
        } else if(unit.equals("NUMBER")) {
            barcodeList.add(createRegisterBarcode(barcode));
        } else {
            barcodeList.add(createProductBarcode(barcode));
        }
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
        if(!barcodeRepository.existsByProductCode(productCode)) {
            throw new IllegalArgumentException("No such barcode with product code of " + productCode + " exists");
        }
        barcodeRepository.deleteByProductCode(productCode);
    }
}
