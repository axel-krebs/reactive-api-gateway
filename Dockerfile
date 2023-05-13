FROM axel9691/graalvm-java17:22.3.2-native

EXPOSE 81

COPY ./src/main/kotlin/ src/main/kotlin
COPY ./src/main/resources/ src/main/resources
ADD build.gradle.kts .
ADD gradle.properties .
ADD settings.gradle.kts .
RUN gradle clean compileKotlin nativeCompile

CMD ["/bin/bash", "-c", "tail -f /dev/null"]
