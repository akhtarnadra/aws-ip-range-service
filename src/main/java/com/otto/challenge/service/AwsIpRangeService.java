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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.otto.challenge.constants.GlobalApplicationConstants.GENERIC_ERROR;
import static com.otto.challenge.constants.GlobalApplicationConstants.NO_DATA_AVAILABLE_FOR_SELECTED_REGION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsIpRangeService {

    private final RestTemplate restTemplate;
    @Value("${aws.ip.ranges.url}")
    private String awsIpRangesUrl;

    private static final Map<Region, String> regionToApiRegionMap = new HashMap<>();

    static {
        regionToApiRegionMap.put(Region.EU, "eu-west-1,eu-central-1,eu-west-3,eu-west-2");
        regionToApiRegionMap.put(Region.US, "us-east-1");
        regionToApiRegionMap.put(Region.AF, "af-south-1");
        regionToApiRegionMap.put(Region.AP, "ap-southeast-1");
        regionToApiRegionMap.put(Region.CN, "cn-north-1");
        regionToApiRegionMap.put(Region.CA, "ca-central-1");
        regionToApiRegionMap.put(Region.SA, "sa-east-1");
        regionToApiRegionMap.put(Region.ALL, Region.EU + ","+ Region.US + ","+ Region.AF + ","+ Region.AP + ","+ Region.CN + ","+ Region.CA + ","+ Region.SA);
    }

    @Cacheable(key = "#region.name()")
    public List<String> getIpRangesByRegion(Region region) {
        CompletableFuture<List<String>> completableFuture = CompletableFuture.supplyAsync(() -> {
            ResponseEntity<IpRangesResponse> responseEntity = restTemplate.getForEntity(awsIpRangesUrl, IpRangesResponse.class);
            IpRangesResponse ipRangesResponse = responseEntity.getBody();

            if (Objects.nonNull(ipRangesResponse) && Objects.nonNull(ipRangesResponse.getPrefixes())) {
                List<String> filteredIpRanges = getFilteredIpRanges(region, ipRangesResponse.getPrefixes());
                return filteredIpRanges;
            } else {
                log.error(NO_DATA_AVAILABLE_FOR_SELECTED_REGION);
                throw new RuntimeException(NO_DATA_AVAILABLE_FOR_SELECTED_REGION);
            }
        }).exceptionally(ex -> {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException(GENERIC_ERROR, ex);
        });

        return completableFuture.join();
    }

    private static List<String> getFilteredIpRanges(Region region, List<IpRange> ipRanges) {
        Set<String> validRegions = region == null ? Collections.emptySet()
                : new HashSet<>(Arrays.asList(mapToApiRegionName(region).split(",")));

        List<String> filteredIpRanges = ipRanges.parallelStream()
                .filter(ipRange -> validRegions.isEmpty() || validRegions.contains(ipRange.getRegion()))
                .map(IpRange::getIpPrefix)
                .collect(Collectors.toList());
        return filteredIpRanges;
    }

    private static String mapToApiRegionName(Region region) {
        return regionToApiRegionMap.getOrDefault(region, null);
    }
}