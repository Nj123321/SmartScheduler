package com.SmartScheduler.Scheduler.service;

import com.SmartScheduler.Scheduler.dto.PreReqDTO;
import com.SmartScheduler.Scheduler.dto.PreRequisites;
import com.SmartScheduler.Scheduler.model.ScheduleDraft;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static java.lang.Math.sqrt;

/**
 * Service for OptimizerController endpoint, implements scheudle backtracking algorithim
 */
@Service
@RequiredArgsConstructor
public class OptimizerService {
    private final WebClient.Builder webClientBuilder;
    private final SchedulerService schedulerService;
    private boolean courseDistanceFlag = true; // can add weighting of how much each course follows the next course
    //how much of a prereq it is, so that [ 61A-| 61B-| CS70-| CS170-| 61C-| ] doesn't happen

    Logger logger = LoggerFactory.getLogger(OptimizerService.class);

    /**
     * Gets the optimal Schedule for the given user's schedule draft
     * @param uid user-id
     * @return an optimal schedule if there exists one
     * @throws JsonProcessingException
     */
    public List<List<String>> getOptimalSchedule(Integer uid) throws JsonProcessingException {
        // Load in User Schedule Draft Data
        logger.info("Loading User Data... uid: " + uid.toString());
        List<ScheduleDraft.PlannedCourse> schedule = schedulerService.getCoursePlan(uid);
        List<Integer> draftScheduleListCourseID = new ArrayList<>();
        Map<String, Integer> userRequirements = new HashMap(); // fix coursename to id
        for(ScheduleDraft.PlannedCourse plannedCourse : schedule){
            draftScheduleListCourseID.add(plannedCourse.getCid());
            if(plannedCourse.getSemesterRequirement() != -1){
                userRequirements.put(plannedCourse.getCname(), plannedCourse.getSemesterRequirement());
            }
        }

        // Get Class Prerequisites for Schedule
        logger.info("Fetching Prerequisites for ... uid: " + uid.toString());
        PreReqDTO jsonBuffer = webClientBuilder.build().get()
                .uri("http://CourseCatalog/courses", //TODO discovery client
                        uriBuilder -> uriBuilder.queryParam("listOfCourseIDs", draftScheduleListCourseID).build())
                .retrieve()
                .bodyToMono(PreReqDTO.class)
                .block();
        List<List<PreRequisites>> inventoryResponseArray = jsonBuffer.getPrereqchain();

        //Prepare Caches for backtracking
        logger.info("Preparing backtrack caches for ... uid: " + uid.toString());
        List<List<CourseNodes>> optimalSchedules = new ArrayList<>();
        int largestPreReq = 0; // cache size to look at parent indexes
        for(List<PreRequisites> lst : inventoryResponseArray){
            largestPreReq = Math.max(largestPreReq, lst.size());
        }

        int[] lowest = {Integer.MAX_VALUE};
        logger.info("Starting Backtracking for: " + uid.toString());
        backtrack(optimalSchedules, inventoryResponseArray, new Stack<>(),
                new int[largestPreReq], new int[schedulerService.getSemesters(uid)], 0, lowest, 0, 0, new HashMap<>(), userRequirements);

        //temporary, maybe store in redis
        System.out.println("Printing results");
        List<List<String>> buffer = new ArrayList<>();
        for(List<CourseNodes> opt : optimalSchedules){
            buffer = new ArrayList<>();
            for(int i = 0; i < schedulerService.getSemesters(uid); i++) {
                buffer.add(new ArrayList<>());
            }
            for(CourseNodes node : opt) {
                buffer.get(node.getSemesterIndex()).add(node.getCourseName());
            }
            System.out.print("[ ");
            for(int j  = 0; j < buffer.size(); j++) {
                for(int i = 0; i < buffer.get(j).size(); i++){
                    System.out.print(buffer.get(j).get(i));
                    System.out.print("-");
                }
                System.out.print("| ");
            }
            System.out.println("]");
        }
        System.out.println("hereend");
        return buffer;
    }

    /**
     * Backtrack helper function for getOptimalSchedule
     * Assumes integeres in condition are 1-indexed
     * @param returnValue List of semesters, each semester can contain multiple courses
     * @param layers Prerequisites
     * @param branchCache backtracking/recursion stack
     * @param indexCache cache for semesters of parents
     * @param sumCache cache of individual semester sum
     * @param totalSum evaluation function: sum(sumCache) + length of each "course" interval - for course proximity
     * @param lowestSum minimize totalSum - lowest totalSum
     * @param interval index for which prerequisite "chain" we are currently exploring
     * @param intervalI index for which course-node along the chain we are exploring
     * @param seen map to cache already seen prerequisites along the search path (diff courses can share same prereq) - order of which course "takes" it first shouldn't matter
     * @param userRequirements user pinning courses
     */
    private void backtrack(List<List<CourseNodes>> returnValue, List<List<PreRequisites>> layers, Stack<CourseNodes> branchCache,
                           int[] indexCache, int[] sumCache, int totalSum, int[] lowestSum, int interval, int intervalI, Map<String, Integer> seen
                            , Map<String, Integer> userRequirements){
        //indexCache size, sumCache size = #of semesters

        //prune
        if(totalSum > lowestSum[0]) {
            return;
        }
        //End of branch - found all positions
        if (interval >= layers.size() || (interval >= layers.size() - 1 && intervalI >= layers.get(interval).size())){
            System.out.println("end of a branch");
            for(CourseNodes n: branchCache){
                System.out.print(n.getCourseName() + " " + n.getSemesterIndex());
                System.out.println("");
            }
            logger.info("total sum for banch: " + Integer.toString(totalSum));
            if(totalSum < lowestSum[0]){
                logger.info("Cleared Cache");
                returnValue.clear();
                lowestSum[0] = totalSum;
            }
            if(totalSum == lowestSum[0]){
                List<CourseNodes> brancheCacheCopy = new ArrayList<>();
                for(CourseNodes cn : branchCache) {
                    brancheCacheCopy.add(CourseNodes.builder()
                            .semesterIndex(cn.getSemesterIndex())
                            .difficulty(cn.getDifficulty())
                            .courseName(cn.getCourseName())
                            .build());
                }
                logger.info("Added new optimal solution: " + brancheCacheCopy.toString());
                returnValue.add(brancheCacheCopy);
            }
            return;
        }
        //End of a prerequisite Interval/chain - move onto NEXT, end of array will not reach here because above condition
        //catches it
        logger.info("Updating Interval");
        if (intervalI >= layers.get(interval).size()) {
            interval++;
            intervalI = 0;
        }

        // IndexCache Debug
        PreRequisites courseSearch = layers.get(interval).get(intervalI);

        // search space for current node in course interval
        // 1-indexed because of users?
        logger.info("Setting Search Bounds");
        int searchLeftBound = 1;
        int searchRightBound = courseSearch.getParentIndex() == -1 ? sumCache.length : indexCache[courseSearch.getParentIndex()];
        //no space - bad branch
        System.out.println(searchRightBound);
        if(searchRightBound == 0){
            return; // prune
        }

        CourseNodes currentPlacement = new CourseNodes();
        //Pinned course
        logger.info("Checking if Course is Pinned");
        if(userRequirements.get(courseSearch.getCourseName()) != null){
            int i = userRequirements.get(courseSearch.getCourseName());
            logger.info("Course found to be pinned: " + courseSearch.toString());
            searchLeftBound = userRequirements.get(courseSearch.getCourseName());
            searchRightBound = searchLeftBound;
        }
        //PreRequisite already fufilled (sharing pre-req paths along tree)
        logger.info("Checking if PreReq was already fufilled");
        if(seen.get(courseSearch.getCourseName()) != null){
            if(seen.get(courseSearch.getCourseName()) > indexCache[courseSearch.getParentIndex()]) { //TO:DO PATCH
                System.out.println("weird");
                return;
            }
            //skip, if shared prereq chain and prereq already placed
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval + 1, 0, seen, userRequirements);
            return;
        }
        logger.info(String.format("Exploring for %s: from %d startIndex to %d endIndex",
                courseSearch.getCourseName(), searchLeftBound, searchRightBound));
        // Place Course node
        for(int i = searchRightBound - 1; i >= searchLeftBound - 1; i--){
            logger.info(String.format("Choosing index: %d for Course: %s", i, courseSearch.toString()) );
            seen.put(courseSearch.getCourseName(), i);
            //store for next interation
            int sumCacheBuffer = sumCache[i];
            int totalSumBuffer = totalSum;

            totalSum -= sumCache[i];
//            System.out.println(p.getDifficulty());
//            System.out.println(sumCache[i]);
            sumCache[i] = (int)Math.pow(sqrt(sumCache[i]) + courseSearch.getDifficulty(), 2); // always integer - sqrt always whole
            totalSum += sumCache[i];
            int courseDistance = 0;
            if(courseDistanceFlag) {
                if (courseSearch.getParentIndex() != -1) {
                    courseDistance = indexCache[courseSearch.getParentIndex()] - i;
                    totalSum += courseDistance;
                } else {
                    System.out.print("jlsdjfkjs"); //TODO
                }
            }

            currentPlacement.setCourseName(courseSearch.getCourseName());
            currentPlacement.setDifficulty(courseSearch.getDifficulty());
            currentPlacement.setSemesterIndex(i);
            branchCache.push(currentPlacement);
            int indexCacheValue = indexCache[intervalI];
            indexCache[intervalI] = i; // no need to refresh index cache between intervals, prev ones will alaways be in range by def
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval, intervalI + 1, seen, userRequirements);
            seen.remove(courseSearch.getCourseName());
            branchCache.pop();

            if(courseDistanceFlag)
                totalSum -= courseDistance;

            //put back invaraints
            indexCache[intervalI] = indexCacheValue;
            sumCache[i] = sumCacheBuffer;
            totalSum = totalSumBuffer;
        }
    }
}
