package com.etec.zl.conecta.Domain.Entities.Users;

import com.etec.zl.conecta.Domain.ValueObjects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private String id;
    private Name nome;
    private Email email;
    private PhoneNumber numero;
    private Password senha;
    private Tipo tipo;
    private TokenUpdater passwordUpdater;
    private TokenUpdater emailUpdater;
    private List<String> turmasIds;

    public User(String id, Name nome, Email email, PhoneNumber numero, Password senha, Tipo tipo, TokenUpdater passwordUpdater, TokenUpdater emailUpdater, List<String> turmasIds) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.numero = numero;
        this.senha = senha;
        this.tipo = tipo;
        this.passwordUpdater = passwordUpdater;
        this.emailUpdater = emailUpdater;
        this.turmasIds = turmasIds;
    }

    public User(String id, Name nome, Email email, PhoneNumber numero, Password senha, Tipo tipo, List<String> turmasIds) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.numero = numero;
        this.senha = senha;
        this.tipo = tipo;
        this.turmasIds = turmasIds;
        this.passwordUpdater = null;
        this.emailUpdater = null;
    }

    public User() {
    }

    public void alteraTipo(Tipo tipo) {
        this.tipo = this.tipo.mudarPara(tipo);
    }

    public void alteraNome(Name nome) {
        this.nome = nome;
    }

    public void desativa() {
        this.tipo = this.tipo.desativar();
    }

    public void sendUpdateEmailToken(){
        this.emailUpdater = this.emailUpdater.Start();
    }

    public void sendUpdatePasswordToken(){
        this.passwordUpdater = this.passwordUpdater.Start();
    }

    public void checkAndChangeEmail(UUID token, Email email) {
        emailUpdater.Check(token.toString());
        this.email = email;
        this.emailUpdater = null;
    }

    public void checkAndChangePassword(UUID token, Password password) {
        passwordUpdater.Check(token.toString());
        this.senha = password;
        this.passwordUpdater = null;
    }

    public void addTurmas(List<String> turmasIds) {
        if (this.turmasIds == null) {
            this.turmasIds = new ArrayList<>();
        }
        turmasIds.forEach(id -> {
            if (!this.turmasIds.contains(id)) {
                this.turmasIds.add(id);
            }
        });
    }

    public String getId() {
        return id;
    }

    public Name getNome() {
        return nome;
    }

    public PhoneNumber getNumero() {
        return numero;
    }

    public Email getEmail() {
        return email;
    }

    public Password getSenha() {
        return senha;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public TokenUpdater getPasswordUpdater() {
        return passwordUpdater;
    }

    public TokenUpdater getEmailUpdater() {
        return emailUpdater;
    }

    public List<String> getTurmasIds() {
        return turmasIds;
    }
}
