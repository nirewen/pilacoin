package br.ufsm.csi.tapw.pilacoin.util;

import java.util.Stack;

public class SizedStack<T> extends Stack<T> {
    private final int maxSize;

    public SizedStack(int size) {
        super();
        this.maxSize = size;
    }

    @Override
    public T push(T object) {
        while (this.size() >= maxSize) {
            this.remove(0);
        }

        return super.push(object);
    }
}
