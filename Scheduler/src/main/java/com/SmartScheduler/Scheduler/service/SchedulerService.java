package com.SmartScheduler.Scheduler.service;

import com.SmartScheduler.Scheduler.dto.buffNode;
import com.SmartScheduler.Scheduler.dto.buffRequest;
import com.SmartScheduler.Scheduler.model.CoursePlan;
import com.SmartScheduler.Scheduler.repository.CoursePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final CoursePlanRepository coursePlanRepository;
    public void addCourse(buffRequest courseNodes){
        System.out.println(courseNodes.getUid());
        System.out.println("hereweird");
        Optional<CoursePlan> buffer = coursePlanRepository.findById(courseNodes.getUid());
        CoursePlan currentPlan;
        System.out.println("here");
        if(buffer.isPresent()){
            currentPlan = buffer.get();
        }else{
            currentPlan = new CoursePlan();
            currentPlan.setUid(courseNodes.getUid());
        }
        currentPlan.getPlannedCourseList().clear(); //patch
        System.out.println("Adding Courses");
        for(buffNode b: courseNodes.getBuffNodeList()){
            System.out.println(b.getSemesterRequirement());
                currentPlan.getPlannedCourseList().add(new CoursePlan.PlannedCourse(
                        b.getCid(), b.getCourseName(), b.getSemesterRequirement()
                ));
        }
        currentPlan.setSemesters(courseNodes.getSemesters());
        coursePlanRepository.save(currentPlan);
    }
    public List<CoursePlan.PlannedCourse> getCoursePlan(Integer uid){
        return coursePlanRepository.findById(uid).get().getPlannedCourseList();
    }
    public Integer getSemesters(Integer uid){
        return coursePlanRepository.findById(uid).get().getSemesters();
    }
}
