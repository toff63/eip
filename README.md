Entreprise Integration Patterns with JMS 2 and WildFly
===

Code implementing or using Entreprise Integration Patterns


# Usage

Download wildfly 8.1 Final and start it:

    $JBOSS_HOME/bin/standalone -c standalone-full.xml
  
Then at the root of this maven project run:

    mvn clean package wildfly:deploy
  
Go to [http://localhost:8080/eip][1]

[1]: http://localhost:8080/eip
