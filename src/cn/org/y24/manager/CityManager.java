package cn.org.y24.manager;

import cn.org.y24.actions.CityAction;
import cn.org.y24.interfaces.IManager;
import cn.org.y24.entity.CityEntity;
import cn.org.y24.enums.CityActionType;
import cn.org.y24.utils.UrlHandler;
import cn.org.y24.interfaces.IUrlProvider;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.org.y24.utils.StringMatcher.getStrByClassname;

/**
 * Fetch all the available cities in the cloud.
 */
public class CityManager implements IManager<CityAction>, IUrlProvider {
    private static final String divClass = "city clearfix";

    @Override
    public boolean execute(CityAction action) {
        final CityActionType type = action.getType();
        final List<CityEntity> cityList = new ArrayList<>();
        final var handler = new UrlHandler();
        switch (type) {
            case fetch -> {
                try {
                    if (!handler.handle(getUrl(), emptyOptions)) {
                        handler.dispose();
                        action.setCityList(cityList);
                        return false;
                    }
                    final var reader = handler.getReader();
                    final Map<String, String> provinces = getProvinces(getStrByClassname(reader, "div", divClass, false));
                    provinces.forEach((province, cnName) ->
                            cityList.addAll(getCities(province, cnName))
                    );
                    action.setCityList(cityList);
                } catch (IOException e) {
                    action.setCityList(cityList);
                    e.printStackTrace();
                    return false;

                } finally {
                    handler.dispose();
                }
            }
            case clear -> action.setCityList(cityList);
        }
        return true;
    }

    private List<CityEntity> getCities(String province, String cnName) {
        List<CityEntity> res = new ArrayList<>();
        final var handler = new UrlHandler();
        if (!handler.handle(getUrl() + "/" + province, emptyOptions)) {
            handler.dispose();
            return res;
        }
        final var reader = handler.getReader();
        /*
        *  <li>
                        <a
                            href="https://tianqi.moji.com/weather/china/beijing/beijing-olympic-forest-park">北京奥林匹克公园</a>
                    </li>*/
        final Pattern pattern = Pattern.compile("\\s*<li>\\s*<a\\s* href=\"https://tianqi.moji.com/weather/china/" + province + "/" + "[a-zA-Z-]+\">[^<]+</a>\\s*</li>\\s*");
        final Pattern groupPattern = Pattern.compile("(\\s*<li>\\s*<a\\s* href=\"https://tianqi.moji.com/weather/china/" + province + "/)([a-zA-Z-]+)(\">)([^<]+)(</a>\\s*</li>\\s*)");
        Matcher matcher;
        try {
            matcher = pattern.matcher(getStrByClassname(reader, "div", "city clearfix", false));
            while (matcher.find()) {
                final Matcher groupMatcher = groupPattern.matcher(matcher.group());
                if (groupMatcher.matches()) {
                    res.add(new CityEntity(province, groupMatcher.group(2), cnName + " " + groupMatcher.group(4)));
                } else break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        handler.dispose();
        return res;
    }

    private Map<String, String> getProvinces(String str) {
        Map<String, String> res = new HashMap<>();
//        <li><a href="/weather/china/anhui">安徽</a></li>
        final Pattern pattern = Pattern.compile("\\s*<li><a href=\"/weather/china/[a-zA-Z]+-?[a-zA-Z]+\">[^<]+</a></li>\\s*");
        final Pattern groupPattern = Pattern.compile("(\\s*<li><a href=\"/weather/china/)([a-zA-Z]+-?[a-zA-Z]+)(\">)([^<]+)(</a></li>\\s*)");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            final Matcher groupMatcher = groupPattern.matcher(matcher.group());
            if (groupMatcher.matches()) {
                res.put(groupMatcher.group(2), groupMatcher.group(4));
            } else break;
        }
        return res;
    }

    @Override
    public String getUrl() {
        return "https://tianqi.moji.com/weather/china";
    }
}
