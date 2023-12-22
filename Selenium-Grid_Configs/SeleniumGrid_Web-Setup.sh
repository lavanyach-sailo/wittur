#!/bin/bash

# start selenium grid server

seleniumGridPath=/Users/rakeshkumargannu/Downloads/selenium-server-standalone-3.141.59.jar

seleniumGrid_Running(){
 return `ps -A|grep -i selenium-server-standalone|wc -l`
}

nohup java -jar $seleniumGridPath -role hub -hubConfig hubconfig.json > $JENKINS_HOME/Mavenjava/test-output/selenium-grid.log 2>&1 &

sleep 10s

 if [ $(seleniumGrid_Running) >= 1 ]  || [ `grep -i "Selenium Grid hub is up and running" $JENKINS_HOME/Mavenjava/test-output/selenium-grid.log|wc -l` == 1 ]; then
   echo "Selenium grid initialized"
 else
   echo "Selenium grid not initialized: `ps -A|grep -i Selenium`"	
 fi   



#### Sample run #####

java -jar selenium-server-standalone-3.141.59.jar -role hub -hubConfig /Users/rakeshkumargannu/git/Pattesa-TestAutomation/PattesastAutomationWeb/Selenium-Grid_Appium_Configs/hubconfig.json

java -Dwebdriver.chrome.driver="chromedriver-5" -Dwebdriver.gecko.driver="gekodriver" -jar selenium-server-standalone-3.141.59.ja-role node -nodeConfig  /Users/rakeshkumargannu/git/Pattesa-TestAutomation/PattesaTestAutomationWeb/Selenium-Grid_Appium_Configs/Web-Node1.json

mvn clean test -PRegressionTest -e -Dtestng.show.stack.frames=true -X
