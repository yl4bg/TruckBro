tomcatpath="apache-tomcat-7.0.57"

mvn clean install

#./$tomcatpath/bin/shutdown.sh

rm -rf $tomcatpath/webapps/AServer*

cp target/AServer.war $tomcatpath/webapps/.

#./$tomcatpath/bin/startup.sh
