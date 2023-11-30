package de.tekup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MailingServiceApplication
{
    public static void main(String[] args) {
        SpringApplication.run(MailingServiceApplication.class, args);
    }
}
