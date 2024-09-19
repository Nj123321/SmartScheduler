package com.SmartScheduler.Scheduler.service;

import com.SmartScheduler.Scheduler.dto.ScheduleCourseRequirement;
import com.SmartScheduler.Scheduler.dto.ScheduleRequest;
import com.SmartScheduler.Scheduler.model.ScheduleDraft;
import com.SmartScheduler.Scheduler.repository.CoursePlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final CoursePlanRepository coursePlanRepository;
    public void addCourse(ScheduleRequest courseNodes){
        System.out.println(courseNodes.getUid());
        System.out.println("hereweird");
        Optional<ScheduleDraft> buffer = coursePlanRepository.findById(courseNodes.getUid());
        ScheduleDraft currentPlan;
        System.out.println("here");
        if(buffer.isPresent()){
            currentPlan = buffer.get();
        }else{
            currentPlan = new ScheduleDraft();
            currentPlan.setUid(courseNodes.getUid());
        }
        currentPlan.getPlannedCourseList().clear(); //patch
        System.out.println("Adding Courses");
        for(ScheduleCourseRequirement b: courseNodes.getScheduleCourseRequirements()){
            System.out.println(b.getSemesterRequirement());
                currentPlan.getPlannedCourseList().add(new ScheduleDraft.PlannedCourse(
                        b.getCid(), b.getCourseName(), b.getSemesterRequirement()
                ));
        }
        currentPlan.setSemesters(courseNodes.getSemesters());
        coursePlanRepository.save(currentPlan);
    }
    public List<ScheduleDraft.PlannedCourse> getCoursePlan(Integer uid){
        return coursePlanRepository.findById(uid).get().getPlannedCourseList();
    }
    public Integer getSemesters(Integer uid){
        return coursePlanRepository.findById(uid).get().getSemesters();
    }
}
