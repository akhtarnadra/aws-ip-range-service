package com.otto.challenge.controller;

import com.otto.challenge.constants.GlobalApplicationConstants;
import com.otto.challenge.model.Region;
import com.otto.challenge.service.AwsIpRangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(GlobalApplicationConstants.CONTEXT_PATH_SUFFIX)
@RequiredArgsConstructor
@Slf4j
public class AwsIpRangeController {

    private final AwsIpRangeService awsIpRangeService;

    @GetMapping(value = "/aws-region-ip-ranges", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getIpRangesByRegion(@RequestParam(required = false) Region region) {
        List<String> ipRanges = awsIpRangeService.getIpRangesByRegion(region);
        return ResponseEntity.ok(String.join("\n", ipRanges));
    }
}
