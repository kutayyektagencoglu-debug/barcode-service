package com.migros.barcodeservice.service;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.enums.BarcodeType;
import com.migros.barcodeservice.mapper.BarcodeMapper;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import com.migros.barcodeservice.repository.BarcodeSequenceRepository;
import com.migros.commonerror.exception.ApiException;
import com.migros.commonerror.exception.BusinessException;
import jakarta.transaction.Transactional;
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
        Barcode barcode = mapper.toEntity(barcodeRequestDTO);
        List<Barcode> barcodeList = new ArrayList<>();

        String productCode = barcode.getProductCode();
        if(productCode == null || productCode.length() != 5) {
            throw new ApiException("PRODUCT_CODE_INVALID", "Product code is invalid (it needs to contain 5 characters)", 400);
        }
        String categoryCode = productCode.substring(0, 2);
        String unit = barcodeRequestDTO.getUnit();
        switch (categoryCode) {
            case "MY":
                handleMyCategory(barcodeList, barcode, unit);
                break;
            case "BL":
                handleBlCategory(barcodeList, barcode, unit);
                break;
            case "ET":
                barcodeList.add(createScaleBarcode(new Barcode(barcode)));
                break;
            default:
                barcodeList.add(createProductBarcode(new Barcode(barcode)));
        }
        barcodeRepository.saveAll(barcodeList);

        return mapper.toResponseDTOList(barcodeList);
    }

    private void handleMyCategory(List<Barcode> barcodeList, Barcode barcode, String unit) {
        if(unit.equals("KILOGRAM")) {
            barcodeList.add(createProductBarcode(new Barcode(barcode)));
            barcodeList.add(createRegisterBarcode(new Barcode(barcode)));
        } else {
            barcodeList.add(createProductBarcode(new Barcode(barcode)));
        }
    }
    private void handleBlCategory(List<Barcode> barcodeList, Barcode barcode, String unit) {
        if(unit.equals("KILOGRAM")) {
            barcodeList.add(createProductBarcode(new Barcode(barcode)));
            barcodeList.add(createScaleBarcode(new Barcode(barcode)));
        } else if(unit.equals("NUMBER")) {
            barcodeList.add(createRegisterBarcode(new Barcode(barcode)));
        } else {
            barcodeList.add(createProductBarcode(new Barcode(barcode)));
        }
    }

    public Barcode createProductBarcode(Barcode barcode) {
        barcode.setType(BarcodeType.PRODUCT);

        Long sequence = barcodeSequenceRepository.nextProductBarcode();
        if(sequence > 999999999) {
            throw new BusinessException("BARCODE_SEQUENCE_EXHAUSTED", "No product barcodes remaining", 422);
        }
        String code = String.format("%09d", sequence);
        barcode.setCode(code);
        return barcode;
    }

    public Barcode createRegisterBarcode(Barcode barcode) {
        barcode.setType(BarcodeType.REGISTER);
        Long sequence = barcodeSequenceRepository.nextRegisterBarcode();
        if(sequence > 9999) {
            throw new BusinessException("BARCODE_SEQUENCE_EXHAUSTED", "No register barcodes remaining", 422);
        }
        String code = String.format("%04d", sequence);
        barcode.setCode(code);
        return barcode;
    }

    public Barcode createScaleBarcode(Barcode barcode) {
        barcode.setType(BarcodeType.SCALE);
        Long sequence = barcodeSequenceRepository.nextScaleBarcode();
        if(sequence > 999) {
            throw new BusinessException("BARCODE_SEQUENCE_EXHAUSTED", "No scale barcodes remaining", 422);
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
                .orElseThrow(() ->  new BusinessException("BARCODE_NOT_FOUND", "Barcode not found: " + code, 404));
        return mapper.toResponseDTO(barcode);
    }

    //READ BY PRODUCT CODE
    public List<BarcodeResponseDTO> getBarcodeByProductCode(String productCode) {
        List<Barcode> barcodes = barcodeRepository.findByProductCode(productCode);
        if(barcodes.isEmpty()) {
            throw new BusinessException("BARCODE_NOT_FOUND", "Barcode not found: " + productCode, 404);
        }
        return mapper.toResponseDTOList(barcodes);
    }

    //READ BY TYPE
    public List<BarcodeResponseDTO> getBarcodeByType(BarcodeType type) {
        List<Barcode> barcodes = barcodeRepository.findByType(type);
        if(barcodes.isEmpty()) {
            throw new BusinessException("BARCODE_NOT_FOUND", "Barcode not found: " + type, 404);
        }
        return mapper.toResponseDTOList(barcodes);
    }

    //DELETE
    @Transactional
    public void deleteBarcode(String productCode) {
        if(!barcodeRepository.existsByProductCode(productCode)) {
            throw new BusinessException("BARCODE_NOT_FOUND", "No such barcode with product code of " + productCode + " exists", 404);
        }
        barcodeRepository.deleteByProductCode(productCode);
    }
}
