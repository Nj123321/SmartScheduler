package com.SmartScheduler.CourseCatalog.service;

import com.SmartScheduler.CourseCatalog.dto.CourseListRequest;
import com.SmartScheduler.CourseCatalog.dto.CourseRequest;
import com.SmartScheduler.CourseCatalog.dto.PreRequisites;
import com.SmartScheduler.CourseCatalog.model.Course;
import com.SmartScheduler.CourseCatalog.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CourseRepository courseRepository;

    /**
     * Gets all PreRequisites for targeted courses
     * @param listOfCourseIDs list of courses to get prerequisite
     * @return prerequisites of courses sorted in DFS in-order traversal
     * @throws CourseNotFoundException
     */
    @Transactional(readOnly = true)
    public List<List<PreRequisites>> getPreReqs(List<Integer> listOfCourseIDs) throws CourseNotFoundException {
        List<List<PreRequisites>> rv = new ArrayList<>();
        for(Integer cid: listOfCourseIDs){
            rv.add(getCoursePreReqs(cid, -1));
            System.out.println("added");
        }
        return rv;
    }

    /**
     * Gets Prereqs for course
     * @param cID course
     * @param parentDepth starting value
     * @return prerequisites of rootName with startingValue, and incremented in in-order DFS traversal
     * @throws CourseNotFoundException
     */
    private List<PreRequisites> getCoursePreReqs(Integer cID, int parentDepth) throws CourseNotFoundException{
        Stack<Course> stack = new Stack<Course>();
        Stack<Integer> intStack = new Stack<Integer>();
        List<PreRequisites> rv = new ArrayList<>();
        int preOrderIndex = 0;
        Course courseIterator;

        //intialize
        Optional<Course> targetCourse =  courseRepository.findById(cID);
        if(!targetCourse.isPresent()){
            throw new CourseNotFoundException(cID);
        }
        stack.push(targetCourse.get());
        intStack.push(-1);

        while(!stack.isEmpty()){
            courseIterator = stack.pop();
            int parent = intStack.pop();
            for(Course neighbors: courseIterator.getNeighbors()){
                stack.push(neighbors);
                intStack.push(preOrderIndex);
            }
            // build dto
            rv.add(PreRequisites.builder()
                    .courseName(courseIterator.getCourseName())
                    .difficulty(courseIterator.getDifficulty())
                    .parentIndex(parent)
                    .build());
            //update
            preOrderIndex += 1;
        }
        return rv;
    }

    /**
     * Adds/Updates course to database
     * @param courseListRequest List of courses to add
     */
    public void addCourse(CourseListRequest courseListRequest) {
        Course buffer;
        for(CourseRequest courseRequest : courseListRequest.getCourseRequestList()){
            List<Course> neightbors = new ArrayList<>();
            for(String pname: courseRequest.getPreReqs()){
                neightbors.add(courseRepository.findByCourseName(pname));
            }
            buffer = new Course();
            buffer.setCourseID(courseRequest.getCid());
            buffer.setNeighbors(neightbors);
            buffer.setCourseName(courseRequest.getCourseName());
            buffer.setPreReqs(courseRequest.getPreReqs());
            buffer.setDifficulty(courseRequest.getDifficulty());
            courseRepository.save(buffer);
        }

    }
}
