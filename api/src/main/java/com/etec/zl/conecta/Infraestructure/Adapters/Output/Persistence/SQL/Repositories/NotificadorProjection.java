package com.etec.zl.conecta.Infraestructure.Adapters.Output.Persistence.SQL.Repositories;

public interface NotificadorProjection {
    Long getId();
    String getEndpoint();
    String getP256dh();
    String getAuth();
}