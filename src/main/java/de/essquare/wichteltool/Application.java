package de.essquare.wichteltool;

import java.util.Map;
import java.util.Properties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner CommandLineRunnerBean() {
		System.out.println("-------------------------------------------------------");

		Map<String, String> getenv = System.getenv();
		getenv.entrySet().forEach(System.out::println);

		System.out.println("-------------------------------------------------------");

		Properties properties = System.getProperties();
		properties.forEach((o, o2) -> System.out.println(o + ": " + o2));

		return (args) -> {
			System.out.println("In CommandLineRunnerImpl ");

			for (String arg : args) {
				System.out.println(arg);
			}
		};
	}
}