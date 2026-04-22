package com.etec.zl.conecta.Application.Ports.Input.Users;

public interface VincularNotificadorPort {

    void vincular(String userId, String endpoint, String p256dh, String auth);
}
