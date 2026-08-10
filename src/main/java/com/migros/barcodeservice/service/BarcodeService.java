package com.migros.barcodeservice.service;

import com.migros.barcodeservice.client.ProductClient;
import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.dto.ProductDTO;
import com.migros.barcodeservice.mapper.BarcodeMapper;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    }

    public Barcode createRegisterBarcode(Barcode barcode) {

    }

    public Barcode createScaleBarcode(Barcode barcode) {

    }
}
