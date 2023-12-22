package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponse {
	private String company;
	private String rpAccessToken;
	private String rpUsername;
	private String createdAt;
	private String updatedAt;
	private String createdBy;
	private String updatedBy;
}
