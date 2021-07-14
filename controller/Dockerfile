FROM openjdk:14.0.2-jdk
ADD ./target/todoist-controller.jar .
EXPOSE 8080
RUN jar xf todoist-controller.jar
COPY .env /.env
ENTRYPOINT ["java", "main.Client"]