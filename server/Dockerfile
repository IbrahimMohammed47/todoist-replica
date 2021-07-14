FROM openjdk:14.0.2-jdk
ADD ./target/todoist-netty.jar .
EXPOSE 8080
RUN jar xf todoist-netty.jar
COPY .env /.env
ENTRYPOINT ["java", "netty.NettyHTTPServer"]
