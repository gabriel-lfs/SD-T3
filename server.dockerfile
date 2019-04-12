FROM frekele/ant:1.10.3-jdk8

WORKDIR /SD-T3

ENV CLASSPATH /SD-T3/SD-T3-SERVER.jar

RUN apt-get update && \
    apt-get install -y git

RUN git clone https://github.com/gabriel-lfs/SD-T3.git

RUN cd SD-T3 && ant server

RUN rmiregistry &

CMD [ "java", "-jar", "/SD-T3/SD-T3/SD-T3-SERVER.jar" ]