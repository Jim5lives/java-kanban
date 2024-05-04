package com.yandex.app.service;

public class TimeCollisionException extends RuntimeException {

    public TimeCollisionException(String message) {
        super(message);
    }
}
