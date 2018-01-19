/*
 * Copyright (c) 2018 A.C. Kockx, All Rights Reserved.
 */
package util.model;

import java.util.Observable;

/**
 * Extension of the class java.util.Observable that exposes the setChanged method,
 * so that this method can be called by objects that contain an observable object
 * rather than extend an observable object (favor composition over inheritance).
 *
 * In other words:
 * This is the only way to re-use the code from the class java.util.Observable
 * without extending it in objects that implement the interface util.Observable.
 *
 * @author A.C. Kockx
 */
public final class ExposedObservable extends Observable {
    @Override
    public void setChanged() {
        super.setChanged();
    }
}
