package cn.org.y24.actions;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.AccountActionType;

public class AccountAction {
    public AccountActionType getType() {
        return type;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public AccountAction(AccountActionType type, AccountEntity account) {
        this.type = type;
        this.account = account;
    }

    private final AccountActionType type;
    private final AccountEntity account;
}
