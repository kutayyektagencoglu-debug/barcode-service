package com.migros.barcodeservice.unittest;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.mapper.BarcodeMapper;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import com.migros.barcodeservice.repository.BarcodeSequenceRepository;
import com.migros.barcodeservice.service.BarcodeService;
import com.migros.commonerror.exception.ApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Barcode Service Unit Test")
public class BarcodeServiceTest {
    @Mock
    private BarcodeRepository barcodeRepository;
    @Mock
    private BarcodeSequenceRepository barcodeSequenceRepository;
    @Mock
    private BarcodeMapper mapper;

    @InjectMocks
    private BarcodeService barcodeService;

    @Test
    void createBarcodeEmptyProductCode() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode(null);

        Barcode barcode = new Barcode();
        barcode.setProductCode(null);

        when(mapper.toEntity(dto)).thenReturn(barcode);

        assertThrows(ApiException.class, () -> barcodeService.createBarcode(dto));
    }

    @Test
    void createBarcodeMYKilogramSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("MY001");
        dto.setUnit("KILOGRAM");

        Barcode barcode = new Barcode();
        barcode.setProductCode("MY001");

        when(mapper.toEntity(dto)).thenReturn(barcode);
        
    }

    @Test
    void createBarcodeMYNumberSuccess() {
    }

    @Test
    void createBarcodeBlKilogramSuccess() {
    }

    @Test
    void createBarcodeBlNumberSuccess() {
    }

    @Test
    void createBarcodeETKilogramSuccess() {
    }

    @Test
    void createBarcodeETNumberSuccess() {
    }

    @Test
    void createBarcodeNormalSuccess() {
    }

}
