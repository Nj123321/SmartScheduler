package com.SmartScheduler.CourseCatalog.repository;


import com.SmartScheduler.CourseCatalog.model.Course;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface CourseRepository extends Neo4jRepository<Course, Integer> {
    Course findByCourseName(String name);
}
