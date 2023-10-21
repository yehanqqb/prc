package prc.timing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
@EnableScheduling
@ComponentScan({"prc.**", "prc.**.service.**","prc.service.**"})
@MapperScan(value = "prc.service.mapper")
public class TimingApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(TimingApplication.class);
        application.run(args);
    }
}
