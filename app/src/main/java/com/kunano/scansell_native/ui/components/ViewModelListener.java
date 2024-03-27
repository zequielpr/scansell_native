package com.kunano.scansell_native.ui.components;

@FunctionalInterface
public interface ViewModelListener<T> {
    abstract void result(T object);
}
