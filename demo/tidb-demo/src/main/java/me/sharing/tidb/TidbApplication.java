package me.sharing.tidb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TidbApplication {

    public static void main(String[] args) {
        SpringApplication.run(TidbApplication.class, args);
    }

}
