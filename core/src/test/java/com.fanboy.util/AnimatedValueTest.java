package com.fanboy.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnimatedValueTest {
    @Test
    public void shouldGetValue() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(14);

        // when
        double value = animatedValue.getValue();

        // then
        assertEquals(14, value, 0.000001);
    }

    @Test
    public void shouldUpdateToCurrent() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(14);

        // when
        animatedValue.update(1.1);

        // then
        assertEquals(14, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateUpTo() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(10);
        animatedValue.setValue(11);

        // when
        animatedValue.update(0.1);

        // then
        assertEquals(10.1, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateDownTo() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(10);
        animatedValue.setValue(9);

        // when
        animatedValue.update(0.1);

        // then
        assertEquals(9.9, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateUpToLimit() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(10);
        animatedValue.setValue(11);

        // when
        animatedValue.update(1.5);

        // then
        assertEquals(11.0, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateDownToLimit() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(10);
        animatedValue.setValue(9);

        // when
        animatedValue.update(1.5);

        // then
        assertEquals(9.0, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateUpToWithExceedingSpeed() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(10);
        animatedValue.setValue(15, 2.5);

        // when
        animatedValue.update(5.0);

        // then
        assertEquals(animatedValue.getValue(), 15.0, 0.000001);
    }

    @Test
    public void shouldUpdateUpToWithDifferentSpeed() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(10);
        animatedValue.setValue(25, 5.0);

        // when
        animatedValue.update(2.5);

        // then
        assertEquals(17.5, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateNegativeNumber() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(-5.0);
        animatedValue.setValue(-1.0, 2.0);

        // when
        animatedValue.update(0.25);

        // then
        assertEquals(-4.5, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldUpdateNegativeToPositiveNumber() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(-1.0);
        animatedValue.setValue(1.0);

        // when
        animatedValue.update(0.25);

        // then
        assertEquals(-0.5, animatedValue.getValue(), 0.000001);
    }

    @Test
    public void shouldNotInvokeOnFinish() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(0.0);
        MockRunnable mock = new MockRunnable();
        animatedValue.setValue(5.0, 1.0, mock);

        // when
        animatedValue.update(0.9);

        // then
        assertFalse(mock.wasRun);
    }

    @Test
    public void shouldInvokeOnFinish() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(0.0);
        MockRunnable mock = new MockRunnable();
        animatedValue.setValue(5.0, 1.0, mock);

        // when
        animatedValue.update(1.01);

        // then
        assertTrue(mock.wasRun);
    }

    @Test
    public void shouldNotBeFinished() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(0.0);
        animatedValue.setValue(5.0, 1.0);

        // when
        animatedValue.update(0.4);
        animatedValue.update(0.4);

        // then
        assertFalse(animatedValue.isFinished());
    }

    @Test
    public void shouldBeFinished() {
        // given
        AnimatedValue animatedValue = new AnimatedValue(0.0);
        animatedValue.setValue(5.0, 1.0);

        // when
        animatedValue.update(1.01);

        // then
        assertTrue(animatedValue.isFinished());
    }
}

class MockRunnable implements Runnable {
    public boolean wasRun = false;

    @Override
    public void run() {
        wasRun = true;
    }
}
