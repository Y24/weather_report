package cn.org.y24.manager;

import cn.org.y24.actions.CityWeatherAction;
import cn.org.y24.interfaces.IManager;
import cn.org.y24.entity.CityEntity;
import cn.org.y24.enums.CityWeatherActionType;
import cn.org.y24.utils.UrlHandler;
import cn.org.y24.interfaces.IUrlProvider;
import cn.org.y24.entity.WeatherEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.org.y24.utils.StringMatcher.getStrByClassname;
import static cn.org.y24.utils.StringMatcher.match;

/**
 * Fetch the weather of the target city from the server side.
 */
public class CityWeatherManager implements IManager<CityWeatherAction>, IUrlProvider {

    private CityEntity city;
    private CityWeatherActionType type;
    private static final String[] toDayClasses = {
            "wea_alert clearfix", "wea_weather clearfix", "wea_about clearfix", "wea_tips clearfix"
    };
    private static final String[] sevenDaysClasses = {
            "detail_future_title", "clearfix"
    };

    @Override
    public String getUrl() {
        final String target;
        switch (type) {
            case fetchToday -> target = "weather";
            case fetch7days -> {
                target = "forecast7";
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
        return "https://tianqi.moji.com/" + target + "/china" +
                "/" + city.getProvince() +
                "/" + city.getName();
    }

    @Override
    public boolean execute(CityWeatherAction action) {
        city = action.getCityEntity();
        type = action.getType();
        final var handler = new UrlHandler();
        if (!handler.handle(getUrl(), emptyOptions)) {
            handler.dispose();
            action.setWeather(Collections.singletonList(WeatherEntity.nullWeatherEntity));
            return false;
        }
        final var reader = handler.getReader();
        try {
            final String title = handleTitle(reader);
            if (title.equals("")) {
                handler.dispose();
                return false;
            }
            switch (type) {
                case fetchToday -> {
                    final String weaAlert = handleWeaAlert(reader);
                    final Map<String, String> weaWeather = handleWeaWeather(reader);
                    final Map<String, String> weaAbout = handleWeaAbout(reader);
                    final String weaTips = handleWeaTips(reader);
                    assert weaWeather != null;
                    assert weaAbout != null;
                    action.setWeather(Collections.singletonList(new WeatherEntity(weaAlert,
                            weaWeather.get("temp"),
                            weaAbout.get("humidity"),
                            weaWeather.get("weather"),
                            weaAbout.get("wind"),
                            weaWeather.get("time"),
                            weaTips)));
                    return true;
                }

                case fetch7days -> {
                    final var result = handle7Days(reader);
                    assert result != null;
                    action.setWeather(result);
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            action.setWeather(Collections.singletonList(WeatherEntity.nullWeatherEntity));
        } finally {
            handler.dispose();
        }
        return false;
    }

    private List<WeatherEntity> handle7Days(BufferedReader reader) throws IOException {
        final List<WeatherEntity> result = new ArrayList<>(7);
        final String updateTime = handle7DaysUpdateTime(reader);
        final String[][] weatherList = handle7DaysWeather(reader);
        if (weatherList == null)
            return null;
        for (int i = 0; i < 7; i++)
            result.add(new WeatherEntity("", weatherList[i][3] + " - " + weatherList[i][2], "", weatherList[i][1] + " " + weatherList[i][4], "", updateTime, weatherList[i][5] + " " + weatherList[i][0]));
        return result;
    }

    private String handle7DaysUpdateTime(BufferedReader reader) throws IOException {
        final Pattern updateTimePattern = Pattern.compile("\\s*<span>[^<]+</span>\\s*");
        final Pattern updateTimeGroupPattern = Pattern.compile("(\\s*<span>)([^<]+)(</span>\\s*)");
        return match(getStrByClassname(reader, "div", sevenDaysClasses[0], false), updateTimePattern, updateTimeGroupPattern);

    }

    private String[][] handle7DaysWeather(BufferedReader reader) throws IOException {
        final String[][] result = new String[7][6];
//        <span class="wea">雷阵雨</span>
        final Pattern weatherPattern = Pattern.compile("\\s*<span class=\"wea\">[^<]+</span>\\s*");
        final Pattern weatherGroupPattern = Pattern.compile("(\\s*<span class=\"wea\">)([^<]+)(</span>\\s*)");
        final Pattern timePattern = Pattern.compile("\\s*<span class=\"week\">[^<]+</span>\\s*");
        final Pattern timeGroupPattern = Pattern.compile("(\\s*<span class=\"week\">)([^<]+)(</span>\\s*)");
        final Pattern lowTempPattern = Pattern.compile("\\s*<b>[^<]+</b>\\s*");
        final Pattern lowTempGroupPattern = Pattern.compile("(\\s*<b>)([^<]+)(</b>\\s*)");
        final Pattern highTempPattern = Pattern.compile("\\s*<strong>[^<]+</strong>\\s*");
        final Pattern highTempGroupPattern = Pattern.compile("(\\s*<strong>)([^<]+)(</strong>\\s*)");
        final Pattern[] patterns = {timePattern, weatherPattern, lowTempPattern, highTempPattern, weatherPattern, timePattern};
        final Pattern[] groupPatterns = {timeGroupPattern, weatherGroupPattern, lowTempGroupPattern, highTempGroupPattern, weatherGroupPattern, timeGroupPattern};
        final int[] magicNumbers = {0, 0, 1, 1, 2, 2};
        for (int i = 0; i < 7; i++) {
            final String[] str = getStrByClassname(reader, "li", "active", true).split("<img", 3);
            for (int j = 0; j < 6; j++) {
                if (match(str[magicNumbers[j]], patterns[j], groupPatterns[j]).equals("")) return null;
                else result[i][j] = match(str[magicNumbers[j]], patterns[j], groupPatterns[j]);
            }
        }
        return result;

    }

    private String handleWeaTips(BufferedReader reader) throws IOException {

        final Pattern tipsPattern = Pattern.compile("\\s*<em>[^<]+</em>\\s*");
        final Pattern tipsGroupPattern = Pattern.compile("(\\s*<em>)([^<]+)(</em>\\s*)");
        final String str = getStrByClassname(reader, "div", toDayClasses[3], false);
        return match(str, tipsPattern, tipsGroupPattern);
    }

    private Map<String, String> handleWeaAbout(BufferedReader reader) throws IOException {
        final Map<String, String> map = new HashMap<>();
        final Pattern humidityPattern = Pattern.compile("\\s*<span>[^<]+</span>\\s*");
        final Pattern humidityGroupPattern = Pattern.compile("(\\s*<span>)([^<]+)(</span>\\s*)");
        final Pattern windPattern = Pattern.compile("\\s*<em>[^<]+</em>\\s*");
        final Pattern windGroupPattern = Pattern.compile("(\\s*<em>)([^<]+)(</em>\\s*)");

        final String str = getStrByClassname(reader, "div", toDayClasses[2], false);

        if (match(str, humidityPattern, humidityGroupPattern).equals("")) return null;
        else map.put("humidity", match(str, humidityPattern, humidityGroupPattern));
        if (match(str, windPattern, windGroupPattern).equals("")) return null;
        else map.put("wind", match(str, windPattern, windGroupPattern));
        return map;
    }

    private Map<String, String> handleWeaWeather(BufferedReader reader) throws IOException {
        final Map<String, String> map = new HashMap<>();

        final Pattern tempPattern = Pattern.compile("\\s*<em>\\d+</em>\\s*");
        final Pattern tempGroupPattern = Pattern.compile("(\\s*<em>)(\\d+)(</em>\\s*)");
        final Pattern weatherPattern = Pattern.compile("\\s*<b>[^<]+</b>\\s*");
        final Pattern weatherGroupPattern = Pattern.compile("(\\s*<b>)([^<]+)(</b>\\s*)");
        final Pattern timePattern = Pattern.compile("\\s*<strong[^>]+>[^<]+</strong>\\s*");
        final Pattern timeGroupPattern = Pattern.compile("(\\s*<strong[^>]+>)([^<]+)(</strong>\\s*)");

        final String str = getStrByClassname(reader, "div", toDayClasses[1], false);

        if (match(str, tempPattern, tempGroupPattern).equals("")) return null;
        else map.put("temp", match(str, tempPattern, tempGroupPattern));
        if (match(str, weatherPattern, weatherGroupPattern).equals("")) return null;
        else map.put("weather", match(str, weatherPattern, weatherGroupPattern));
        if (match(str, timePattern, timeGroupPattern).equals("")) return null;
        else map.put("time", match(str, timePattern, timeGroupPattern));
        return map;
    }


    private String handleWeaAlert(BufferedReader reader) throws IOException {
        final StringBuilder weaAlertStr = new StringBuilder();
        final Pattern alertPattern = Pattern.compile("\\s*<em>[^<]+</em>\\s*");
        final Pattern alertStrPattern = Pattern.compile("(\\s*<em>)([^<]+)(</em>\\s*)");
        Matcher alertMatcher = alertPattern.matcher(getStrByClassname(reader, "div", toDayClasses[0], false));
        while (alertMatcher.find()) {
            final Matcher matcher = alertStrPattern.matcher(alertMatcher.group());
            if (matcher.matches()) {
                weaAlertStr.append(matcher.group(2)).append(" ");
            } else {
                return "";
            }
        }
        return weaAlertStr.toString();
    }

    private String handleTitle(BufferedReader reader) throws IOException {
        final Pattern titlePattern = Pattern.compile("(\\s*<title>)(.*)(</title>\\s*)");
        while (true) {
            Matcher titleMatcher = titlePattern.matcher(reader.readLine());
            if (titleMatcher.matches()) {
                return titleMatcher.group(2);
            }
        }
    }


}
