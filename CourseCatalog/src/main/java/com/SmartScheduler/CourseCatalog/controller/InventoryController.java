package com.SmartScheduler.CourseCatalog.controller;

import com.SmartScheduler.CourseCatalog.dto.CourseListRequest;
import com.SmartScheduler.CourseCatalog.dto.CourseRequest;
import com.SmartScheduler.CourseCatalog.dto.PreReqWrapper;
import com.SmartScheduler.CourseCatalog.dto.PreRequisites;
import com.SmartScheduler.CourseCatalog.service.CatalogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class InventoryController {

    private final CatalogService catalogService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public String getPreqs(@RequestParam List<String> targetCourses) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<PreRequisites>> testing = catalogService.getPreReqs(targetCourses);
        for(List<PreRequisites> lst : testing){
            for(int i = 0; i < lst.size(); i++){
                System.out.print(lst.get(i).getCourseName());
                System.out.print(lst.get(i).getParentIndex());
                System.out.print("    ");
            }
            System.out.println("");
        }
        return objectMapper.writeValueAsString(testing);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addCourse(@RequestBody CourseListRequest courseListRequest){
        catalogService.addCourse(courseListRequest);
    }

}
