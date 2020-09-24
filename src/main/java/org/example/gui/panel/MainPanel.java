package org.example.gui.panel;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;

@Component
public class MainPanel extends BasePanel {


    public MainPanel() {
        super("Window 2");

    }

    @PostConstruct
    private void init() {
        JLabel label = new JLabel("Test");
        JTextField field = new JTextField("Field");

        add(label);
        add(field);
        add(new JCheckBox("Test 111"));
    }

    @Override
    public void beforeVisible() {
        test();
    }

    public void test() {
        System.out.println("Test");
    }

}
