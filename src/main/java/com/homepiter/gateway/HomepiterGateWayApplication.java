package com.homepiter.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class HomepiterGateWayApplication {
    public static void main(String[] args) {
//        // .env 파일 로드 및 시스템 환경변수로 설정
//        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
//        dotenv.entries().forEach(entry ->
//                System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(HomepiterGateWayApplication.class, args);
    }
}
