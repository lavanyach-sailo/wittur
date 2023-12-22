package com.sailotech.testautomation.test.parameterization.jsonformat;

public interface NavigationAction {
	// Format for the jsonString:
	//  {"<NavigationAction>":{"<locatorIdentity>":"<value>/<parameterizedValue>"}}
	// <NavigationAction> --> Could be a generic navigation action like ExpandDropDown,SelectRadioButton etc.
	// <locatorIdentity> -->  Key identifier to find the generic locator code, i.e. Xpath locator string in general CPQ automatin.
	// <value>/<parameterizedValue> --> Actual value / parametrized value in case of multiple iterations over generic logic.
	public void performAction();
}
