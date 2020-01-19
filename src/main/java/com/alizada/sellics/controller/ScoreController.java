package com.alizada.sellics.controller;

import com.alizada.sellics.model.SearchValuesEstimation;
import com.alizada.sellics.service.AmazonAutocompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;

@RestController
public class ScoreController {
    private static final int TIMEOUT_MILLIS = 10000;

    private AmazonAutocompleteService amazonAutocomplete;

    @Autowired
    public ScoreController(AmazonAutocompleteService amazonAutocomplete) {
        this.amazonAutocomplete = amazonAutocomplete;
    }

    @RequestMapping(value = "/estimate", method = RequestMethod.GET)
    public WebAsyncTask<SearchValuesEstimation> estimate(@RequestParam(value = "keyword", defaultValue = "") String keyword){
        Callable<SearchValuesEstimation> callable = () -> amazonAutocomplete.estimateSearchVolume(keyword);
        return new WebAsyncTask<>(TIMEOUT_MILLIS, callable);
    }
}
