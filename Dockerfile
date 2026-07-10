# ---- Build stage: compile the WAR with Maven + JDK 25 ----
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# The app reads DB credentials from src/main/resources/db.properties, which is
# gitignored (contains local creds). For the container we point it at the
# "db" service defined in docker-compose.yml instead.
RUN printf 'db.url=jdbc:mysql://db:3306/quiz_db\ndb.user=quizapp\ndb.password=quizapp\n' \
    > src/main/resources/db.properties

RUN mvn -B -q -DskipTests package

# ---- Runtime stage: run the WAR on Tomcat ----
FROM tomcat:11-jdk25-temurin

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/quiz-website.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
