package com.msa.banking.product;

import com.msa.banking.product.infrastructure.repository.ProductRepository;
import com.msa.banking.product.lib.ProductType;
import com.msa.banking.product.domain.model.CheckingDetail;
import com.msa.banking.product.domain.model.LoanDetail;
import com.msa.banking.product.domain.model.PDFInfo;
import com.msa.banking.product.domain.model.Product;
import com.msa.banking.product.domain.repository.LoanDetailRepository;
import com.msa.banking.product.domain.service.PDFInfoService;
import com.msa.banking.product.domain.service.ProductService;
import com.msa.banking.product.presentation.request.RequestSearchProductDto;
import com.msa.banking.product.presentation.response.ResponseProductPage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("dev")
@Disabled("이 테스트는 생략됩니다.")
public class ProductCreater {

//    @Autowired
//    private PDFInfoService pdfInfoService;
//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private LoanDetailRepository loanDetailRepository;
//    @Disabled("이 테스트는 생략됩니다.")
//    @Test
//    public void createCheckingProduct() {
//        for (int i = 0; i<5000; i++){
//            PDFInfo pdf = PDFInfo.create("testfile 다운 안됩니다.","e1d7e542-2764-4ce4-82f2-470d41b15122.pdf");
//
//            // 입출금 상품 생성
//            Product product = Product.create(
//                    "Savings Account: "+i,
//                    ProductType.CHECKING,
//                    LocalDateTime.now(),
//                    LocalDateTime.parse("2025-10-10T12:00:00")
//            );
//
//
//            // 입출금 상품 디테일 생성
//            CheckingDetail checkingDetail = CheckingDetail.create(
//                    "This is a detailed description of the checking product.",
//                    "These are the terms and conditions of the checking product.",
//                    BigDecimal.valueOf(0.12),
//                    10
//            );
//            // 상품 저장
//            product = productService.saveProduct(product);
//            // 상품 디테일 저장
//            productService.saveCheckingProduct(product, checkingDetail, pdf);
//        }
//
//    }
//
//    @Test
//    public void createLoanProduct() {
//        for (int i = 0; i<5000; i++){
//
//
//        PDFInfo pdf = PDFInfo.create("testfile 다운 안됩니다.","1cc72db1-4e3b-4f8a-bce5-a56e425c2b7e.pdf");
//        // 대출 상품 생성
//        Product product = Product.create(
//                "Standard Loan: "+i,
//                ProductType.NEGATIVE_LOANS,
//                LocalDateTime.now(),
//                LocalDateTime.parse("2025-10-10T12:00:00")
//        );
//        // 대출 상품 디테일 생성
//        LoanDetail loanDetail = LoanDetail.create(
//                BigDecimal.valueOf(3.4567),
//        1000L,
//        20000000L,
//        36,
//                "2.5% for premium customers",
//                "This loan product offers flexible repayment options for personal use.",
//                "Terms and conditions apply. Please review before proceeding."
//                );
//        // 대출 상품 저장
//        product = productService.saveProduct(product);
//        // 대출 상품 디테일 저장
//        productService.saveLoanProduct(product, loanDetail, pdf);
//        }
//
//    }
//    @Autowired
//    private ProductRepository productRepository;
//
//
//
//    @Test
//    void contextLoads() {
//        Pageable pageable = PageRequest.of(0,10, Sort.by("create_at").ascending());
//
//        RequestSearchProductDto requestSearchProductDto = new RequestSearchProductDto(null, null, null, null);
//
//        List<ResponseProductPage> data =  productRepository.findAllProductsPage(pageable, requestSearchProductDto);
//        System.out.println("결과1");
//        for (ResponseProductPage responseProductPage : data) {
//            System.out.println("결과2");
//            System.out.println(responseProductPage.getName());
//        }
//    }
//    @Test
//    void fingDetail(){
//        productRepository.findEntityGrapById(UUID.fromString("5e2cf550-60b6-4be7-baf4-97b6b8d396d7"));
//    }
}
