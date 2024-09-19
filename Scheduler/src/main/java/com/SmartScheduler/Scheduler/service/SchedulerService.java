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
//TODO allow multiple drafts to be added
public class SchedulerService {
    private final CoursePlanRepository coursePlanRepository;

    /**
     * Adds/updates schedule draft with userdata
     * @param courseNodes user schedule data along with new/updated courses for schedule
     */
    public void addCourse(ScheduleRequest courseNodes){
        //TODO update functionality instead of overwriting
        Optional<ScheduleDraft> buffer = coursePlanRepository.findById(courseNodes.getUid());
        ScheduleDraft currentPlan;
        if(buffer.isPresent()){
            currentPlan = buffer.get();
        }else{
            currentPlan = new ScheduleDraft();
            currentPlan.setUid(courseNodes.getUid());
        }
        currentPlan.getPlannedCourseList().clear(); //patch
        for(ScheduleCourseRequirement b: courseNodes.getScheduleCourseRequirements()){
            System.out.println(b.getSemesterRequirement());
                currentPlan.getPlannedCourseList().add(new ScheduleDraft.PlannedCourse(
                        b.getCid(), b.getCourseName(), b.getSemesterRequirement()
                ));
        }
        currentPlan.setSemesters(courseNodes.getSemesters());
        coursePlanRepository.save(currentPlan);
    }

    /**
     * Gets Schedule for User
     * @param uid userid
     * @return planned Courses
     */
    public List<ScheduleDraft.PlannedCourse> getCoursePlan(Integer uid){
        //TODO add custom exceptions for missing data
        return coursePlanRepository.findById(uid).get().getPlannedCourseList();
    }

    /**
     * Gets number semesters for the User's draft schedule
     * @param uid userid
     * @return
     */
    public Integer getSemesters(Integer uid){
        //TODO add custom exceptions for missing data
        return coursePlanRepository.findById(uid).get().getSemesters();
    }
}
