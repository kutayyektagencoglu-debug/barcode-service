package com.migros.barcodeservice.client;

import com.migros.barcodeservice.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "http://localhost:8080")
public interface ProductClient {
    @GetMapping("/code/{code}")
    ProductDTO getProductByCode(@PathVariable String code);
}
