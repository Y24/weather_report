package cn.org.y24.test;

import cn.org.y24.actions.AccountAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.AccountActionType;
import cn.org.y24.interfaces.IManager;
import cn.org.y24.manager.AccountManager;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AccountManagerTest {

    @Test
    void testRightAccount() {
        final IManager<AccountAction> manager = new AccountManager();
        final AccountEntity account = new AccountEntity("y24", "yue");
        assertTrue(manager.execute(new AccountAction(AccountActionType.login, account)));
        assertTrue(manager.execute(new AccountAction(AccountActionType.logout, account)));
        assertFalse(manager.execute(new AccountAction(AccountActionType.register, account)));
        assertTrue(manager.execute(new AccountAction(AccountActionType.dispose, account)));
        assertFalse(manager.execute(new AccountAction(AccountActionType.login, account)));
        assertFalse(manager.execute(new AccountAction(AccountActionType.dispose, account)));
        assertTrue(manager.execute(new AccountAction(AccountActionType.register, account)));
        assertTrue(manager.execute(new AccountAction(AccountActionType.login, account)));
    }

    @Test
    void testWrongAccount() {
        AccountManager manager = new AccountManager();
        AccountEntity account = new AccountEntity("yue", "y");
    }

}