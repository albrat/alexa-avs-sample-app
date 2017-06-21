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
package com.amazon.alexa.avs;

import com.amazon.alexa.avs.auth.AuthSetup;
import com.amazon.alexa.avs.auth.companionservice.RegCodeDisplayHandler;
import com.amazon.alexa.avs.config.DeviceConfig;
import com.amazon.alexa.avs.config.DeviceConfigUtils;
import com.amazon.alexa.avs.http.AVSClientFactory;
import com.amazon.alexa.avs.ui.BearerTokenView;
import com.amazon.alexa.avs.ui.CardView;
import com.amazon.alexa.avs.ui.DeviceNameView;
import com.amazon.alexa.avs.ui.DialogFactory;
import com.amazon.alexa.avs.ui.ListenView;
import com.amazon.alexa.avs.ui.LocaleView;
import com.amazon.alexa.avs.ui.MainWindow;
import com.amazon.alexa.avs.ui.PlaybackControlsView;
import com.amazon.alexa.avs.ui.UserSpeechVisualizerView;
import com.amazon.alexa.avs.wakeword.WakeWordIPCFactory;

import javax.swing.SwingUtilities;

public class App {

    private AVSController controller;
    private AuthSetup authSetup;
    private BearerTokenView bearerTokenView;
    private UserSpeechVisualizerView visualizerView;
    private ListenView listenView;
    private PlaybackControlsView playbackControlsView;
    private CardView cardView;
    private MainWindow mainWindow;

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            new App(args[0]);
        } else {
            new App();
        }
    }

    public App() throws Exception {
        this(DeviceConfigUtils.readConfigFile());
    }

    public App(String configName) throws Exception {
        this(DeviceConfigUtils.readConfigFile(configName));
    }

    public App(DeviceConfig config) throws Exception {

        authSetup = new AuthSetup(config);
        controller =
                new AVSController(new AVSAudioPlayerFactory(), new AlertManagerFactory(),
                        getAVSClientFactory(config), DialogRequestIdAuthority.getInstance(),
                        new WakeWordIPCFactory(), config);
        SwingUtilities.invokeAndWait(() -> createViews(config));
        addListeners();
        startAuthentication(config);
        start();
    }

    protected AVSClientFactory getAVSClientFactory(DeviceConfig config) {
        return new AVSClientFactory(config);
    }

    private void startAuthentication(DeviceConfig config) {
        RegCodeDisplayHandler regCodeDisplayHandler =
                new RegCodeDisplayHandler(new DialogFactory(mainWindow), config);
        authSetup.startProvisioningThread(regCodeDisplayHandler);
    }

    private void createViews(DeviceConfig config) {
        bearerTokenView = new BearerTokenView(authSetup, controller);
        DeviceNameView deviceNameView = new DeviceNameView(config.getProductId(), config.getDsn());
        LocaleView localeView = new LocaleView(config, controller);
        visualizerView = new UserSpeechVisualizerView();
        listenView = new ListenView(visualizerView, controller);
        playbackControlsView = new PlaybackControlsView(visualizerView, controller);
        cardView = new CardView();

        mainWindow =
                new MainWindow(deviceNameView, localeView, bearerTokenView, visualizerView, listenView,
                        playbackControlsView, cardView);
    }

    private void addListeners() {
        listenView.addSpeechStateChangeListener(visualizerView);
        listenView.addSpeechStateChangeListener(playbackControlsView);
        listenView.addSpeechStateChangeListener(cardView);
        authSetup.addAccessTokenListener(bearerTokenView);
        authSetup.addAccessTokenListener(controller);
    }

    private void start() {
        controller.init(listenView, cardView);
        controller.startHandlingDirectives();
        SwingUtilities.invokeLater(() -> mainWindow.setVisible(true));
    }

    public AVSController getController() {
        return controller;
    }
}
