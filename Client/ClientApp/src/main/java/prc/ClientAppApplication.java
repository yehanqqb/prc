package prc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaAuditing
@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@ComponentScan({"prc.**", "prc.**.service.**"})
@MapperScan(value = "prc.service.mapper")
public class ClientAppApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(ClientAppApplication.class);
        application.run(args);
    }
}
