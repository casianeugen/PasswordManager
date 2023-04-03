package com.example.passwordmanager;

public class Password {
    private String password;
    private String hint;

    public Password() {}

    public Password(String password, String hint) {
        this.password = password;
        this.hint = hint;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
