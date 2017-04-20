/**
 * Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this file
 * except in compliance with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.amazon.alexa.avs.ui;

import java.awt.FlowLayout;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainWindow extends JFrame {
    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

    private static final int APP_WIDTH = 570;
    private static final int APP_HEIGHT = 300;
    private static final String APP_TITLE = "Alexa Voice Service";
    private static final String VERSION_PROPERTIES_FILE = "/res/version.properties";
    private static final String VERSION_KEY = "version";

    public MainWindow(DeviceNameView deviceNameView, LocaleView localeView,
            BearerTokenView bearerTokenView, UserSpeechVisualizerView userSpeechVisualizerView,
            ListenView listenView, PlaybackControlsView playbackControlsView) {
        super();
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getRootPane().setBorder(new EmptyBorder(10, 10, 10, 10));

        addTopPanel(deviceNameView);
        getContentPane().add(localeView);
        getContentPane().add(bearerTokenView);
        getContentPane().add(userSpeechVisualizerView);
        getContentPane().add(listenView);
        getContentPane().add(playbackControlsView);

        setTitle(getAppTitle());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(APP_WIDTH, APP_HEIGHT);
    }

    private String getAppTitle() {
        String version = getAppVersion();
        String title = APP_TITLE;
        if (version != null) {
            title += " - v" + version;
        }
        return title;
    }

    private String getAppVersion() {
        final Properties properties = new Properties();
        try (final InputStream stream = getClass().getResourceAsStream(VERSION_PROPERTIES_FILE)) {
            properties.load(stream);
            if (properties.containsKey(VERSION_KEY)) {
                return properties.getProperty(VERSION_KEY);
            }
        } catch (IOException e) {
            log.warn("version.properties file not found on classpath");
        }
        return null;
    }

    private void addTopPanel(DeviceNameView deviceNameView) {
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT);
        flowLayout.setHgap(0);
        JPanel topPanel = new JPanel(flowLayout);
        topPanel.add(deviceNameView);
        getContentPane().add(topPanel);
    }
}
