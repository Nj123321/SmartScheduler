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
        Optional<CoursePlan> buffer = coursePlanRepository.findById(courseNodes.getUid());
        CoursePlan currentPlan;
        if(buffer.isPresent()){
            currentPlan = buffer.get();
        }else{
            currentPlan = new CoursePlan();
            currentPlan.setUid(courseNodes.getUid());
        }
        for(buffNode b: courseNodes.getBuffNodeList()){
            currentPlan.getPlannedCourseList().add(new CoursePlan.PlannedCourse(
                    b.getCid(), b.getCourseName(), b.getSemesterNeeded()
            ));
        }
        coursePlanRepository.save(currentPlan);
    }
    public List<CoursePlan.PlannedCourse> getCoursePlan(Integer uid){
        return coursePlanRepository.findById(uid).get().getPlannedCourseList();
    }
}
