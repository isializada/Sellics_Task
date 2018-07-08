package com.alizada.sellics.service;

import com.alizada.sellics.model.SearchValuesEstimation;
import com.alizada.sellics.util.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AmazonAutocompleteService {

    private RestOperations restOperations;
    @Autowired
    public AmazonAutocompleteService(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    public SearchValuesEstimation estimateSearchVolume(String keyword) {

        Logger.getLogger().info("Requested keyword-> " + keyword);

        SearchValuesEstimation searchValuesEstimation = new SearchValuesEstimation();
        searchValuesEstimation.setKeyword(keyword);

        if(keyword == null || keyword.equals("")){
            searchValuesEstimation.setScore(0);
        }else{
            searchValuesEstimation.setScore(calculateKeywordScore(keyword));
        }


        Logger.getLogger().info("Responsed score->" + searchValuesEstimation.getScore());

        return searchValuesEstimation;
    }

    private Integer calculateKeywordScore(String keyword){

        Integer typingResultsScoresSum = 0;
        Integer maxTypingResultsScoreSum = keyword.length()*10; //10 is max index
        Integer typingResultsScore = -1;
        Integer score;

        ObjectMapper mapper = new ObjectMapper();
        String amazonAutocompleteUrl = "http://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=";
        //Loop keyword by typing inputs (linux -> l, li,lin, linu, linux)
        for(int i=1; i<=keyword.length();i++){
            String requestUrl = amazonAutocompleteUrl + keyword.substring(0,i);

            typingResultsScore = calculateTypingResultScore(requestUrl, keyword, mapper);
            if(typingResultsScore != -1){
                //if keyword is exists in top ten list, calculate input score by index
                //10 is maximal index
                typingResultsScoresSum += 10 - typingResultsScore;
            }
        }
        //find sum of scores average
        score = (int)(((double)typingResultsScoresSum/maxTypingResultsScoreSum)*100);

        return score;
    }

    private Integer calculateTypingResultScore(String url, String keyword, ObjectMapper mapper) {
        try{
            //get json by get request
            String result = restOperations.getForObject(url, String.class);

            //map json and get top ten list
            Object[] objArray = mapper.readValue(result, Object[].class);
            List<String> topTenList = (List<String>)Arrays.asList(objArray[1]).get(0);

            return topTenList.indexOf(keyword);
        }catch(Exception ex){
            Logger.getLogger().error(ex.getMessage(), ex);

            return null;
        }
    }
}

