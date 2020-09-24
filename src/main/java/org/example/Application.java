package org.example;

import org.example.gui.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.awt.*;

@SpringBootApplication
public class Application {

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = new SpringApplicationBuilder(Application.class).headless(false).run(args);
        EventQueue.invokeLater(()->{
            MainFrame frame = context.getBean(MainFrame.class);
            frame.setVisible(true);
        });
    }
}
