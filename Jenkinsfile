pipeline {
	agent any
	stages {
		stage('Build'){
			steps {
				echo "Building the code"
				bat "mvn clean"
			}
		}
		stage('Deploy'){
			steps {
				echo "Deploying the code"
				bat "mvn compile"
			}
		}
		stage('Test'){
			steps {
				echo "Testing the code"
				bat "mvn install test -DlaunchName=$LAUNCH_NAME -DlaunchedBy=$LAUNCHED_BY -DjsonFiles=$JSON_FILES -Dtags=$TAGS -DlaunchID=$LAUNCH_ID"
			}
		}
	}
}