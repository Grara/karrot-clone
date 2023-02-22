package com.karrotclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class KarrotcloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(KarrotcloneApplication.class, args);
	}

}
