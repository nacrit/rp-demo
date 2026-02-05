#FROM anapsix/alpine-java
#FROM azul/zulu-openjdk:8
FROM azul/zulu-openjdk:8-jre-alpine
MAINTAINER mars
EXPOSE 8888
ADD target/rp-demo-0.0.1-SNAPSHOT.jar /opt/address-export/rp-demo/app.jar
ENTRYPOINT ["java","-jar","/opt/address-export/rp-demo/app.jar"]
