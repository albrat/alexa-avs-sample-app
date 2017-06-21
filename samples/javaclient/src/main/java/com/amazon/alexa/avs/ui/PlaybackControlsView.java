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

import com.amazon.alexa.avs.AVSController;
import com.amazon.alexa.avs.PlaybackAction;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class PlaybackControlsView extends JPanel implements SpeechStateChangeListener {

    private static final String PREVIOUS_LABEL = "\u21E4";
    private static final String NEXT_LABEL = "\u21E5";
    private static final String PAUSE_LABEL = "\u275A\u275A";
    private static final String PLAY_LABEL = "\u25B6";

    private final AVSController controller;
    private final UserSpeechVisualizerView visualizer;

    private JButton playPauseButton;

    public PlaybackControlsView(UserSpeechVisualizerView visualizerView, AVSController controller) {
        super();
        this.controller = controller;
        this.visualizer = visualizerView;
        this.setLayout(new GridLayout(1, 5));

        createMusicButton(PREVIOUS_LABEL, PlaybackAction.PREVIOUS);
        createPlayPauseButton();
        createMusicButton(NEXT_LABEL, PlaybackAction.NEXT);
    }

    private void createMusicButton(String label, final PlaybackAction action) {
        JButton button = new JButton(label);
        button.setEnabled(true);
        button.addActionListener(e -> {
            controller.onUserActivity();
            musicButtonPressedEventHandler(action);
        });
        this.add(button);
    }

    private void createPlayPauseButton() {
        playPauseButton = new JButton(PLAY_LABEL + "/" + PAUSE_LABEL);
        playPauseButton.setEnabled(true);
        playPauseButton.addActionListener(e -> {
            controller.onUserActivity();
            if (controller.isPlaying()) {
                musicButtonPressedEventHandler(PlaybackAction.PAUSE);
            } else {
                musicButtonPressedEventHandler(PlaybackAction.PLAY);
            }
        });
        this.add(playPauseButton);
    }

    private void musicButtonPressedEventHandler(final PlaybackAction action) {
        SwingWorker<Void, Void> alexaCall = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() throws Exception {
                visualizer.onProcessing();
                controller.handlePlaybackAction(action);
                return null;
            }

            @Override
            public void done() {
                visualizer.onProcessingFinished();
            }
        };
        alexaCall.execute();
    }

    private void setPlaybackControlEnabled(boolean enable) {
        SwingUtilities.invokeLater(() -> setComponentsOfContainerEnabled(this, enable));
    }

    /**
     * Recursively Enable/Disable components in a container
     *
     * @param container
     *            Object of type Container (like JPanel).
     * @param enable
     *            Set true to enable all components in the container. Set to false to disable all.
     */
    private void setComponentsOfContainerEnabled(Container container, boolean enable) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                setComponentsOfContainerEnabled((Container) component, enable);
            }
            SwingUtilities.invokeLater(() -> component.setEnabled(enable));
        }
    }

    @Override
    public void onProcessing() {
        // No-op
    }

    @Override
    public void onListening() {
        setPlaybackControlEnabled(false);
    }

    @Override
    public void onProcessingFinished() {
        setPlaybackControlEnabled(true);
    }
}
