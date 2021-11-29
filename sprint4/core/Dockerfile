FROM gradle:7.3-jdk11

#RUN addgroup --system gradle && adduser --system gradle --ingroup gradle --shell /bin/bash
#USER gradle

COPY sprint4/core /app/core

COPY unibolibs /unibolibs

WORKDIR /app/core

#RUN ["ls"]
#RUN ["ls", "/unibolibs"]
#CMD ["ls", "/unibolibs"]

CMD "gradle  -b build_ctxservice.gradle --console=plain --warning-mode none run"

#in main folder:
#docker build . -f sprint4/web/Dockerfile -t parkmanagerservice:1.0
#docker run -p 8081:8081 --name parkmanagers --network host --rm parkmanagerservice:0.1
#docker rm parkmanagers

#To remove old parking slot status
#docker exec -it parkmanagers rm /app/core/status.json
#docker exec parkmanagers rm -rf /app/core/status.json