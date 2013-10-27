Usage
=====

* Add your war file artifact to the Maven <code>pom.xml</code>
* Build it <code>mvn clean install</code>
* Run it <code>java -jar yourWebApp-version.jar start</code>

Configuration
=============

Create a secret (i.e. one per environment):
<pre>
:➜ md5sum yourWarFile.war
eb27fb2e61ed603363461b3b4e37e0a0  yourWarFile.war
</pre>

Create a configuration file:
<pre>
:➜ cat > /etc/bekkopen/appname.properties
jetty.contextPath=/appname
jetty.port=7000
jetty.workDir=/var/apps/appname/
jetty.secret=eb27fb2e61ed603363461b3b4e37e0a0
[ctrl+d]
</pre>

Start it with a configuration file (default: CWD/jetty.properties):
<pre>
:➜ java -Dconfig=/etc/bekkopen/appname.properties -jar appname-1.0.0rc0.jar
</pre>

Override individual properties:
<pre>
:➜ java -Djetty.port=7001 -jar appname-1.0.0rc0.jar
</pre>

(I didn't bother implementing combinations of system properties and resource properties - we i.e. use Constretto in our own launcher).
