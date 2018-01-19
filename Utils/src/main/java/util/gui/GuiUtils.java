/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.gui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsEnvironment;

/**
 * @author A.C. Kockx
 */
public final class GuiUtils {
    private GuiUtils() {
    }

    /**
     * Creates and shows a window that contains the given content.
     *
     * @return the created frame.
     */
    public static JFrame createAndShowFrame(Component content, String title, Color backgroundColor, Color foregroundColor, boolean exitOnClose, boolean fullScreen) {
        //create contentPane.
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        //add content in the center.
        panel.add(Box.createVerticalGlue());
        panel.add(content);
        panel.add(Box.createVerticalGlue());

        //create frame.
        JFrame frame = new JFrame(title);
        if (exitOnClose) frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addKeyListener(new ExitOnEscapeKeyListener());
        frame.setContentPane(panel);

        //set colors in all components.
        frame.setBackground(backgroundColor);
        frame.setForeground(foregroundColor);
        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);

        //display frame.
        if (fullScreen) {
            //for info about full screen rendering see https://docs.oracle.com/javase/tutorial/extra/fullscreen/index.html
            frame.setUndecorated(true);//otherwise frame has a border.
            frame.setResizable(false);
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().setFullScreenWindow(frame);
        } else {//if not fullScreen.
            frame.setUndecorated(false);
            frame.pack();
            //center frame on screen.
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(true);
        return frame;
    }
}
