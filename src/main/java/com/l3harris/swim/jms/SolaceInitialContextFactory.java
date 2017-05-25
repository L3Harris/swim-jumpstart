/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.jms;

import com.solacesystems.jms.SupportedProperty;
import com.typesafe.config.Config;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

public class SolaceInitialContextFactory {

    public static InitialContext create(Config config) throws Exception {
        String initialContextFactory = config.getString("initialContextFactory");
        String providerUrl = config.getString("providerUrl");
        String username = config.getString("username");
        String password = config.getString("password");
        String vpn = config.getString("vpn");
        String truststorePath = SolaceInitialContextFactory.class.getClassLoader().getResource("cacerts").toExternalForm();

        Hashtable<String, Object> env = new Hashtable <>();
        env.put(InitialContext.INITIAL_CONTEXT_FACTORY, initialContextFactory);
        env.put(InitialContext.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(SupportedProperty.SOLACE_JMS_SSL_VALIDATE_CERTIFICATE, true);
        env.put(SupportedProperty.SOLACE_JMS_VPN, vpn);
        env.put(SupportedProperty.SOLACE_JMS_SSL_TRUST_STORE, truststorePath);
        env.put(SupportedProperty.SOLACE_JMS_JNDI_CONNECT_RETRIES, -1);

        return new InitialContext(env);
    }
}
