/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs.database;

import com.l3harris.swim.outputs.Output;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import org.bson.Document;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoOutput extends Output {
    private static Logger logger = LoggerFactory.getLogger(MongoOutput.class);

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection collection;

    public MongoOutput(Config config) {
        super(config);
        // TODO pass in mongo connection from config
        client = new MongoClient();
        database = client.getDatabase("test");
        collection = database.getCollection("messages");

        logger.info("Connection to mongo successful");
    }

    @Override
    public void output(String message) {
        JSONObject xmlJSONObj = XML.toJSONObject(message);
        xmlJSONObj.toString();

        // TODO only add fields required

        collection.insertOne(Document.parse(xmlJSONObj.toString()));
    }
}
