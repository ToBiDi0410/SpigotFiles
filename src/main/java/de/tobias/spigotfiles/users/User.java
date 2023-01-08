package de.tobias.spigotfiles.users;

import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.ArrayList;
import java.util.UUID;

public class User {

    public String username;
    public String passwordHash;
    public ArrayList<UserPermission> permissions;
    public String ID;

    public User(String name) {
        ID = UUID.randomUUID().toString();
        username = name;
        permissions = new ArrayList<>();
    }

    public void updatePassword(String pass) {
        passwordHash = BCrypt.withDefaults().hashToString(12, pass.toCharArray());
    }

    public boolean isValidPassword(String pass) {
        return BCrypt.verifyer().verify(pass.toCharArray(), passwordHash).verified;
    }

}
