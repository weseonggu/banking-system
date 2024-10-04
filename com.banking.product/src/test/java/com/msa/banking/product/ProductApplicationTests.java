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

@SpringBootTest
@ActiveProfiles("dev")
class ProductApplicationTests {

    @Autowired
    private ProductRepositoryCustom productRepositoryCustom;



    @Test
    void contextLoads() {
        Pageable pageable = PageRequest.of(0,10, Sort.by("create_at").ascending());

        RequestSearchProductDto requestSearchProductDto = new RequestSearchProductDto(null, null, null, null);

        List<ResponseProductPage> data =  productRepositoryCustom.findAllProduct(pageable, requestSearchProductDto);
        System.out.println("결과1");
        for (ResponseProductPage responseProductPage : data) {
            System.out.println("결과2");
            System.out.println(responseProductPage.getName());
        }
    }

}
