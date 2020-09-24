package org.example.gui.panel;

import javax.swing.*;

public abstract class BasePanel extends JPanel {

    private String identifier;

    public BasePanel() {
        this("");
    }

    public BasePanel(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void beforeVisible() {

    }


    public void beforeHidden() {

    }


    public void afterVisible() {

    }

    public void afterHidden() {

    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            beforeVisible();
        } else {
            beforeHidden();
        }

        super.setVisible(visible);

        if (visible) {
            afterVisible();
        } else {
            afterHidden();
        }

    }

}
