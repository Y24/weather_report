package cn.org.y24.manager;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.AccountActionType;
import cn.org.y24.interfaces.IManager;
import cn.org.y24.interfaces.IUrlProvider;
import cn.org.y24.actions.AccountAction;
import cn.org.y24.utils.UrlHandler;

import java.io.IOException;

public class AccountManager implements IManager<AccountAction>, IUrlProvider {

    private AccountActionType actionType;

    @Override
    public boolean execute(AccountAction accountAction) {
        final AccountEntity account = accountAction.getAccount();
        actionType = accountAction.getType();
        final var handler = new UrlHandler();
        if (!handler.handle(getUrl(), account.toMap())) {
            handler.dispose();
            return false;
        }
        try {
            final var reader = handler.getReader();
            if (!reader.readLine().startsWith("OK")) {
                handler.dispose();
                return false;
            }
            if (reader.readLine().startsWith("succeed")) {
                handler.dispose();
                return true;
            } else {
                handler.dispose();
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            handler.dispose();
        }
    }

    @Override
    public String getUrl() {
        return baseUrl + actionType.toString();
    }
}
