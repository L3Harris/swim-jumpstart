/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs;

import com.typesafe.config.Config;

public class NoopOutput extends Output {
    public NoopOutput(Config config) {
        super(config);
    }

    @Override
    public void output(String message) {
        // explicitly do nothing
    }
}
