# influxdb-unsupported-mwe

This is a minimal (non-)working example for issue #256 of the InfluxDB Java Client:

https://github.com/influxdata/influxdb-client-java/issues/256

## Usage

You need to have Docker and Docker-Compose installed.
Then perform the following steps:

```
git clone https://github.com/meyfa/influxdb-unsupported-mwe.git
cd influxdb-unsupported-mwe
docker-compose build
docker-compose up
```

The final step will start the test app attached to the console.
There will be output documenting the process.

First, the app will wait until InfluxDB has come online.
Then, it will perform a query, which will fail (topic of the issue).

Note that there is no need to provide your own InfluxDB instance;
there is one included in docker-compose.yml specifically for this demonstration.

## Fixing the problem

The problem occurs because InfluxDB depends on Gson, which uses `sun.misc.Unsafe`.
To make the error go away, open `Dockerfile` and include `jdk.unsupported`
in the list of JLink modules:

```Dockerfile
RUN jlink --compress=2 --add-modules java.sql,jdk.unsupported --output jlink
```

Run `docker-compose build` and `docker-compose up` again, and the error will disappear.
