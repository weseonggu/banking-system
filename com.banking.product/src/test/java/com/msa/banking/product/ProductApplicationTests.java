package com.msa.banking.product;

import com.msa.banking.product.application.dto.ResponseProductPage;
import com.msa.banking.product.domain.repository.ProductRepository;
import com.msa.banking.product.domain.repository.ProductRepositoryCustom;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("dev")
class ProductApplicationTests {


    @Autowired
    private ProductRepository productRepository;



    @Test
    void contextLoads() {
        Pageable pageable = PageRequest.of(0,10, Sort.by("create_at").ascending());

        RequestSearchProductDto requestSearchProductDto = new RequestSearchProductDto(null, null, null, null);

        List<ResponseProductPage> data =  productRepository.findAllProduct(pageable, requestSearchProductDto);
        System.out.println("결과1");
        for (ResponseProductPage responseProductPage : data) {
            System.out.println("결과2");
            System.out.println(responseProductPage.getName());
        }
    }
    @Test
    void fingDetail(){
        productRepository.findEntityGrapById(UUID.fromString("5e2cf550-60b6-4be7-baf4-97b6b8d396d7"));
    }

}
