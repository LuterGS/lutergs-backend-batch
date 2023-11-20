FROM ghcr.io/graalvm/graalvm-community:21-ol9 AS builder

ARG ACTIVE_PROFILES
ENV JAVA_VERSION=21
RUN mkdir /lutergs-backend-batch
COPY . /lutergs-backend-batch
RUN echo "spring.profiles.active=${ACTIVE_PROFILES}" > /lutergs-backend-batch/src/main/resources/application.properties
WORKDIR /lutergs-backend-batch

RUN ./gradlew nativeCompile


FROM ubuntu:latest

WORKDIR /
COPY --from=builder /lutergs-backend-batch/build/native/nativeCompile/lutergs-backend-batch application
CMD ["/application", "-Duser.timezone=Asia/Seoul"]

