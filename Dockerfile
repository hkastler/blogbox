# Use local build wildfly 15
# see docker-build.* in wildfly directory
FROM wildfly.15.0.0.final:latest

#Dockerfile in the root to access target dirs
COPY blogbox/target/blogbox.war /opt/jboss/wildfly/standalone/deployments/
RUN rm -f /opt/jboss/wildfly/welcome-content/*.*

ADD standalone-custom.xml /opt/jboss/wildfly/standalone/configuration/
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin0099 --silent

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]