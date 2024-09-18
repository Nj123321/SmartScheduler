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

//	@Bean
//	public CommandLineRunner loadData(CourseRepository inventoryRepository) {
//		return args -> {
//			Course c = new Course();
//			c.setCourseName("amen");
//			c.setDifficulty(4);
//
//			Course c2 = new Course();
//			c2.setCourseName("bongo");
//			c2.setDifficulty(4);
//
//			Course c3 = new Course();
//			c3.setCourseName("cat");
//			c3.setDifficulty(4);
//
//			Course c4 = new Course();
//			c4.setCourseName("dog");
//			c4.setDifficulty(3);
//
//			Course c5 = new Course();
//			c5.setCourseName("ear");
//			c5.setDifficulty(4);
//
//			Course c6 = new Course();
//			c6.setCourseName("fat");
//			c6.setDifficulty(4);
//
//			c.setPreReqs(new ArrayList<>());
//			c.setNeighbors(new ArrayList<>());
//			c2.setPreReqs(new ArrayList<>());
//			c2.setNeighbors(new ArrayList<>());
//			c3.setPreReqs(new ArrayList<>());
//			c3.setNeighbors(new ArrayList<>());
//			c4.setPreReqs(new ArrayList<>());
//			c4.setNeighbors(new ArrayList<>());
//			c5.setPreReqs(new ArrayList<>());
//			c5.setNeighbors(new ArrayList<>());
//			c6.setPreReqs(new ArrayList<>());
//			c6.setNeighbors(new ArrayList<>());
//
//			List<String> atemp = new ArrayList<>();
//			atemp.add("bongo");
//			List<String> btemp = new ArrayList<>();
//			btemp.add("cat");
//			btemp.add("dog");
//			List<String> ctemp = new ArrayList<>();
//			ctemp.add("ear");
//
//			c.setPreReqs(atemp);
//			c2.setPreReqs(btemp);
//			c3.setPreReqs(ctemp);
//
//			List<Course> uf = new ArrayList<>();
//			uf.add(c2);
//			c.setNeighbors(uf);
//
//			uf = new ArrayList<>();
//			uf.add(c3);
//			uf.add(c4);
//			c2.setNeighbors(uf);
//
//			uf = new ArrayList<>();
//			uf.add(c5);
//			c3.setNeighbors(uf);
//
//			inventoryRepository.save(c);
//			inventoryRepository.save(c2);
//			inventoryRepository.save(c3);
//			inventoryRepository.save(c4);
//			inventoryRepository.save(c5);
//			inventoryRepository.save(c6);
//		};
//	}
}
