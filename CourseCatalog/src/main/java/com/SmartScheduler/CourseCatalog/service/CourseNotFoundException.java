package com.SmartScheduler.CourseCatalog.service;

public class CourseNotFoundException extends Exception{
    public CourseNotFoundException(Integer cid){
        super("Could not find course with cid: " + cid.toString());
    }
}
