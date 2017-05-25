# Consumer Jumpstart

This is a simple JMS Consumer that allows for testing the connection to SWIM Cloud Distribution Service.  It is not intended to be used for a realworld application.


The Jumpstart allows you to log the message rate metrics and/or messages to the console.

### Configuration Options
The following configuration options will be provided when you create a subscription:

**providerUrl:** url of the message broker including the port (e.g. tcps://hostname:55443)

**queue:** the name of the queue to connect to receive data

**connectionFactory:** the connection factory used for connecting which contains specific configuration parameters defined by an administrator

**username:** the connection username for authentication

**password:** the connection password for authentication

**vpn:** the message vpn to connect to on the broker

**metrics (Optional):** log message rate metrics to the console and defaults to `true`. Set it to `false` for disabling metrics.

**output (Optional):** log messages to a specific output and defaults to `com.l3harris.swim.outputs.NoopOutput`.

* **com.l3harris.swim.outputs.NoopOutput**: does not output message
* **com.l3harris.swim.outputs.StdoutOutput**: outputs the messages to standard out
* **com.l3harris.swim.outputs.FileOutput**: outputs the messages to a single rotating file log located in `./log/messages.log`
* **com.l3harris.swim.outputs.MessageFileOutput**: outputs each message to a separate file located in `./log/`
* **com.l3harris.swim.outputs.database.MongoOutput**: outputs each message to a document in a mongo collection in **json** format
* **com.l3harris.swim.outputs.database.PostgresOutput**: outputs each message to a row in a postgres table in **json** format

Custom Outputs
* **com.l3harris.swim.outputs.custom.TfmsFlowPostgresOutput**: outputs each TFMS restriction and airport configuration message to postgres
* **com.l3harris.swim.outputs.json.TDESPostgresOutput**: outputs each TDES message to postgres


**json (Optional):** attempt to convert the xml messages to json and defaults to `false`.  Set it to `true` for json output

**postgres:** configuration options for postgres

* **postgres.uri**: uri of the postgres database with the database name (e.g. "jdbc:postgresql://localhost:5432/test")
* **postgres.user**: the username for login to postgres
* **postgres.password**: the password for login to postgres


### Running with cli options
```
./bin/run -DproviderUrl= -Dqueue= -DconnectionFactory= -Dusername= -Dpassword= -Dvpn=
```

### Running with config file
```
./bin/run -Dconfig.file=path/to/config-file
```

Config File Example (application.conf)
```
providerUrl= 
queue= 
connectionFactory= 
username= 
password= 
vpn=
initialContextFactory=
metrics=false
output=com.l3harris.swim.outputs.FileOutput
json=true


postgres.uri="jdbc:postgresql://localhost:5432/test"
postgres.user=postgres
postgres.password=postgres
```


### Using Database Outputs

Mongo (com.l3harris.swim.outputs.database.MongoOutput)
* Install mongodb locally: https://docs.mongodb.com/manual/installation/
* Setup using docker: `docker run --name mongo -p 27017:27017 -d mongo`
* Use a remote instance: (TODO: requires code changes to take in mongo connection string)

Postgres (com.l3harris.swim.outputs.database.PostgresOutput)
* Install postgres locally: https://www.postgresql.org/download/
* Setup using docker: `docker run --name postgres -e POSTGRES_DB=test -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:alpine`
* Use a remote instance: (TODO: requires code changes to take in postgres config options)

### Create a new Output
Extend the `Output` class and implement a `output(String messsage)` function.  When launching the jumpstart kit pass in new class `-Doutput=com.l3harris.swim.outputs.<NewClass>`
