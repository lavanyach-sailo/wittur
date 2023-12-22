package com.sailotech.testautomation.beans;

import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobUpdateRequest {
    private String reportPortalUrl;
    private String ltVideoUrl;
    private List<String> screenshot;
    private String log;
}
