/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Exits the progam when escape key is pressed.
 *
 * @author A.C. Kockx
 */
public final class ExitOnEscapeKeyListener implements KeyListener {
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) System.exit(0);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
