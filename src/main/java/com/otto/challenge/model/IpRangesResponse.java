package com.otto.challenge.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IpRangesResponse {

    private String syncToken;
    private String createDate;
    private List<IpRange> prefixes;
}
