/*
 * Copyright (c) 2020 L3Harris Technologies
 */
package com.l3harris.swim.outputs;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MessageFileOutput extends Output {
    private static Logger logger = LoggerFactory.getLogger(MessageFileOutput.class);
    private File outputDirectory;

    public MessageFileOutput(Config config) {
        super(config);
        // TODO change the diretory from config
        outputDirectory = new File("./log/");
    }

    @Override
    public void output(String message) {
        File file = new File(outputDirectory + File.separator + System.currentTimeMillis());
        try (FileOutputStream stream = new FileOutputStream(file)) {
            stream.write(this.convert(message).getBytes());
        } catch (FileNotFoundException e) {
            logger.error("File not found", e);
        } catch (IOException e) {
            logger.error("Failed to close the file", e);
        }
    }
}
