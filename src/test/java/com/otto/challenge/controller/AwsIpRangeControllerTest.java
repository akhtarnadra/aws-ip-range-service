package com.otto.challenge.controller;

import com.otto.challenge.service.AwsIpRangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
        when(awsIpRangeService.getIpRangesByRegion(null)).thenReturn(sampleIpRanges);

        AwsIpRangeController awsIpRangeController = new AwsIpRangeController(awsIpRangeService);
        ResponseEntity<String> responseEntity = awsIpRangeController.getIpRangesByRegion(null);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals("10.0.0.0/24\n192.168.0.0/24", responseEntity.getBody());
    }
}