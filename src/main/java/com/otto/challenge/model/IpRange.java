package com.otto.challenge.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IpRange {

    @JsonProperty("ip_prefix")
    private String ipPrefix;
    private String region;
    private String service;
    @JsonProperty("network_border_group")
    private String networkBorderGroup;
}