/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.jms;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.l3harris.swim.outputs.Output;
import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import javax.naming.Context;

public class JMSConsumer implements MessageListener, ExceptionListener {
    private static Logger logger = LoggerFactory.getLogger(JMSConsumer.class);

    // Connection configuration
    private final String queue;
    private final String providerUrl;
    private final String connectionFactory;

    // Connection members
    private Connection connection;
    private Session session;
    private Context context;

    private Meter messageRate;

    // Output to output the messages
    private Output reporter;

    /**
     * Constructor sets configuration items
     *
     * @param config the consumer config properties
     */
    public JMSConsumer(Config config, MetricRegistry metrics, Output reporter) {
        // Set configuration items
        providerUrl = config.getString("providerUrl");
        queue = config.getString("queue");
        connectionFactory = config.getString("connectionFactory");

        messageRate = metrics.meter("messages");

        this.reporter = reporter;
    }

    /**
     * Opens a JMS connection and creates a message consumer for receiving messages
     */
    public void connect(Config config) throws Exception {
        try {
            context = SolaceInitialContextFactory.create(config);
            QueueConnectionFactory conFactory = (QueueConnectionFactory) context.lookup(connectionFactory);

            connection = conFactory.createQueueConnection();
            connection.setExceptionListener(this);

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Queue theQueue = (Queue) context.lookup(queue);
            MessageConsumer consumer = session.createConsumer(theQueue);
            consumer.setMessageListener(this);

            // Start the JMS connection.
            start();
        } catch (Exception e) {
            logger.error("Failed to create the JMS connection: [ " + providerUrl + " -- " + queue + " ] ", e);
            throw e;
        }
    }

    /**
     * Start receiving messages
     */
    private synchronized void start() throws JMSException {
        try {
            connection.start();
        } catch (JMSException e) {
            logger.error("Failed to start the JMS connection: [ " + providerUrl + " -- " + queue + " ] ", e);
            throw e;
        }
    }

    /**
     * Sends received JMS message to the broker. Includes original JMS properties in addition to the message body.
     *
     * @param message The incoming message.
     */
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof BytesMessage) {
                BytesMessage byteMessage = (BytesMessage) message;
                byte[] content = new byte[(int) byteMessage.getBodyLength()];
                byteMessage.readBytes(content);
                logMessage(new String(content));
            } else if (message instanceof TextMessage) {
                logMessage(((TextMessage) message).getText());
            } else {
                logger.error("Received incorrect message type");
            }

            messageRate.mark();

        } catch (Exception e) {
            logger.error("Error receiving message", e);
        }
    }

    @Override
    public void onException(JMSException e) {
        logger.error("Consumer Exception", e);
        System.exit(-1);
    }

    private void logMessage(String message) {
        reporter.output(message);
    }
}
