package com.migros.barcodeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
        "com.migros.barcodeservice",
        "com.migros.commonerror"
})
@EnableFeignClients
public class BarcodeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BarcodeServiceApplication.class, args);
    }

}
