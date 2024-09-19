package com.SmartScheduler.Scheduler.service;

import com.SmartScheduler.Scheduler.dto.PreRequisites;
import com.SmartScheduler.Scheduler.model.ScheduleDraft;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static java.lang.Math.sqrt;

@Service
@RequiredArgsConstructor
public class OptimizerService {
    private final WebClient.Builder webClientBuilder;
    private final SchedulerService schedulerService;
    private boolean courseDistanceFlag = true; // can add weighting of how much each course follows the next course
    //how much of a prereq it is, so that [ 61A-| 61B-| CS70-| CS170-| 61C-| ] doesn't happen
    public List<List<String>> getOptimalSchedule(Integer uid) throws JsonProcessingException {
        // Load in User Schedule Draft Data
        System.out.println("Loading User Data");
        List<ScheduleDraft.PlannedCourse> schedule = schedulerService.getCoursePlan(uid);
        List<String> courses = new ArrayList<>();
        Map<String, Integer> userRequirements = new HashMap(); // fix coursename to id
        for(ScheduleDraft.PlannedCourse plannedCourse : schedule){
            courses.add(plannedCourse.getCname());
            if(plannedCourse.getSemesterRequirement() != -1){
                userRequirements.put(plannedCourse.getCname(), plannedCourse.getSemesterRequirement());
            }
        }

        // Get Class Prerequisites for Schedule
        System.out.println("fetching Prereqs");
        String jsonBuffer = webClientBuilder.build().get()
                .uri("http://localhost:65106/api/courses",
                        uriBuilder -> uriBuilder.queryParam("targetCourses", courses).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<PreRequisites>> inventoryResponseArray = objectMapper.readValue(jsonBuffer, new TypeReference<List<List<PreRequisites>>>(){});

        //Prepare Caches for backtracking
        System.out.println("BackTracking prep");
        List<List<CourseNodes>> optimalSchedules = new ArrayList<>();
        int largestPreReq = 0; // cache size to look at parent indexes
        for(List<PreRequisites> lst : inventoryResponseArray){
            largestPreReq = Math.max(largestPreReq, lst.size());
        }

        int[] lowest = {Integer.MAX_VALUE};
        System.out.println("BackTrack start");
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
     * Backtrack Courses
     * Assumes integeres in condition are 1-indexed
     * @param returnValue
     * @param layers
     * @param branchCache
     * @param indexCache
     * @param sumCache
     * @param totalSum
     * @param lowestSum
     * @param interval
     * @param intervalI
     * @param seen
     * @param userRequirements
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
            System.out.print("Total SUM:");
            System.out.println(totalSum);
            if(totalSum < lowestSum[0]){
                System.out.println("cleared return cache");
                returnValue.clear();
                lowestSum[0] = totalSum;
            }
            if(totalSum == lowestSum[0]){
                System.out.println("added new optimal solution");
                System.out.println(lowestSum[0]);
                List<CourseNodes> whack = new ArrayList<>();
                for(CourseNodes cn : branchCache) {
                    whack.add(CourseNodes.builder()
                            .semesterIndex(cn.getSemesterIndex())
                            .difficulty(cn.getDifficulty())
                            .courseName(cn.getCourseName())
                            .build());
                }
                returnValue.add(whack);
            }
            return;
        }
        //End of a prerequisite Interval/chain - move onto NEXT, end of array will not reach here because above condition
        //catches it
        System.out.println("Update Interval");
        if (intervalI >= layers.get(interval).size()) {
            interval++;
            intervalI = 0;
        }

        // IndexCache Debug
        PreRequisites p = layers.get(interval).get(intervalI);
        System.out.println("ParentIndex: ");
        System.out.println(p.getParentIndex());
        System.out.println(p.getCourseName());
        System.out.print("[");
        for(int i = 0; i < indexCache.length; i++){
            System.out.print(indexCache[i]);
            System.out.print(", ");
        }
        System.out.println("]");

        // search space for current node in course interval
        // 1-indexed because of users?
        System.out.println("Setting bounds");
        int searchLeftBound = 1;
        int searchRightBound = p.getParentIndex() == -1 ? sumCache.length : indexCache[p.getParentIndex()];
        //no space - bad branch
        System.out.println(searchRightBound);
        if(searchRightBound == 0){
            return; // prune
        }

        CourseNodes currentPlacement = new CourseNodes();
        //Pinned course
        System.out.println("Checking Pinned Course");
        if(userRequirements.get(p.getCourseName()) != null){
            int i = userRequirements.get(p.getCourseName());
            System.out.print("Requiring: ");
            System.out.print(p.getCourseName());
            System.out.print(":    ");
            System.out.println(i);
            searchLeftBound = userRequirements.get(p.getCourseName());
            searchRightBound = searchLeftBound;
        }
        //PreRequisite already fufilled (sharing pre-req paths along tree)
        System.out.println("Checking if already seen prereq");
        if(seen.get(p.getCourseName()) != null){
            if(seen.get(p.getCourseName()) > indexCache[p.getParentIndex()]) { //TO:DO PATCH
                System.out.println("weird");
                return;
            }
            //skip, if shared prereq chain and prereq already placed
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval + 1, 0, seen, userRequirements);
            return;
        }
        System.out.print("Explore: ");
        System.out.print(p.getCourseName());
        System.out.print(" from: ");
        System.out.print(searchLeftBound);
        System.out.println(" to ");
        System.out.println(searchRightBound);
        // Place Course node
        for(int i = searchRightBound - 1; i >= searchLeftBound - 1; i--){
            System.out.print("Choosing: ");
            System.out.print(p.getCourseName());
            System.out.print(":    ");
            System.out.println(i);
            seen.put(p.getCourseName(), i);
            //store for next interation
            int sumCacheBuffer = sumCache[i];
            int totalSumBuffer = totalSum;

            totalSum -= sumCache[i];
//            System.out.println(p.getDifficulty());
//            System.out.println(sumCache[i]);
            sumCache[i] = (int)Math.pow(sqrt(sumCache[i]) + p.getDifficulty(), 2); // always integer - sqrt always whole
            totalSum += sumCache[i];
            int courseDistance = 0;
            if(courseDistanceFlag) {
                if (p.getParentIndex() != -1) {
                    courseDistance = indexCache[p.getParentIndex()] - i;
                    totalSum += courseDistance;
                } else {
                    System.out.print("jlsdjfkjs"); //TODO
                }
            }
            System.out.print("p difficulty: ");
            System.out.println(p.getDifficulty());
            System.out.print("netsum: ");
            System.out.println(totalSum);
            System.out.print("sumCache length: ");
            System.out.println(sumCache.length);
            System.out.println("SumCache:");
            System.out.print("[ ");
            for(int j  = 0; j < sumCache.length; j++) {
                System.out.print(sumCache[j]);
                System.out.print(", ");
            }
            System.out.println("]");

            currentPlacement.setCourseName(p.getCourseName());
            currentPlacement.setDifficulty(p.getDifficulty());
            currentPlacement.setSemesterIndex(i);
            branchCache.push(currentPlacement);
            int indexCacheValue = indexCache[intervalI];
            indexCache[intervalI] = i; // no need to refresh index cache between intervals, prev ones will alaways be in range by def
            System.out.print(i);
            System.out.print(p.getCourseName());
            System.out.print("cacheindex for got updated to: ");
            System.out.print(indexCache[intervalI]);
            System.out.print(" for ");
            System.out.println(intervalI);
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval, intervalI + 1, seen, userRequirements);
            seen.remove(p.getCourseName());
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
