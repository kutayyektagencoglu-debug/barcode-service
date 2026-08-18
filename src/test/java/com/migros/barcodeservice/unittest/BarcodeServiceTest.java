package com.migros.barcodeservice.unittest;

import com.migros.barcodeservice.dto.BarcodeRequestDTO;
import com.migros.barcodeservice.dto.BarcodeResponseDTO;
import com.migros.barcodeservice.enums.BarcodeType;
import com.migros.barcodeservice.mapper.BarcodeMapper;
import com.migros.barcodeservice.model.Barcode;
import com.migros.barcodeservice.repository.BarcodeRepository;
import com.migros.barcodeservice.repository.BarcodeSequenceRepository;
import com.migros.barcodeservice.service.BarcodeService;
import com.migros.commonerror.exception.ApiException;
import com.migros.commonerror.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
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
    void createBarcodeThrowsWhenEmptyProductCode() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode(null);

        Barcode barcode = new Barcode();
        barcode.setProductCode(null);

        when(mapper.toEntity(dto)).thenReturn(barcode);

        assertThrows(ApiException.class, () -> barcodeService.createBarcode(dto));
    }

    @Test
    void createBarcodeThrowsWhenLongProductSequence() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("TS001");
        dto.setUnit("NUMBER");

        Barcode barcode = new Barcode();
        barcode.setProductCode("TS001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextProductBarcode()).thenReturn(1000000000L);

        assertThrows(BusinessException.class, () -> barcodeService.createBarcode(dto));
    }

    @Test
    void createBarcodeThrowsWhenLongRegisterSequence() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("BL001");
        dto.setUnit("NUMBER");

        Barcode barcode = new Barcode();
        barcode.setProductCode("BL001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextRegisterBarcode()).thenReturn(10000L);

        assertThrows(BusinessException.class, () -> barcodeService.createBarcode(dto));
    }

    @Test
    void createBarcodeThrowsWhenLongScaleSequence() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("ET001");
        dto.setUnit("NUMBER");

        Barcode barcode = new Barcode();
        barcode.setProductCode("ET001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextScaleBarcode()).thenReturn(1000L);

        assertThrows(BusinessException.class, () -> barcodeService.createBarcode(dto));
    }

    @Test
    void createBarcodeMYKilogramSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("MY001");
        dto.setUnit("KILOGRAM");

        Barcode barcode = new Barcode();
        barcode.setProductCode("MY001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextProductBarcode()).thenReturn(1L);
        when(barcodeSequenceRepository.nextRegisterBarcode()).thenReturn(1L);

        when(barcodeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponseDTOList(anyList())).thenAnswer(invocation -> {
            List<Barcode> barcodes = invocation.getArgument(0);
            return barcodes.stream()
                    .map(b -> {
                        BarcodeResponseDTO barcodeResponseDTO = new BarcodeResponseDTO();
                        barcodeResponseDTO.setType(b.getType());
                        barcodeResponseDTO.setCode(b.getCode());
                        return barcodeResponseDTO;
                    })
                    .toList();
        });

        List<BarcodeResponseDTO> result = barcodeService.createBarcode(dto);

        assertEquals(2, result.size());
        assertEquals(BarcodeType.PRODUCT, result.get(0).getType());
        assertEquals("000000001",  result.get(0).getCode());
        assertEquals(BarcodeType.REGISTER, result.get(1).getType());
        assertEquals("0001",   result.get(1).getCode());

        verify(barcodeRepository).saveAll(anyList());
    }

    @Test
    void createBarcodeMYNumberSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("MY001");
        dto.setUnit("NUMBER");

        Barcode barcode = new Barcode();
        barcode.setProductCode("MY001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextProductBarcode()).thenReturn(1L);

        when(barcodeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponseDTOList(anyList())).thenAnswer(invocation -> {
            List<Barcode> barcodes = invocation.getArgument(0);
            return barcodes.stream()
                    .map(b -> {
                        BarcodeResponseDTO barcodeResponseDTO = new BarcodeResponseDTO();
                        barcodeResponseDTO.setType(b.getType());
                        barcodeResponseDTO.setCode(b.getCode());
                        return barcodeResponseDTO;
                    })
                    .toList();
        });

        List<BarcodeResponseDTO> result = barcodeService.createBarcode(dto);

        assertEquals(1, result.size());
        assertEquals(BarcodeType.PRODUCT, result.getFirst().getType());
        assertEquals("000000001",  result.getFirst().getCode());

        verify(barcodeRepository).saveAll(anyList());
    }

    @Test
    void createBarcodeBlKilogramSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("BL001");
        dto.setUnit("KILOGRAM");

        Barcode barcode = new Barcode();
        barcode.setProductCode("BL001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextProductBarcode()).thenReturn(1L);
        when(barcodeSequenceRepository.nextScaleBarcode()).thenReturn(1L);

        when(barcodeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponseDTOList(anyList())).thenAnswer(invocation -> {
            List<Barcode> barcodes = invocation.getArgument(0);
            return barcodes.stream()
                    .map(b -> {
                        BarcodeResponseDTO barcodeResponseDTO = new BarcodeResponseDTO();
                        barcodeResponseDTO.setType(b.getType());
                        barcodeResponseDTO.setCode(b.getCode());
                        return barcodeResponseDTO;
                    })
                    .toList();
        });

        List<BarcodeResponseDTO> result = barcodeService.createBarcode(dto);

        assertEquals(2, result.size());
        assertEquals(BarcodeType.PRODUCT, result.get(0).getType());
        assertEquals("000000001",  result.get(0).getCode());
        assertEquals(BarcodeType.SCALE, result.get(1).getType());
        assertEquals("BL001001",   result.get(1).getCode());

        verify(barcodeRepository).saveAll(anyList());
    }

    @Test
    void createBarcodeBlNumberSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("BL001");
        dto.setUnit("NUMBER");

        Barcode barcode = new Barcode();
        barcode.setProductCode("BL001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextRegisterBarcode()).thenReturn(1L);

        when(barcodeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponseDTOList(anyList())).thenAnswer(invocation -> {
            List<Barcode> barcodes = invocation.getArgument(0);
            return barcodes.stream()
                    .map(b -> {
                        BarcodeResponseDTO barcodeResponseDTO = new BarcodeResponseDTO();
                        barcodeResponseDTO.setType(b.getType());
                        barcodeResponseDTO.setCode(b.getCode());
                        return barcodeResponseDTO;
                    })
                    .toList();
        });

        List<BarcodeResponseDTO> result = barcodeService.createBarcode(dto);

        assertEquals(1, result.size());
        assertEquals(BarcodeType.REGISTER, result.getFirst().getType());
        assertEquals("0001",   result.getFirst().getCode());

        verify(barcodeRepository).saveAll(anyList());
    }

    @Test
    void createBarcodeETSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("ET001");
        dto.setUnit("KILOGRAM");

        Barcode barcode = new Barcode();
        barcode.setProductCode("ET001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextScaleBarcode()).thenReturn(1L);

        when(barcodeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponseDTOList(anyList())).thenAnswer(invocation -> {
            List<Barcode> barcodes = invocation.getArgument(0);
            return barcodes.stream()
                    .map(b -> {
                        BarcodeResponseDTO barcodeResponseDTO = new BarcodeResponseDTO();
                        barcodeResponseDTO.setType(b.getType());
                        barcodeResponseDTO.setCode(b.getCode());
                        return barcodeResponseDTO;
                    })
                    .toList();
        });

        List<BarcodeResponseDTO> result = barcodeService.createBarcode(dto);

        assertEquals(1, result.size());
        assertEquals(BarcodeType.SCALE, result.getFirst().getType());
        assertEquals("ET001001", result.getFirst().getCode());

        verify(barcodeRepository).saveAll(anyList());
    }

    @Test
    void createBarcodeNormalSuccess() {
        BarcodeRequestDTO dto = new BarcodeRequestDTO();
        dto.setProductCode("TS001");
        dto.setUnit("NUMBER");

        Barcode barcode = new Barcode();
        barcode.setProductCode("TS001");

        when(mapper.toEntity(dto)).thenReturn(barcode);

        when(barcodeSequenceRepository.nextProductBarcode()).thenReturn(1L);

        when(barcodeRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        when(mapper.toResponseDTOList(anyList())).thenAnswer(invocation -> {
            List<Barcode> barcodes = invocation.getArgument(0);
            return barcodes.stream()
                    .map(b -> {
                        BarcodeResponseDTO barcodeResponseDTO = new BarcodeResponseDTO();
                        barcodeResponseDTO.setType(b.getType());
                        barcodeResponseDTO.setCode(b.getCode());
                        return barcodeResponseDTO;
                    })
                    .toList();
        });

        List<BarcodeResponseDTO> result = barcodeService.createBarcode(dto);

        assertEquals(1, result.size());
        assertEquals(BarcodeType.PRODUCT, result.getFirst().getType());
        assertEquals("000000001",  result.getFirst().getCode());

        verify(barcodeRepository).saveAll(anyList());
    }

}
