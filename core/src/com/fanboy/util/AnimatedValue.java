package com.fanboy.util;

import com.fanboy.util.fx.LinearTransition;
import com.fanboy.util.fx.Transition;

import static java.lang.Double.compare;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class AnimatedValue {
    private final Transition transition;
    private Runnable onFinish;

    private double passedDuration = 0.0;
    private double duration;
    private double startValue;
    private double currentValue;
    private double destinationValue;

    public AnimatedValue(double value) {
        this(value, new LinearTransition());
    }

    AnimatedValue(double value, Transition transition) {
        this.currentValue = value;
        this.destinationValue = value;
        this.transition = transition;
    }

    public void setValue(double value) {
        setValue(value, 1.0);
    }

    public void setValue(double value, double duration) {
        setValue(value, duration, null);
    }

    public void setValue(double value, double duration, Runnable onFinish) {
        this.startValue = currentValue;
        this.destinationValue = value;
        this.duration = duration;
        this.passedDuration = 0.0;
        this.onFinish = onFinish;
    }

    public double getValue() {
        return currentValue;
    }

    public void update(double seconds) {
        if (seconds < 0) {
            throw new RuntimeException("Negative argument for AnimationValue.update()");
        }
        this.passedDuration += seconds;
        updateCurrentValue();
    }

    private void updateCurrentValue() {
        int compare = compare(startValue, destinationValue);
        double progress = min(1.0, passedDuration / duration);
        double difference = destinationValue - startValue;
        double newValue = startValue + difference * transition.valueOf(progress);

        if (compare < 0) {
            currentValue = min(newValue, destinationValue);
        }
        if (compare > 0) {
            currentValue = max(newValue, destinationValue);
        }
        invokeListenerIfDue();
    }

    public boolean isFinished() {
        return compare(currentValue, destinationValue) == 0;
    }

    private void invokeListenerIfDue() {
        if (onFinish != null) {
            if (isFinished()) {
                onFinish.run();
                onFinish = null;
            }
        }
    }
}
