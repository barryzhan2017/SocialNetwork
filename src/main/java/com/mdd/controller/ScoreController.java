package com.mdd.controller;

import com.mdd.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ScoreController {

    private ScoreService scoreService;

    @Autowired
    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    /**
     *
     * @param sourceId Id of source person
     * @param targetId Id of target person
     * @param trustLevel Which trust level to get the rating
     * @return NO_AVAILABLE_RATING_NOW (-1.0) if there is no link between them.
     */
    @GetMapping("/score/{sourceId}/{targetId}/{trustLevel}")
    public Map<String, Object> getScore(@PathVariable long sourceId, @PathVariable long targetId,
                        @PathVariable int trustLevel) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> payload = new HashMap<>();
        double score = scoreService.getScore(sourceId, targetId, trustLevel);
        payload.put("score", score);
        long endTime = System.currentTimeMillis();
        System.out.println("Running time for source id " + sourceId +
                " targetId " + targetId + " at trust level " + trustLevel +" is " +
                (endTime - startTime) + "ms.");
        return payload;
    }


    // Note to judge if the list of child nodes not equal to the number of trust level
    @PutMapping("/score")
    public Map<String, Object> updateScore(@RequestBody Map map) {
        int level = (int)map.get("trustLevel");
        long sourceId = (int)map.get("sourceId");
        long targetId = (int)map.get("targetId");
        Map<String, Object> payload = new HashMap<>();
        scoreService.updateScore(sourceId, targetId, level);
        payload.put("message", "Update successfully!");
        return payload;
    }
}
