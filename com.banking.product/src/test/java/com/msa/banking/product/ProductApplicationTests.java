package com.msa.banking.product;

import com.msa.banking.product.presentation.response.ResponseProductPage;
import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import org.junit.jupiter.api.Disabled;
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

//    @Test
//    void contextLoads() {}

}
