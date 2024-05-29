package com.kunano.scansell.components;

@FunctionalInterface
public interface ViewModelListener<T> {
    abstract void result(T object);
}
