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

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.amazon.alexa.avs.AVSController;
import com.amazon.alexa.avs.auth.AccessTokenListener;
import com.amazon.alexa.avs.auth.AuthSetup;

public class BearerTokenView extends JPanel implements AccessTokenListener {

    private static final String BEARER_TOKEN_LABEL = "Bearer token:";
    private static final int TEXT_FIELD_COLUMNS = 50;

    private JTextField tokenTextField;

    public BearerTokenView(AuthSetup authSetup, AVSController controller) {
        super(new GridLayout(0, 1));
        this.add(new JLabel(BEARER_TOKEN_LABEL));

        tokenTextField = new JTextField(TEXT_FIELD_COLUMNS);
        tokenTextField.addActionListener(e -> {
            controller.onUserActivity();
            authSetup.onAccessTokenReceived(tokenTextField.getText());
        });
        this.add(tokenTextField);
    }

    @Override
    public synchronized void onAccessTokenReceived(String accessToken) {
        SwingUtilities.invokeLater(() -> tokenTextField.setText(accessToken));
    }
}
