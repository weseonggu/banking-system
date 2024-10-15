package com.msa.banking.personal.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openApi() {
        final String jwtSecurityName = "bearerAuth"; // 헤더 인증 Key (중요!!)
        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes(jwtSecurityName, securityScheme(jwtSecurityName)) // Header 토큰 인증 정보를 Swagger 문서에 등록
                )
                .info(swaggerInfo());
    }

    // Header 토큰 인증 정보 생성
    private SecurityScheme securityScheme(String jwtSecurityName) {
        return new SecurityScheme()
                .name(jwtSecurityName)
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer") // 토큰 Key
                .bearerFormat("JWT"); // 토큰 Value Format
    }

    // 문서 기본 정보
    private Info swaggerInfo() {
        return new Info()
                .title("개인 예산 관리 시스템 API 문서")
                .description("사용자는 예산을 설정하고 지출 내역을 추적할 수 있으며, 설정한 예산을 초과할 경우 알림이 전송됩니다.")
                .version("1.0.0");
    }
}