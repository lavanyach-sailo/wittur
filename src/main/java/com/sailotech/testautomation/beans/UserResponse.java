package com.sailotech.testautomation.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private CompanyResponse company;
	private String status;
	private String role;
	private String createdAt;
	private String updatedAt;
}
