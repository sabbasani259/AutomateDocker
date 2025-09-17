FROM payara/server-full:5.2022.5

ARG EAR_FILE
COPY ${EAR_FILE} /opt/payara/deployments/

EXPOSE 8080
