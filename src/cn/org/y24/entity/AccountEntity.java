package cn.org.y24.entity;

import cn.org.y24.interfaces.IEntity;

import java.util.HashMap;
import java.util.Map;

public class AccountEntity implements IEntity {
    public static final AccountEntity nullAccount = new AccountEntity("", "");
    private final String name;
    /*
     * Note: the password is not the real password.
     * Instead, it stores the user input infos.
     * input password when login,
     * register password when registering.
     * */
    private final String password;

    public AccountEntity() {
        this.name = "";
        this.password = "";
    }

    public AccountEntity(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AccountEntity && name.equals(((AccountEntity) obj).name);
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("password", password);
        return map;
    }
}
