package com.alizada.sellics.controller;

import com.alizada.sellics.model.SearchValuesEstimation;
import com.alizada.sellics.service.AmazonAutocompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ScoreController {
    private AmazonAutocompleteService amazonAutocomplete;

    @Autowired
    public ScoreController(AmazonAutocompleteService amazonAutocomplete) {
        this.amazonAutocomplete = amazonAutocomplete;
    }

    @RequestMapping(value = "/estimate", method = RequestMethod.GET)
    public SearchValuesEstimation estimate(@RequestParam(value = "keyword", defaultValue = "") String keyword){
        return amazonAutocomplete.estimateSearchVolume(keyword);
    }

}
