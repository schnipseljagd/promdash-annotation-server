#!/bin/bash

if [[ ! -d dynamodb ]]; then
    echo "Downloading dynamodb local..."
    mkdir -p dynamodb
    curl -s -L http://dynamodb-local.s3-website-us-west-2.amazonaws.com/dynamodb_local_latest | tar xzv -C dynamodb
fi
cd dynamodb/
echo "Running dynamodb local..."
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar
