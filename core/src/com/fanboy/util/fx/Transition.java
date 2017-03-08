package com.fanboy.util.fx;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

public interface Transition {
    double valueOf(double value);

    static double linearTransition(double value) {
        return value;
    }

    static double cosineTransition(double value) {
        return -(cos(value * PI) - 1) / 2;
    }

    static double inOutQuart(double value) {
        double t = value * 2;
        if (t < 1) {
            return 0.5 * t * t * t * t;
        } else {
            t = t - 2;
            return -0.5 * (t * t * t * t - 2);
        }
    }
}
