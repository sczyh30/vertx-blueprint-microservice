FROM java:8-jre

ENV VERTICLE_FILE target/api-gateway-fat.jar

# Set the location of the verticles
ENV VERTICLE_HOME /opt/verticles

EXPOSE 8787

COPY $VERTICLE_FILE $VERTICLE_HOME/
COPY src/docker/config.json $VERTICLE_HOME/


WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["java -jar api-gateway-fat.jar -cluster -conf config.json"]
