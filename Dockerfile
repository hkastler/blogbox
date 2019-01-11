# Use local build wildfly 15
# see docker-build.* in wildfly directory
FROM wildfly.15.0.0.final:latest

#Dockerfile in the root to access target dirs
COPY blogbox/target/blogbox.war /opt/jboss/wildfly/standalone/deployments/
RUN rm -f /opt/jboss/wildfly/welcome-content/*.*

ADD standalone-custom.xml /opt/jboss/wildfly/standalone/configuration/
RUN /opt/jboss/wildfly/bin/add-user.sh admin admin0099 --silent

COPY ./blogbox-resources/wildfly/cli/configure-mail.cli $JBOSS_HOME/standalone/tmp/.

RUN /bin/sh -c '$JBOSS_HOME/bin/standalone.sh &' && \
  sleep 11 && \
  cd $JBOSS_HOME/standalone/tmp/ && \  
  $JBOSS_HOME/bin/jboss-cli.sh --user=admin --password=admin0099  --connect --file=configure-mail.cli && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown && \
  rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* && \
  rm configure*.*

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]