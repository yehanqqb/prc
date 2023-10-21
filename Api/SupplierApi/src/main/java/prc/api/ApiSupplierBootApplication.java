package prc.api;


import org.jetbrains.annotations.NotNull;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@ComponentScan({"prc.**", "prc.**.service.**","prc.service.**"})
@MapperScan(value = "prc.service.mapper")
public class ApiSupplierBootApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ApiSupplierBootApplication.class);
        application.run(args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }
}