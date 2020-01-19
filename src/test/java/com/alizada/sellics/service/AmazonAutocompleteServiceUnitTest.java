package com.alizada.sellics.service;

import com.alizada.sellics.model.SearchValuesEstimation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestOperations;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AmazonAutocompleteServiceUnitTest {
    @Mock
    private RestOperations restOperations;
    private static String firstResponse = "[\"s\",[\"swimsuits for women\",\"shower curtain\",\"shower curtain liner\",\"summer dresses for women\"," +
            "\"shoe rack\",\"scale\",\"squishies\",\"sleep mask\",\"sd card\",\"sunscreen\"],[{\"nodes\":[{\"name\":\"Sports & Fitness\"," +
            "\"alias\":\"sports-and-fitness\"}]},{},{},{},{},{},{},{},{},{}],[],\"3PYAIK51OYB4G\"]\n";

    private static String secondResponse = "[\"s8\",[\"s8 case\",\"s8 screen protector\",\"s8 plus case\",\"s8 plus screen protector\"," +
            "\"s8 charger\",\"s8 phone case\",\"s8 active case\"," +
            "\"s8\",\"s8 screen protector tempered glass\",\"s8 plus tempered glass\"],[{\"nodes\":" +
            "[{\"name\":\"Cell Phones & Accessories\",\"alias\":\"mobile\"}]},{},{},{},{},{},{},{},{},{}],[],\"1LD8KAE5T68ES\"]\n";

    private static String amazonApiUrl = "https://completion.amazon.com/search/complete?search-alias=aps&client=amazon-search-ui&mkt=1&q=";

    private AmazonAutocompleteService amazonAutocompleteService;

    @Before
    public void setup() {
        restOperations = Mockito.mock(RestOperations.class);
        when(restOperations.getForObject(eq(amazonApiUrl + "s"),any())).thenReturn(firstResponse);
        when(restOperations.getForObject(eq(amazonApiUrl + "s8"),any())).thenReturn(secondResponse);
        amazonAutocompleteService = new AmazonAutocompleteService(restOperations);
    }

    @Test
    public void shouldReturnEstimatedValueByNullKeyword(){
        //given
        String keyword = null;
        //when
        SearchValuesEstimation estimation = amazonAutocompleteService.estimateSearchVolume(keyword);
        //then
        assertThat(estimation.getScore()).isEqualTo(0);
    }

    @Test
    public void shouldReturnEstimatedValueByEmptyKeyword(){
        //given
        String keyword = "";
        //when
        SearchValuesEstimation estimation = amazonAutocompleteService.estimateSearchVolume(keyword);
        //then
        assertThat(estimation.getScore()).isEqualTo(0);
    }

    @Test
    public void shouldReturnEstimatedValueByGivenKeyword(){
        //given
        String keyword = "s8";
        //when
        SearchValuesEstimation estimation = amazonAutocompleteService.estimateSearchVolume(keyword);
        //then
        assertThat(estimation.getScore()).isEqualTo(15);
    }
}
