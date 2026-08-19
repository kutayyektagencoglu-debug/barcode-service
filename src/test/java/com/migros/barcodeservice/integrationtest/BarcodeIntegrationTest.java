package com.migros.barcodeservice.integrationtest;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.enums.BarcodeType;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import com.migros.barcodeservice.service.BarcodeService;
import com.migros.commonerror.exception.ApiException;
import com.migros.commonerror.exception.BusinessException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BarcodeIntegrationTest {
    @Autowired
    private BarcodeService barcodeService;

    @Autowired
    private BarcodeRepository barcodeRepository;

    @Test
    void createBarcodeInvalidCode() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("INVALID");
        dto.setUnit("KILOGRAM");

        assertThrows(ApiException.class, () -> barcodeService.createBarcode(dto));

        assertTrue(barcodeRepository.findAll().isEmpty());
    }

    @Test
    void createBarcodeSuccess() {
        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        List<BarcodeResponseDTO> result1 = barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        List<BarcodeResponseDTO> result2 = barcodeService.createBarcode(dto2);

        String productCode1 = result1.get(0).getCode();
        assertTrue(productCode1.matches("\\d{9}"));

        String registerCode = result1.get(1).getCode();
        assertTrue(registerCode.matches("\\d{4}"));

        String productCode2 = result2.get(0).getCode();

        // Codes should be unique
        assertNotEquals(productCode1, productCode2);

        assertEquals(2, result1.size());
        assertEquals(BarcodeType.PRODUCT, result1.get(0).getType());
        assertEquals(BarcodeType.REGISTER, result1.get(1).getType());
        assertEquals(Long.parseLong(productCode1) + 1, Long.parseLong(productCode2));

        List<Barcode> saved = barcodeRepository.findAll();
        assertEquals(3, saved.size());
        assertTrue(saved.stream().anyMatch(b -> b.getType() == BarcodeType.PRODUCT));
        assertTrue(saved.stream().anyMatch(b -> b.getType() == BarcodeType.REGISTER));
    }

    @Test
    void getAllBarcodesSuccess() {
        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        barcodeService.createBarcode(dto2);

        BarcodeRequestDTO dto3 = new BarcodeRequestDTO();
        dto3.setProductCode("BL001");
        dto3.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto3);

        List<BarcodeResponseDTO> result = barcodeService.getAllBarcodes();

        assertEquals(5, result.size());
    }

    @Test
    void getBarcodeByProductCodeFail() {

        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        barcodeService.createBarcode(dto2);

        BarcodeRequestDTO dto3 = new BarcodeRequestDTO();
        dto3.setProductCode("BL001");
        dto3.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto3);

        assertThrows(BusinessException.class, () -> barcodeService.getBarcodeByProductCode("IC001"));
    }

    @Test
    void getBarcodeByProductCodeSuccess() {

        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        barcodeService.createBarcode(dto2);

        BarcodeRequestDTO dto3 = new BarcodeRequestDTO();
        dto3.setProductCode("BL001");
        dto3.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto3);

        List<BarcodeResponseDTO> result = barcodeService.getBarcodeByProductCode("MY001");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(b -> "MY001".equals(b.getProductCode())));
    }

    @Test
    void getBarcodeByCodeSuccess() {
        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        List<BarcodeResponseDTO> created1 = barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        List<BarcodeResponseDTO> created2 = barcodeService.createBarcode(dto2);

        BarcodeRequestDTO dto3 = new BarcodeRequestDTO();
        dto3.setProductCode("BL001");
        dto3.setUnit("KILOGRAM");
        List<BarcodeResponseDTO> created3 = barcodeService.createBarcode(dto3);

        // Use generated codes
        String code1 = created1.get(0).getCode();
        String code2 = created2.get(0).getCode();
        String code3 = created1.get(1).getCode();
        String code4 = created3.get(1).getCode();

        BarcodeResponseDTO result1 = barcodeService.getBarcodeByCode(code1);
        BarcodeResponseDTO result2 = barcodeService.getBarcodeByCode(code2);
        BarcodeResponseDTO result3 = barcodeService.getBarcodeByCode(code3);
        BarcodeResponseDTO result4 = barcodeService.getBarcodeByCode(code4);

        assertEquals(BarcodeType.PRODUCT, result1.getType());
        assertEquals("MY001", result1.getProductCode());

        assertEquals(BarcodeType.PRODUCT, result2.getType());
        assertEquals("MY002", result2.getProductCode());

        assertEquals(BarcodeType.REGISTER, result3.getType());
        assertEquals("MY001", result3.getProductCode());

        assertEquals(BarcodeType.SCALE, result4.getType());
        assertEquals("BL001", result4.getProductCode());
    }

    @Test
    void getBarcodeByTypeSuccess() {

        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        barcodeService.createBarcode(dto2);

        BarcodeRequestDTO dto3 = new BarcodeRequestDTO();
        dto3.setProductCode("BL001");
        dto3.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto3);

        List<BarcodeResponseDTO> result = barcodeService.getBarcodeByType(BarcodeType.PRODUCT);

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(b -> b.getType() == BarcodeType.PRODUCT));
    }

    @Test
    void deleteBarcodeSuccess() {

        BarcodeRequestDTO dto1 = new BarcodeRequestDTO();
        dto1.setProductCode("MY001");
        dto1.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto1);

        BarcodeRequestDTO dto2 = new BarcodeRequestDTO();
        dto2.setProductCode("MY002");
        dto2.setUnit("NUMBER");
        barcodeService.createBarcode(dto2);

        BarcodeRequestDTO dto3 = new BarcodeRequestDTO();
        dto3.setProductCode("BL001");
        dto3.setUnit("KILOGRAM");
        barcodeService.createBarcode(dto3);

        barcodeService.deleteBarcode("MY001");

        List<BarcodeResponseDTO> result = barcodeService.getAllBarcodes();

        assertEquals(3, result.size());
        assertTrue(result.stream().noneMatch(b -> "MY001".equals(b.getProductCode())));
    }
}