package com.SmartScheduler.CourseCatalog;

import com.SmartScheduler.CourseCatalog.model.Course;
import com.SmartScheduler.CourseCatalog.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class CourseCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseCatalogApplication.class, args);
	}

}
