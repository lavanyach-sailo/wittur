package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Release {
    private String releaseID;
    private String releaseName;
    private String releaseDesc;
    private String company;
    private String createdBy;
    private List<Project> projects;
}
