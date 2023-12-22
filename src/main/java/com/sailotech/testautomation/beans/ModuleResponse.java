package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModuleResponse {
    private String _id;
    private String projectID;
    private String suiteName;
    private String suiteDescription;
    private Map<String, String> testPlaceholders;
    private List<TestNodeResponse> testNodes;
}
