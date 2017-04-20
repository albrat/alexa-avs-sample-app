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
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.amazon.alexa.avs.AVSController;
import com.amazon.alexa.avs.config.DeviceConfig;
import com.amazon.alexa.avs.config.DeviceConfigUtils;

public class LocaleView extends JPanel {

    private static final String LOCALE_LABEL = "Locale:";
    private static final String LOCALE_NAME = "Locale";

    private DeviceConfig deviceConfig;

    public LocaleView(DeviceConfig config, AVSController controller) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.deviceConfig = config;
        JLabel localeLabel = new JLabel(LOCALE_LABEL);
        this.add(localeLabel);
        localeLabel.setName(LOCALE_NAME);
        JComboBox<Object> localeSelector =
                new JComboBox<>(deviceConfig.getSupportedLocalesLanguageTag().toArray());
        localeSelector.setSelectedItem(deviceConfig.getLocale().toLanguageTag());
        localeSelector.addActionListener(e -> {
            Locale locale = Locale.forLanguageTag(localeSelector.getSelectedItem().toString());
            deviceConfig.setLocale(locale);
            DeviceConfigUtils.updateConfigFile(deviceConfig);
            controller.setLocale(locale);
        });
        this.add(localeSelector);
    }
}
