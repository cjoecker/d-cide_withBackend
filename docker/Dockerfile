#
# Copyright (C) MaibornWolff GmbH
#
FROM                openjdk:8-jre

ENV PROJECT         d-cide
ENV LIB_DIR         /opt/lib

COPY .target/${PROJECT}-0.0.1-SNAPSHOT.jar ${LIB_DIR}/${PROJECT}.jar


# add EXPOSE line for traefik
EXPOSE 8080

WORKDIR ${LIB_DIR}

ENTRYPOINT exec java $JAVA_OPTS -jar ${LIB_DIR}/${PROJECT}.jar
