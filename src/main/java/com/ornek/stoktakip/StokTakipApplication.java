package com.ornek.stoktakip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class StokTakipApplication {

	public static void main(String[] args) {
		SpringApplication.run(StokTakipApplication.class, args);
	}

}
