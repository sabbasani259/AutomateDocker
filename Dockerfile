FROM openjdk:8-jre
ARG EAR_FILE
COPY ${EAR_FILE} /app/wise.ear
# Set the working directory
WORKDIR /app
# Add ENTRYPOINT or CMD as needed
