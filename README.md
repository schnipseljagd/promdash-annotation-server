# promdash-annotation-server
[![Build Status](https://travis-ci.org/schnipseljagd/promdash-annotation-server.svg)](https://travis-ci.org/schnipseljagd/promdash-annotation-server)

An annotation backend for [promdash](http://prometheus.io/docs/visualization/promdash/).
Uses [DynamoDB](https://aws.amazon.com/dynamodb/) as datastore.

Get the latest [docker build](https://registry.hub.docker.com/u/schnipseljagd/promdash-annotation-server/).

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Run it locally

Setup environment:

    cp profiles.clj.example profiles.clj
    ./run_dynamodb_local.sh ; have dynamodb local running

Run the tests:

    lein test

To start a web server for the application, run:

    lein ring server

    # let the annotation server generate a new annotation
    curl -d'{"message":"juuhuu"}' -H'Content-type: application/json' http://localhost:3000/annotations/tags/deployment
    # or with your own timestamp in milliseconds
    curl -d'{"created_at":1423487712000,"message":"juuhuu"}' -H'Content-type: application/json' http://localhost:3000/annotations/tags/deployment

    curl 'http://localhost:3000/annotations?range=3600&tags%5B%5D=deployment&tags%5B%5D=prometheus&until=1423491311.424'

## Usage

Set the following environment variables:

| Key                             | Purpose                |
| ------------------------------- | ---------------------  |
| AWS_ACCESS_KEY                  | An AWS access key      |
| AWS_SECRET_KEY                  | An AWS secret key      |
| DYNAMODB_READ_CAPACITY_UNITS    | Default 1              |
| DYNAMODB_WRITE_CAPACITY_UNITS   | Default 1              |
| AWS_DYNAMODB_ENDPOINT           | Default local dynamodb |

## Run it in docker

    docker run -d -p 8080:8080 -e AWS_ACCESS_KEY="XXX" -e AWS_SECRET_KEY="YYY" -e AWS_DYNAMODB_ENDPOINT="http://dynamodb.eu-west-1.amazonaws.com/" -e DYNAMODB_READ_CAPACITY_UNITS=10 -e DYNAMODB_WRITE_CAPACITY_UNITS=1 <some-id>
