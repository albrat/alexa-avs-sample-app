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
import com.amazon.alexa.avs.ListenHandler;
import com.amazon.alexa.avs.RecordingRMSListener;
import com.amazon.alexa.avs.RequestListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ListenView extends JPanel implements ListenHandler, SpeechStateChangeListener {
    private static final Logger log = LoggerFactory.getLogger(ListenView.class);

    private static final String NOT_LISTENING_LABEL = "Tap to speak to Alexa";
    private static final String LISTENING_LABEL = "Listening";
    private static final String PROCESSING_LABEL = " ";
    private static final String ERROR_DIALOG_TITLE = "Error";
    private static final String BUTTON_NAME = "speechbutton";

    private volatile ButtonState buttonState;
    private ImageIcon notListeningIcon;
    private ImageIcon listeningIcon;
    private JLabel actionButtonLabel;
    private JButton actionButton;
    private AVSController controller;
    private RecordingRMSListener rmsListener;
    private List<SpeechStateChangeListener> listeners;

    public ListenView(RecordingRMSListener rmsListener, AVSController controller) {
        super();
        ClassLoader resLoader = Thread.currentThread().getContextClassLoader();
        notListeningIcon = new ImageIcon(resLoader.getResource("res/avs-mic-icon.png"));
        listeningIcon = new ImageIcon(resLoader.getResource("res/avs-blue-mic-icon.png"));
        this.controller = controller;
        this.rmsListener = rmsListener;
        listeners = new LinkedList<>();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        actionButtonLabel = new JLabel(NOT_LISTENING_LABEL);
        actionButtonLabel.setAlignmentX(CENTER_ALIGNMENT);
        this.add(actionButtonLabel);

        actionButton = new JButton(notListeningIcon);
        actionButton.setAlignmentX(CENTER_ALIGNMENT);
        actionButton.setName(BUTTON_NAME);
        buttonState = ButtonState.START;
        actionButton.setEnabled(true);
        actionButton.addActionListener(e -> listenButtonPressed());

        this.add(actionButton);
    }

    private void listenButtonPressed() {
        controller.onUserActivity();

        if (buttonState == ButtonState.START) { // if in idle mode
            buttonState = ButtonState.STOP;
            controller.startRecording(rmsListener, new SpeechRequestListener());
            onListening();
        } else { // else we must already be in listening
            buttonState = ButtonState.PROCESSING;
            // stop the recording so the request can complete
            controller.stopRecording();
            onProcessing();
        }
    }

    private class SpeechRequestListener extends RequestListener {

        @Override
        public void onRequestFinished() {
            // In case we get a response from the server without
            // terminating the stream ourselves.
            if (buttonState == ButtonState.STOP) {
                listenButtonPressed();
            }
            finishProcessing();
        }

        @Override
        public void onRequestError(Throwable e) {
            log.error("An error occured creating speech request", e);
            JOptionPane.showMessageDialog(ListenView.this, e.getMessage(), ERROR_DIALOG_TITLE,
                    JOptionPane.ERROR_MESSAGE);
            listenButtonPressed();
            finishProcessing();
        }
    }

    /**
     * Handles functional logic to wrap up a speech request
     */
    private void finishProcessing() {
        buttonState = ButtonState.START;
        controller.processingFinished();
        // Now update the UI
        onProcessingFinished();
    }

    public void addSpeechStateChangeListener(SpeechStateChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void onStopCaptureDirective() {
        if (buttonState == ButtonState.STOP) {
            listenButtonPressed();
        }
    }

    @Override
    public void onProcessing() {
        SwingUtilities.invokeLater(() -> {
            actionButton.setEnabled(false);
            actionButtonLabel.setText(PROCESSING_LABEL);
        });
        for (SpeechStateChangeListener listener : listeners) {
            listener.onProcessing();
        }
    }

    @Override
    public void onListening() {
        SwingUtilities.invokeLater(() -> {
            actionButtonLabel.setText(LISTENING_LABEL);
            actionButton.setIcon(listeningIcon);
        });
        for (SpeechStateChangeListener listener : listeners) {
            listener.onListening();
        }
    }

    @Override
    public void onProcessingFinished() {
        SwingUtilities.invokeLater(() -> {
            actionButton.setEnabled(true);
            actionButtonLabel.setText(NOT_LISTENING_LABEL);
            actionButton.setIcon(notListeningIcon);
        });
        for (SpeechStateChangeListener listener : listeners) {
            listener.onProcessingFinished();
        }
    }

    @Override
    public void onExpectSpeechDirective() {
        Thread thread =
                new Thread(() -> {
                    while (!actionButton.isEnabled() || buttonState != ButtonState.START
                            || controller.isSpeaking()) {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                        }
                    }
                    actionButton.doClick();
                });
        thread.start();
    }

    @Override
    public synchronized void onWakeWordDetected() {
        if (buttonState == ButtonState.START) { // if in idle mode
            log.info("Wake Word was detected");
            listenButtonPressed();

        }
    }

    private enum ButtonState {
        START,
        STOP,
        PROCESSING;
    }
}
