# promdash-annotation-server

An annotation backend for [promdash](http://prometheus.io/docs/visualization/promdash/).
Uses [DynamoDB](https://aws.amazon.com/dynamodb/) as datastore.

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Tests

    lein test

## Running

To start a web server for the application, run:

    lein ring server

    curl -d'{"created_at":1423491000,"message":"juuhuu"}' -H'Content-type: application/json' http://localhost:3000/annotations/tags/deployment

    curl 'http://localhost:3000/annotations?range=3600&tags%5B%5D=deployment&tags%5B%5D=prometheus&until=1423491311.424'
