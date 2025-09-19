#!/bin/bash
echo "Preparing deployment directory..."
mkdir -p /data5/LLApplicationDocker_Images/
chmod +x /data5/LLApplicationDocker_Images || true

echo "Retrieving LLApplicationToken from Secrets Manager..."
export GITHUB_TOKEN=$(aws secretsmanager get-secret-value --secret-id LLApplicationToken --query SecretString --output text | jq -r .GITHUB_TOKEN)

echo "Logging in to GitHub Container Registry..."
echo $GITHUB_TOKEN | docker login ghcr.io -u sabbasani259 --password-stdin

if [ $? -eq 0 ]; then
    echo "Successfully logged into GHCR."
else
    echo "Failed to log into GHCR. Exiting deployment."
    exit 1
fi
