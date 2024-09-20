package com.SmartScheduler.CourseCatalog.controller;

import com.SmartScheduler.CourseCatalog.dto.*;
import com.SmartScheduler.CourseCatalog.service.CatalogService;
import com.SmartScheduler.CourseCatalog.service.CourseNotFoundException;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
public class InventoryController {

    private final CatalogService catalogService;

    /**
     * Gets PreRequisites for each Course requested
     * @param listOfCourseIDs list of courses to search prerequisites for
     * @return List of PreRequisites
     * @throws JsonProcessingException
     * @throws CourseNotFoundException
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public PreReqDTO getPreqs(@RequestParam List<Integer> listOfCourseIDs) throws JsonProcessingException, CourseNotFoundException {
        List<List<PreRequisites>> testing = catalogService.getPreReqs(listOfCourseIDs);
        return new PreReqDTO(testing);
    }

    /**
     * Adds/Updates course to database
     * @param courseListRequest List of courses to add
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addCourse(@RequestBody CourseListRequest courseListRequest){
        catalogService.addCourse(courseListRequest);
    }

}
