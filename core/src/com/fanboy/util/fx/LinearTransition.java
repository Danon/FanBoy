package com.fanboy.util.fx;

public class LinearTransition implements Transition {
    @Override
    public double valueOf(double value) {
        return Transition.linearTransition(value);
    }
}
