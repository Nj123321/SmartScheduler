package com.SmartScheduler.Scheduler.repository;

import com.SmartScheduler.Scheduler.model.ScheduleDraft;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoursePlanRepository extends MongoRepository<ScheduleDraft, Integer> {
}
