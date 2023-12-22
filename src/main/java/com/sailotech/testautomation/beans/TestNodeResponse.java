package com.sailotech.testautomation.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonPropertyOrder({ "testNode", "_id" })
public class TestNodeResponse {
	private String _id;
	private List<TestNodeSingularResponse> testNode;
}
