package com.otto.challenge.service;

import com.otto.challenge.model.IpRange;
import com.otto.challenge.model.IpRangesResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static com.otto.challenge.constants.TestConstants.AWS_IP_RANGE_SERVICE_API;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AwsIpRangeServiceTest {

    private AwsIpRangeService awsIpRangeService;
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        awsIpRangeService = new AwsIpRangeService(restTemplate, AWS_IP_RANGE_SERVICE_API);
    }

    @Test
    public void testGetIpRangesByRegion() {
        // Create sample data for IpRangesResponse
        IpRangesResponse ipRangesResponse = new IpRangesResponse();
        IpRange ipRange1 = new IpRange("10.0.0.0/24", "us-east-1", "AMAZON", "us-east-1");
        IpRange ipRange2 = new IpRange("10.0.0.0/24", "eu-west-3", "AMAZON", "eu-west-3");
        ipRangesResponse.setPrefixes(Arrays.asList(ipRange1, ipRange2));

        // Mock the behavior of the RestTemplate
        when(restTemplate.getForEntity("https://example.com/aws-ip-ranges", IpRangesResponse.class)).thenReturn(ResponseEntity.ok(ipRangesResponse));

        // Test the service method
        List<String> ipRanges = awsIpRangeService.getIpRangesByRegion(null);
        assertEquals(2, ipRanges.size());
    }
}