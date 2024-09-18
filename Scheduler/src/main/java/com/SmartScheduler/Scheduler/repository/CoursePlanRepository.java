package com.SmartScheduler.Scheduler.repository;

import com.SmartScheduler.Scheduler.model.CoursePlan;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoursePlanRepository extends MongoRepository<CoursePlan, Integer> {
}
