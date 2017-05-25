/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim;

import com.codahale.metrics.MetricRegistry;

import com.codahale.metrics.Slf4jReporter;
import com.l3harris.swim.jms.JMSConsumer;
import com.l3harris.swim.outputs.Output;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.TimeUnit;

class Consumer {
    private static Logger logger = LoggerFactory.getLogger(Consumer.class);
    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception {

        Config config = ConfigFactory.load();

        String reporterName = config.getString("output");
        Output reporter = null;
        try {
            Class<?> clazz = Class.forName(reporterName);
            Object obj = clazz.getConstructor(Config.class).newInstance(config);
            reporter = (Output) obj;
        } catch (Exception e) {
            logger.error("Invalid outputs class provided {}", reporterName);
            System.exit(-1);
        }

        JMSConsumer jmsConsumer = new JMSConsumer(config, metrics, reporter);
        jmsConsumer.connect(config);

        if (config.getBoolean("metrics")) {
            startReporter();
        }

        while (true) {
            Thread.sleep(20000);
        }
    }

    static void startReporter() {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger("com.l3harris.swim"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }

}
