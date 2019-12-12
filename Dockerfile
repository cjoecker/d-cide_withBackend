FROM openjdk:8
ADD target/d-cide-0.0.1-SNAPSHOT.jar d-cide-0.0.1-SNAPSHOT.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "d-cide-0.0.1-SNAPSHOT.jar"]