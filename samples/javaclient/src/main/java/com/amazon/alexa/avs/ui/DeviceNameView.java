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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DeviceNameView extends JPanel {

    private static final String DEVICE_LABEL = "Device: ";
    private static final String DSN_LABEL = "DSN: ";
    private static final String DSN_LABEL_NAME = "dsnlabel";
    private static final String PRODUCTID_LABEL_NAME = "productIDlabel";
    private static final String DEVICE_NAME = "devicename";

    public DeviceNameView(String productId, String dsn) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel productIdLabel = new JLabel(productId);
        JLabel dsnLabel = new JLabel(dsn);
        productIdLabel.setFont(productIdLabel.getFont().deriveFont(Font.PLAIN));
        dsnLabel.setFont(dsnLabel.getFont().deriveFont(Font.PLAIN));
        dsnLabel.setName(DSN_LABEL_NAME);
        productIdLabel.setName(PRODUCTID_LABEL_NAME);
        this.add(new JLabel(DEVICE_LABEL));
        this.add(productIdLabel);
        this.add(Box.createRigidArea(new Dimension(15, 0)));
        this.add(new JLabel(DSN_LABEL));
        this.add(dsnLabel);
        this.add(Box.createRigidArea(new Dimension(15, 0)));
        this.setName(DEVICE_NAME);
    }
}
