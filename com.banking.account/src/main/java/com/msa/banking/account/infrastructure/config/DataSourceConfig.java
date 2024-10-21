package com.msa.banking.account.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.msa.banking.account.domain.repository",
        entityManagerFactoryRef = "dataEntityManager",
        transactionManagerRef = "dataTransactionManager"
)
public class DataSourceConfig {

    @Value("${spring.jpa.properties.hibernate.show_sql}")
    private boolean showSql;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;

    @Primary
    @Bean // Spring 기본 데이터 소스
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {

        return DataSourceBuilder.create().build();
    }

    @Bean(name="jpaVendorAdapter")
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(showSql);
        adapter.setGenerateDdl("update".equalsIgnoreCase(ddlAuto));
        adapter.setDatabasePlatform(dialect);
        return adapter;
    }

    @Primary
    @Bean(name="dataEntityManager")
    public LocalContainerEntityManagerFactoryBean dataEntityManager(
            @Qualifier("dataSource") DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter) {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.msa.banking.account.domain.model");  // 엔티티가 있는 패키지
        em.setJpaVendorAdapter(jpaVendorAdapter);

        return em;
    }

    // PlatformTransactionManager: Spring 프레임워크에서 트랜잭션 관리를 담당하는 인터페이스
    // 필드로 주입받아 사용
    @Primary
    @Bean(name="dataTransactionManager")
    public PlatformTransactionManager dataTransactionManager(
            @Qualifier("dataEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactory) {

        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory.getObject());
        return tm;
    }
}
