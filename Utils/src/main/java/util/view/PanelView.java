/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.view;

import javax.swing.JPanel;
import java.util.Observer;

/**
 * Interface to be implemented by classes that can be used as a view in a Model-View-Controller pattern.
 * View that draws an image to a panel.
 * To show the panel on screen, call the getPanel() method and put the panel in a JFrame or another Swing component.
 *
 * @author A.C. Kockx
 */
public interface PanelView extends Observer {
    JPanel getPanel();
}
