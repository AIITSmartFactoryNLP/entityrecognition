FROM openjdk:8-jre
ADD target/entityrecognition-0.0.1-SNAPSHOT.jar /home/
CMD java -jar /home/entityrecognition-0.0.1-SNAPSHOT.jar --server.port=8080