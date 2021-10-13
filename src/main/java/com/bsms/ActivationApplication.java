package com.bsms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.bsms.cons.MbApiConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class ActivationApplication {
	
	public static void main(String args[]) {
		SpringApplication.run(ActivationApplication.class, args);
	}
	
	@Bean
	public ObjectMapper getObjectMapper(){
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping();
		return mapper;
	}
	
	@Bean 
	public MbApiConstant getHlAdConstant(){
		return new MbApiConstant();
	}
	
	@Bean
	public DateFormat dateFormat() {
		return new SimpleDateFormat(MbApiConstant.TIME_FORMAT);
	}
	
}
