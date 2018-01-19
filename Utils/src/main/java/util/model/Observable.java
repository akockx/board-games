/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.model;

import java.util.Observer;

/**
 * Interface to be implemented by classes that can be observed by a PanelView.
 *
 * @author A.C. Kockx
 */
public interface Observable {
    void addObserver(Observer o);

    void removeObserver(Observer o);

    void setChangedAndNotifyObservers();
}
