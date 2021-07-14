FROM openjdk:14.0.2-jdk
ADD ./target/todoist-tasks-app.jar .
EXPOSE 8080
RUN jar xf todoist-tasks-app.jar
COPY .env /.env
ENTRYPOINT ["java", "core.Main"]
