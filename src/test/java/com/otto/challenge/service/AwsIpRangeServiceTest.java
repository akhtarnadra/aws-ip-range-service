package com.otto.challenge.service;

import com.otto.challenge.model.IpRange;
import com.otto.challenge.model.IpRangesResponse;
import com.otto.challenge.model.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.otto.challenge.constants.TestConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwsIpRangeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AwsIpRangeService awsIpRangeService;

    @Value("${aws.ip.ranges.url}")
    private String awsIpRangesUrl;

    @BeforeEach
    public void setUp() {
        // Inject the value of awsIpRangesUrl into the service using ReflectionTestUtils
        ReflectionTestUtils.setField(awsIpRangeService, REFLECTION_FIELD_NAME, AWS_IP_RANGE_SERVICE_API);
    }

    @Test
    public void testGetIpRangesByRegion() {
        // Given
        // Create sample data for IpRangesResponse
        IpRangesResponse ipRangesResponse = new IpRangesResponse();
        IpRange ipRange1 = new IpRange("10.0.0.0/24", "us-east-1", "AMAZON", "us-east-1");
        IpRange ipRange2 = new IpRange("10.0.0.0/24", "eu-west-3", "AMAZON", "eu-west-3");
        ipRangesResponse.setPrefixes(Arrays.asList(ipRange1, ipRange2));

        // When
        // Mock the behavior of the RestTemplate
        when(restTemplate.getForEntity(AWS_IP_RANGE_SERVICE_API, IpRangesResponse.class)).thenReturn(ResponseEntity.ok(ipRangesResponse));

        // Then
        // Test the service method
        List<String> ipRanges = awsIpRangeService.getIpRangesByRegion(Region.EU);
        assertEquals(1, ipRanges.size());
    }

    @Test
    public void testGetIpRangesByRegionWithException() {
        // When
        // Mock the behavior of the RestTemplate to throw an exception
        when(restTemplate.getForEntity(AWS_IP_RANGE_SERVICE_API, ResponseEntity.class)).thenThrow(new RuntimeException(TEST_EXCEPTION));

        // Throws
        // Test the service method, expecting an exception
        assertThrows(RuntimeException.class, () -> {
            awsIpRangeService.getIpRangesByRegion(null);
        });
    }
}