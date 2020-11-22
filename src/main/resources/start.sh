#!/bin/bash

set -ex

docker-compose down

export AWS_PAGER=""
export AWS_MAX_ATTEMPTS=10
export AWS_RETRY_MODE=standard
export DEFAULT_REGION=eu-central-1
export SERVICES=dynamodb,ses
docker-compose up -d
echo "local setup started successfully"

echo "Creating DynamoDb-Table User"
aws dynamodb create-table \
    --endpoint-url http://localhost:4569 \
    --table-name User \
    --attribute-definitions \
        AttributeName=userId,AttributeType=S \
    --key-schema AttributeName=userId,KeyType=HASH \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1

aws dynamodb update-table \
    --endpoint-url http://localhost:4569 \
    --table-name User \
    --attribute-definitions AttributeName=email,AttributeType=S \
    --global-secondary-index-updates \
    "[{\"Create\":{\"IndexName\": \"email-index\",\"KeySchema\":[{\"AttributeName\":\"email\",\"KeyType\":\"HASH\"}], \"ProvisionedThroughput\": {\"ReadCapacityUnits\": 10, \"WriteCapacityUnits\": 5},\"Projection\":{\"ProjectionType\":\"ALL\"}}}]"
echo "Successfully created DynamoDb-Table User."
