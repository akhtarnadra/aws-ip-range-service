package com.otto.challenge.controller;

import com.otto.challenge.model.Region;
import com.otto.challenge.service.AwsIpRangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AwsIpRangeControllerTest {

    @Mock
    private AwsIpRangeService awsIpRangeService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize annotated mocks
    }

    @Test
    public void testGetIpRangesByRegion() {
        // Create a sample list of IP ranges
        List<String> sampleIpRanges = Arrays.asList("10.0.0.0/24", "192.168.0.0/24");

        // Mock the behavior of the service method
        when(awsIpRangeService.getIpRangesByRegion(Region.EU)).thenReturn(sampleIpRanges);

        AwsIpRangeController awsIpRangeController = new AwsIpRangeController(awsIpRangeService);
        ResponseEntity<String> responseEntity = awsIpRangeController.getIpRangesByRegion(Region.EU);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("10.0.0.0/24\n192.168.0.0/24", responseEntity.getBody());
    }

    @Test
    public void testGetIpRangesByAnInvalidRegion() {
        // Mock the behavior of the service method for an invalid region
        when(awsIpRangeService.getIpRangesByRegion(null)).thenReturn(Collections.emptyList());

        AwsIpRangeController awsIpRangeController = new AwsIpRangeController(awsIpRangeService);
        ResponseEntity<String> responseEntity = awsIpRangeController.getIpRangesByRegion(null);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("", responseEntity.getBody());
    }
}