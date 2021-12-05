FROM gradle:7.3-jdk11

#RUN addgroup --system gradle && adduser --system gradle --ingroup gradle --shell /bin/bash
#USER gradle

COPY sprint4/web /app/web

COPY unibolibs /unibolibs

WORKDIR /app/web

#RUN ["ls"]
#RUN ["ls", "/unibolibs"]
#CMD ["ls", "/unibolibs"]

CMD ["gradle", "bootRun"]

#in main folder:
#docker build . -f sprint4/web/Dockerfile -t parkmanagerservice-webapp:1.0
#docker run -p 8081:8081 --name parkmanagersweb --network host --rm parkmanagerservice-webapp:0.1
#docker rm parkmanagersweb

#To remove old parking slot status
#docker exec -it parkmanagers rm /app/core/status.json
#docker exec parkmanagers rm -rf /app/core/status.json