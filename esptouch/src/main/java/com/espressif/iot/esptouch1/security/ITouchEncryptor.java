package com.espressif.iot.esptouch1.security;

public interface ITouchEncryptor {
    byte[] encrypt(byte[] src);
}
