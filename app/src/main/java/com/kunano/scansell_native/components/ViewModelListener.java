package com.kunano.scansell_native.components;

@FunctionalInterface
public interface ViewModelListener<T> {
    abstract void result(T object);
}
