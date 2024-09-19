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
import java.util.Stack;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CourseRepository courseRepository;

    /**
     * Gets all PreRequisites for targeted courses
     * @param courses courses to get prerequisites
     * @return prerequisites of courses sorted in DFS in-order traversal
     */
    @Transactional(readOnly = true)
    public List<List<PreRequisites>> getPreReqs(List<String> courses) {
        List<List<PreRequisites>> rv = new ArrayList<>();
        for(String cname: courses){
            rv.add(getCoursePreReqs(cname, -1));
            System.out.println("added");
        }
        return rv;
    }

    /**
     * Gets Prereqs for course
     * @param rootName course to get PreRequisites
     * @param parentDepth starting value
     * @return prerequisites of rootName with startingValue, and incremented in in-order DFS traversal
     */
    private List<PreRequisites> getCoursePreReqs(String rootName, int parentDepth){
        Stack<String> stack = new Stack<String>();
        Stack<Integer> intStack = new Stack<Integer>();
        List<PreRequisites> rv = new ArrayList<>();
        stack.push(rootName);
        intStack.push(-1);
        int preOrderIndex = 0;
        while(!stack.isEmpty()){
            Course c = courseRepository.findByCourseName(stack.pop());
            System.out.println(c.getCourseName());
            int parent = intStack.pop();
            List<String> buffer = c.getPreReqs();
            if(buffer != null && buffer.size() != 0 ){
                for(String s: buffer){
                    stack.push(s);
                    intStack.push(preOrderIndex);
                }
            }
            System.out.print("skadfklsdjflkasjdfklsjdflk: ");
            System.out.print(c.getCourseName());
            System.out.print(parent);
            System.out.println("");
            rv.add(PreRequisites.builder()
                    .courseName(c.getCourseName())
                    .difficulty(c.getDifficulty())
                    .parentIndex(parent)
                    .build());
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
