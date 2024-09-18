package com.SmartScheduler.Scheduler.service;

import com.SmartScheduler.Scheduler.dto.CourseNodes;
import com.SmartScheduler.Scheduler.dto.PreRequisites;
import com.SmartScheduler.Scheduler.model.CoursePlan;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jmx.ParentAwareNamingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

import static java.lang.Math.sqrt;

@Service
@RequiredArgsConstructor
public class OptimizerService {
    private final WebClient.Builder webClientBuilder;
    private final SchedulerService schedulerService;
    public void getOptimalSchedule(Integer uid) throws JsonProcessingException {
        List<CoursePlan.PlannedCourse> schedule = schedulerService.getCoursePlan(uid);
        List<String> courses = new ArrayList<>();
        Map<String, Integer> userRequirements = new HashMap(); // fix coursename to id
        for(CoursePlan.PlannedCourse plannedCourse : schedule){
            courses.add(plannedCourse.getCname());
            if(plannedCourse.getSemester() != null) {
                System.out.print("REQUIRED: ");
                System.out.print(plannedCourse.getCname());
                System.out.print(" by ");
                System.out.println(plannedCourse.getSemester());
                userRequirements.put(plannedCourse.getCname(), plannedCourse.getSemester());
            }
        }
        if(courses == null){
            System.out.println("weird");
            return;
        }
        // Call Inventory Service
        String jsonBuffer = webClientBuilder.build().get()
                .uri("http://localhost:65106/api/courses",
                        uriBuilder -> uriBuilder.queryParam("targetCourses", courses).build())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        ObjectMapper objectMapper = new ObjectMapper();
        List<List<PreRequisites>> inventoryResponseArray = objectMapper.readValue(jsonBuffer, new TypeReference<List<List<PreRequisites>>>(){});
        System.out.println("GotJson");
        List<List<CourseNodes>> optimalSchedules = new ArrayList<>();
        int indexCacheSize = 0; //longest possibal interval of prereqs
        int sumCacheSize = 8; //# of seemsters  TO:DO HARD CODED RIGHT NOW, CHANGE LATER
        for(List<PreRequisites> chain: inventoryResponseArray) {
            indexCacheSize = Math.max(indexCacheSize, chain.size());
        }

        for(List<PreRequisites> lst : inventoryResponseArray){
            for(PreRequisites preq : lst){
                System.out.println(preq);
            }
        }

        List<List<Integer>> indexCache = new ArrayList<>();
        int jx = 0;
        for(List<PreRequisites> lst : inventoryResponseArray){
            indexCache.add(new ArrayList<>());
            for(int i = 0; i < lst.size(); i++){
                indexCache.get(jx).add(0);
            }
            jx += 1;
        }
        int[] lowest = {Integer.MAX_VALUE};
        backtrack(optimalSchedules, inventoryResponseArray, new Stack<>(),
                indexCache, new int[sumCacheSize], 0, lowest, 0, 0, new HashMap<>(), userRequirements);
        System.out.println(optimalSchedules.size());


        List<List<String>> buffer = new ArrayList<>();
        for(List<CourseNodes> opt : optimalSchedules){
            buffer = new ArrayList<>();
            for(int i = 0; i < sumCacheSize; i++) {
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
    }
    private void backtrack(List<List<CourseNodes>> returnValue, List<List<PreRequisites>> layers, Stack<CourseNodes> branchCache,
                           List<List<Integer>> indexCache, int[] sumCache, int totalSum, int[] lowestSum, int interval, int intervalI, Map<String, Integer> seen
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
                System.out.print(n.getCourseName());
                System.out.print(" ");
                System.out.print(n.getSemesterIndex());
                System.out.println("");
            }
            System.out.print("Total SUM:");
            System.out.println(totalSum);
            System.out.println("------------------");
            for(int s : sumCache){
                System.out.println(s);
            }
            if(totalSum < lowestSum[0]){
                System.out.println("cleared rv");
                returnValue.clear();
                lowestSum[0] = totalSum;
            }
            if(totalSum == lowestSum[0]){
                System.out.println("added??????????????????");
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
        System.out.println("not end of branch");
        //End of a prerequisite Interval/chain
        if (intervalI >= layers.get(interval).size()) {
            interval++;
            intervalI = 0;
        }
        PreRequisites p = layers.get(interval).get(intervalI);
        System.out.println("ParentIndex: ");
        System.out.println(p.getParentIndex());
        System.out.println(p.getCourseName());
        System.out.print("[");
        for(int i = 0; i < indexCache.get(interval).size(); i++){
            System.out.print(indexCache.get(interval).get(i));
            System.out.print(", ");
        }
        System.out.println("]");
        int boundary = p.getParentIndex() == -1 ? sumCache.length : indexCache.get(interval).get(p.getParentIndex());
        //no space
        System.out.println(boundary);
        if(boundary == 0){
            return;
        }

        System.out.println(p.getParentIndex());
        CourseNodes currentPlacement = new CourseNodes();
        //Pinned course
        if(userRequirements.get(p.getCourseName()) != null){
            int i = userRequirements.get(p.getCourseName());
            System.out.print("Requiring: ");
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
            System.out.print("p difficulty: ");
            System.out.println(p.getDifficulty());
            System.out.print("netsum: ");
            System.out.println(totalSum);
            if(Arrays.stream(sumCache).sum() != totalSum){
                System.out.println("whwowoowwoowowowo");
            }
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
            indexCache.get(interval).set(intervalI, i) ; // no need to refresh index cache between intervals, prev ones will alaways be in range by def
            System.out.print(i);
            System.out.print(p.getCourseName());
            System.out.print("cacheindex for got updated to: ");
            System.out.print(indexCache.get(interval).get(intervalI));
            System.out.print(" for ");
            System.out.println(intervalI);
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval, intervalI + 1, seen, userRequirements);
            seen.remove(p.getCourseName());
            branchCache.pop();

            //put back invaraints
            sumCache[i] = sumCacheBuffer;
//            System.out.print("Cache Buffer: ");
//            System.out.println(sumCacheBuffer);
            totalSum = totalSumBuffer;
            return;
        }
        //PreRequisite already fufilled (sharing pre-req paths along tree)
        if(seen.get(p.getCourseName()) != null){
            System.out.println("newWORKS");
            System.out.println("SEENCHILD: ");
            System.out.println(p.getCourseName());
            if(seen.get(p.getCourseName()) > indexCache.get(interval).get(p.getParentIndex())) { //TO:DO PATCH
                System.out.println("weird");
                return;
            }
            //skip, if shared prereq chain and prereq already placed
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval + 1, 0, seen, userRequirements);
            return;
        }
        System.out.print("Explore: ");
        System.out.print(p.getCourseName());
        System.out.print(", ");
        System.out.println(boundary);
        for(int i = boundary - 1; i >= 0; i--){
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
            System.out.print("p difficulty: ");
            System.out.println(p.getDifficulty());
            System.out.print("netsum: ");
            System.out.println(totalSum);
            if(Arrays.stream(sumCache).sum() != totalSum){
                System.out.println("whwowoowwoowowowo");
            }
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
            indexCache.get(interval).set(intervalI, i) ; // no need to refresh index cache between intervals, prev ones will alaways be in range by def
            System.out.print(i);
            System.out.print(p.getCourseName());
            System.out.print("cacheindex for got updated to: ");
            System.out.print(indexCache.get(interval).get(intervalI));
            System.out.print(" for ");
            System.out.println(intervalI);
            backtrack(returnValue, layers, branchCache, indexCache, sumCache, totalSum, lowestSum, interval, intervalI + 1, seen, userRequirements);
            seen.remove(p.getCourseName());
            branchCache.pop();

            //put back invaraints
            sumCache[i] = sumCacheBuffer;
//            System.out.print("Cache Buffer: ");
//            System.out.println(sumCacheBuffer);
            totalSum = totalSumBuffer;
        }
    }
}
