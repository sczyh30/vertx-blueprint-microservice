FROM java:8-jre

ENV VERTICLE_FILE target/cache-infrastructure-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /opt/verticles

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY src/docker/config.json $VERTICLE_HOME/

WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar cache-infrastructure-fat.jar -cluster -conf config.json"]
