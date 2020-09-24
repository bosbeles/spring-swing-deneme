package org.example.gui;

import org.example.gui.panel.BasePanel;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MainFrame extends JFrame {

    private final Map<String, JPanel> factory = new HashMap<>();


    @Autowired
    private ApplicationContext appContext;

    public Map<String, JPanel> getFactory() {
        return factory;
    }

    @Autowired
    public MainFrame(ListableBeanFactory beanFactory, @Qualifier("menuList") List<String> menuList) {

        setTitle("Main Frame");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setSize(800, 600);
        setLocationRelativeTo(null);

        JDesktopPane desktopPane = new JDesktopPane();

        Collection<BasePanel> interfaces = beanFactory.getBeansOfType(BasePanel.class).values();
        interfaces.forEach(panel -> factory.put(panel.getIdentifier(), panel));

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Windows");
        menuBar.add(menu);
        menuList.forEach(m -> {
            JMenuItem item = new JMenuItem(m);
            menu.add(item);
            item.addActionListener(e -> {

                JPanel panel = factory.get(m);
                if (panel != null) {
                    JInternalFrame internalFrame = new JInternalFrame();
                    internalFrame.setResizable(true);
                    internalFrame.setClosable(true);
                    internalFrame.setIconifiable(true);
                    Dimension preferredSize = panel.getPreferredSize();
                    internalFrame.setSize(preferredSize.width + 5, preferredSize.height + 20);
                    internalFrame.getContentPane().add(panel);

                    desktopPane.add(internalFrame);
                    internalFrame.setVisible(true);
                    panel.setVisible(true);
                }
            });

        });
        setJMenuBar(menuBar);
        add(desktopPane);


    }


}
