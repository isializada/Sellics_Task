package com.alizada.sellics.service;

import com.alizada.sellics.model.SearchValuesEstimation;
import com.alizada.sellics.util.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
/**
 * service calculates the score of keyword
 */
public class AmazonAutocompleteService {

    private final RestOperations restOperations;

    public AmazonAutocompleteService(final RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    /**
     * estimates keyword score
     * @param keyword -> requested text
     * @return -> estimated score
     */
    public SearchValuesEstimation estimateSearchVolume(final String keyword) {
        Logger.getLogger().debug("Keyword-> " + keyword);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final SearchValuesEstimation searchValuesEstimation = new SearchValuesEstimation();
        searchValuesEstimation.setKeyword(keyword);

        if(notEmptyKeyword(keyword)){
            searchValuesEstimation.setScore(0);
        }else{
            searchValuesEstimation.setScore(calculateKeywordScore(keyword));
        }


        Logger.getLogger().debug("Score->" + searchValuesEstimation.getScore());

        return searchValuesEstimation;
    }

    /**
     * calculates score by keyword. It takes all prefixes of keyword (l, li,lin, linu, linux)
     * and finds scores for all of them and calculates the average by max score
     * @param keyword -> requested text
     * @return score
     */
    private Integer calculateKeywordScore(final String keyword){
        int totalScore = 0;
        final int maxIndex = 10;
        final int maxScore = keyword.length()*maxIndex;
        final String amazonAutocompleteUrl = "https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=";

        //Loop prefixes (linux -> l, li,lin, linu, linux)
        for(int i=1; i<=keyword.length(); i++){
            final String requestUrl = amazonAutocompleteUrl + keyword.substring(0,i);
            totalScore += maxIndex - getScoreByPrefix(requestUrl, keyword);
        }

        //find sum of scores average
        return (int)(((double)totalScore/maxScore)*100);
    }

    /**
     * @param url for current prefix
     * @param keyword requested keyword
     * @return index of current keyword in responsed list from amazon
     */
    private Integer getScoreByPrefix(final String url, final String keyword) {
        final List<String> autoCompletedList = getAutoCompletedKeywords(url);
        final int index = autoCompletedList.indexOf(keyword);

        if(index == -1){
            return 10;
        }
        return index;
    }

    /**
     * find all auto completed keywords by prefix
     * @param url for current prefix
     * @return auto completed keywords list by current prefix
     */
    private List<String> getAutoCompletedKeywords(String url){

        final ObjectMapper mapper = new ObjectMapper();
        final String resultJson = restOperations.getForObject(url, String.class);

        if(null == resultJson){
            throw new RuntimeException("result from amazon server can not be null");
        }

        try{
            final Object[] resultArray = mapper.readValue(resultJson, Object[].class);
            return (List<String>)Arrays.asList(resultArray[1]).get(0);
        }catch(IOException ex){
            Logger.getLogger().error(ex.getMessage(), ex);
            throw new RuntimeException("cannot map the result");
        }
    }

    private boolean notEmptyKeyword(String keyword) {
        return keyword == null || keyword.equals("");
    }
}