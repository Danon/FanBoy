package com.fanboy.util.error;

import com.badlogic.gdx.utils.StringBuilder;

public class ExceptionMessage {
    public static String messageForException(Throwable throwable) {
        return throwable.getClass().getName() + ": " + throwable.getMessage()
                + "\n\n" + stackTraceToString(throwable);
    }

    private static String stackTraceToString(Throwable throwable) {
        StackTraceElement[] array = throwable.getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : array) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}
