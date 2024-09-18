package com.SmartScheduler.CourseCatalog.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.ArrayList;
import java.util.List;

@Node("Course")
//boiler plate code
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    @Id
    private Integer courseID;
    @Relationship(type="PREREQUISITES", direction = Relationship.Direction.OUTGOING)
    private List<Course> neighbors = new ArrayList<>();
    private List<String> preReqs = new ArrayList<>();
    private String courseName;
    private Integer difficulty;
}
