#!/bin/bash
if docker ps | grep wise_app; then
    echo "wise_app container is running."
    exit 0
else
    echo "wise_app container is NOT running."
    exit 1
fi
