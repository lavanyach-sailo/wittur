package com.sailotech.testautomation.test.parameterization.common.loopactions;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.sailotech.testautomation.accelarators.TestBase;
import com.sailotech.testautomation.exceptions.jsonparsing.JsonAutomationFormatIssue;
import com.sailotech.testautomation.util.TestEnsureRequest;

import edu.emory.mathcs.backport.java.util.Arrays;

public class LoopingActions {

	public static TestEnsureRequest delegateLoopAction(String loopAction, JsonNode loopActionValueNode, String file, String testCaseStepId) throws Throwable {
		TestEnsureRequest testEnsureRequest = null;
		try {

			// TODO: Generic method to get the class name where to fetch the methods.
			
			String className = "com.sailotech.testautomation.test.parameterization.common.loopactions.LoopActionImpls";
			Class navActionImplsClass = Class.forName(className);
			Map<String, Method> navActionMethodsMap = (Map<String, Method>) Arrays
					.asList(navActionImplsClass.getDeclaredMethods()).stream()
					.collect(Collectors.toMap(Method::getName, method -> method));
			if (navActionMethodsMap.get(loopAction)==null) {
				throw new JsonAutomationFormatIssue("No implementation found for the loopaction: "+loopAction);
			}
			// Generic construct for navAction method invocation	
			try {
				TestBase.noOfRetries = 0;
				testEnsureRequest = (TestEnsureRequest) navActionMethodsMap.get(loopAction).invoke(null, loopActionValueNode, file, testCaseStepId);				
			} catch(Exception e) {
			}
			
		} catch (ClassNotFoundException e) {
			throw new JsonAutomationFormatIssue("No implementation found for the loopaction:" +loopAction, e);
		}
		return testEnsureRequest;
	}


	// TODO: Move this to modularize.
	/* public static Collection<Class> getClasses(final String packageName) throws Exception {
		final StandardJavaFileManager fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null,
				null, null);
		return StreamSupport
				.stream(fileManager.list(StandardLocation.CLASS_PATH, packageName,
						Collections.singleton(JavaFileObject.Kind.CLASS), false).spliterator(), false)
				.map(javaFileObject -> {
					try {
						final String[] split = javaFileObject.getName().replace(".class", "").replace(")", "")
								.split(Pattern.quote(File.separator));

						final String fullClassName = packageName + "." + split[split.length - 1];
						return Class.forName(fullClassName);
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}

				}).collect(Collectors.toCollection(ArrayList::new));
	} */
}
