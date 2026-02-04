FROM anapsix/alpine-java
MAINTAINER mars
EXPOSE 8888
ADD target/rp-demo-0.0.1-SNAPSHOT.jar /opt/rp-demo/app.jar
ENTRYPOINT ["java","-jar","/opt/rp-demo/app.jar"]
