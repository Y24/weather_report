package cn.org.y24.manager;

import cn.org.y24.actions.HistoryAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.enums.HistoryActionType;
import cn.org.y24.interfaces.IManager;
import cn.org.y24.interfaces.IUrlProvider;
import cn.org.y24.utils.UrlHandler;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class QueryHistoryManager implements IManager<HistoryAction>, IUrlProvider {
    private HistoryActionType actionType;
    //    private final List<QueryHistoryEntity> cloudHistoryList = new ArrayList<>();
//    private final List<QueryHistoryEntity> localHistoryList = new ArrayList<>();
//

    @Override
    public String getUrl() {
        return baseUrl + actionType;
    }


    @Override
    public boolean execute(HistoryAction action) {
        actionType = action.getType();
        final AccountEntity account = action.getAccount();
        final var handler = new UrlHandler();
        switch (actionType) {
            case fetch -> {
                final List<QueryHistoryEntity> historyList = new ArrayList<>();
                try {
                    if (!handler.handle(getUrl(), account.toMap())) {
                        handler.dispose();
                        action.setHistoryList(historyList);
                        return false;
                    }
                    final var reader = handler.getReader();
                    String line = reader.readLine();
                    if (!line.startsWith("OK")) {
                        action.setHistoryList(historyList);
                        handler.dispose();
                        return false;
                    }
                    while ((line = reader.readLine()) != null) {
                        historyList.add(new QueryHistoryEntity(line));
                    }
                    action.setHistoryList(historyList);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    action.setHistoryList(historyList);
                    return false;
                } finally {
                    handler.dispose();
                }
            }
            case push -> {
                final List<QueryHistoryEntity> historyList = action.getHistoryList();
                try {
                    Map<String, String> options = new LinkedHashMap<>(account.toMap());
                    options.put("count", historyList.size() + "");
                    for (int i = 0; i < historyList.size(); i++) {
                        options.put("content" + i, historyList.get(i).toString());
                    }
                    if (!handler.handle(getUrl(), options)) {
                        handler.dispose();
                        return false;
                    }
                    final var reader = handler.getReader();
                    String line = reader.readLine();
                    handler.dispose();
                    return line.startsWith("OK");
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;

                } finally {
                    handler.dispose();
                }
            }
            case clear -> action.getHistoryList().clear();
        }
        return true;
    }
}
