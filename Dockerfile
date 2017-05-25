FROM openjdk:alpine

# Install maven
RUN apk update \
 && apk upgrade \
 && apk add maven \
 && rm -f /var/cache/apk/*

WORKDIR /code

# Prepare by downloading dependencies
ADD pom.xml /code/pom.xml
RUN ["mvn", "dependency:resolve"]

# Adding source, compile and package into a fat jar
ADD src /code/src
RUN ["mvn", "package"]

CMD java -DproviderUrl=$PROVIDER_URL -DconnectionFactory=$CONNECTION_FACTORY -DinitialContextFactory=com.solacesystems.jndi.SolJNDIInitialContextFactory -Dusername=$USERNAME -Dpassword=$PASSWORD -Dqueue=$QUEUE -Dvpn=$VPN -jar target/jumpstart-jar-with-dependencies.jar
