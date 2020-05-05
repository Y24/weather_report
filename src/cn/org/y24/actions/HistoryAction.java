package cn.org.y24.actions;

import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.enums.HistoryActionType;

import java.util.List;

public class HistoryAction {
    private final HistoryActionType type;

    public AccountEntity getAccount() {
        return account;
    }

    private final AccountEntity account;
    private List<QueryHistoryEntity> historyList;

    public HistoryActionType getType() {
        return type;
    }

    public HistoryAction(HistoryActionType type, AccountEntity account) {
        this.type = type;
        this.account = account;
    }

    public HistoryAction(HistoryActionType type, AccountEntity account, List<QueryHistoryEntity> historyList) {
        this.type = type;
        this.account = account;
        this.historyList = historyList;
    }

    public List<QueryHistoryEntity> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<QueryHistoryEntity> historyList) {
        this.historyList = historyList;
    }

}
