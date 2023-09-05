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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.otto.challenge.constants.GlobalApplicationConstants.GENERIC_ERROR;
import static com.otto.challenge.constants.GlobalApplicationConstants.NO_DATA_AVAILABLE_FOR_SELECTED_REGION;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsIpRangeService {

    private final RestTemplate restTemplate;
    @Value("${aws.ip.ranges.url: test}")
    private final String awsIpRangesUrl;

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
        List<String> filteredIpRanges = ipRanges.parallelStream()
                .filter(ipRange -> {
                    if (region == null || region == Region.ALL) {
                        return true; // Include all regions
                    }
                    return region.name().equalsIgnoreCase(ipRange.getRegion());
                })
                .map(IpRange::getIpPrefix)
                .collect(Collectors.toList());
        return filteredIpRanges;
    }
}