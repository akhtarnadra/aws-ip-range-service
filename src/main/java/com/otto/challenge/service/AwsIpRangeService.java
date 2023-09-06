package com.otto.challenge.service;

import com.otto.challenge.model.IpRange;
import com.otto.challenge.model.IpRangesResponse;
import com.otto.challenge.model.Region;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.otto.challenge.constants.GlobalApplicationConstants.NO_DATA_AVAILABLE_FOR_SELECTED_REGION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsIpRangeService {

    private final RestTemplate restTemplate;
    @Value("${aws.ip.ranges.url}")
    private String awsIpRangesUrl;

    private static final Map<Region, Set<String>> regionToApiRegionMap = Map.of(
            Region.EU, Set.of("eu-west-1", "eu-central-1", "eu-west-2", "eu-west-3", "eu-south-1", "eu-south-2", "eu-north-1"),
            Region.US, Set.of("us-east-1", "us-east-2", "us-west-1", "us-west-2", "us-gov-west-1", "us-gov-east-1"),
            Region.AF, Set.of("af-south-1"),
            Region.AP, Set.of("ap-east-1", "ap-southeast-1", "ap-southeast-2", "ap-southeast-3", "ap-southeast-4", "ap-northeast-1", "ap-northeast-2", "ap-northeast-3"),
            Region.CN, Set.of("cn-north-1"),
            Region.CA, Set.of("ca-central-1", "ca-west-1"),
            Region.SA, Set.of("sa-east-1"),
            Region.ALL, Set.of("eu-west-1", "eu-central-1", "eu-west-2", "eu-west-3", "eu-south-1", "eu-south-2", "eu-north-1",
                    "us-east-1", "us-east-2", "us-west-1", "us-west-2", "us-gov-west-1", "us-gov-east-1", "af-south-1",
                    "ap-east-1", "ap-southeast-1", "ap-southeast-2", "ap-southeast-3", "ap-southeast-4", "ap-northeast-1", "ap-northeast-2", "ap-northeast-3",
                    "cn-north-1", "ca-central-1", "ca-west-1", "sa-east-1")
    );

    @Cacheable(key = "#region.name()")
    public List<String> getIpRangesByRegion(Region region) {
        return CompletableFuture.supplyAsync(() -> {
            ResponseEntity<IpRangesResponse> responseEntity = restTemplate.getForEntity(awsIpRangesUrl, IpRangesResponse.class);
            IpRangesResponse ipRangesResponse = responseEntity.getBody();

            if (ipRangesResponse != null && ipRangesResponse.getPrefixes() != null) {
                return getFilteredIpRanges(region, ipRangesResponse.getPrefixes());
            } else {
                log.error(NO_DATA_AVAILABLE_FOR_SELECTED_REGION);
                throw new RuntimeException(NO_DATA_AVAILABLE_FOR_SELECTED_REGION);
            }
        }).exceptionally(ex -> {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }).join();
    }

    private List<String> getFilteredIpRanges(Region region, List<IpRange> ipRanges) {
        Set<String> validRegions = regionToApiRegionMap.getOrDefault(region, Collections.emptySet());

        return ipRanges.parallelStream()
                .filter(ipRange -> validRegions.isEmpty() || validRegions.contains(ipRange.getRegion()))
                .map(IpRange::getIpPrefix)
                .collect(Collectors.toList());
    }
}