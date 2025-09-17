# Use Payara Full as base image
FROM payara/server-full:5.2022.5

# Copy your EAR file into the deployments directory
COPY build/ear/wise.ear /opt/payara/deployments/

# Expose the default Payara port
EXPOSE 8080

# Optionally, set environment variables or commands as needed