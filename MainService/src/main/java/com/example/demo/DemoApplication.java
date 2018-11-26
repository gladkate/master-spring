package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableAutoConfiguration(exclude=RabbitAutoConfiguration.class)
@SpringBootApplication

//@EnableCircuitBreaker
//@EnableEurekaClient
public class DemoApplication  {

	public static void main(String[] args) {

		Integer a = 1;
		a = 5;
		SpringApplication.run(DemoApplication.class, args);
	}
}
