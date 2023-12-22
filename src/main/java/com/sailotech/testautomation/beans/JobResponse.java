package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class JobResponse {
    private String _id;
    private String jenkinsJobID;
    private String jenkinsPath;
    private String releaseID;
    private String createdBy;
    private List<ModuleResponse> modules;
    private String __v;
}
