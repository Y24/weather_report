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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.org.y24.utils.StringMatcher.getDivByClassname;
import static cn.org.y24.utils.StringMatcher.match;

/**
 * Fetch the weather of the target city from the server side.
 */
public class CityWeatherManager implements IManager<CityWeatherAction>, IUrlProvider {

    private CityEntity city;
    private static final String[] divClasses = {
            "wea_alert clearfix", "wea_weather clearfix", "wea_about clearfix", "wea_tips clearfix"
    };

    @Override
    public String getUrl() {
        return "https://tianqi.moji.com/weather/china" +
                "/" + city.getProvince() +
                "/" + city.getName();
    }

    @Override
    public boolean execute(CityWeatherAction action) {
        city = action.getCityEntity();
        final CityWeatherActionType type = action.getType();
        final var handler = new UrlHandler();
        if (type == CityWeatherActionType.fetch) {
            if (!handler.handle(getUrl(), emptyOptions)) {
                handler.dispose();
                action.setWeather(WeatherEntity.nullWeatherEntity);
                return false;
            }
            try {
                final var reader = handler.getReader();
                final String title = handleTitle(reader);
                if (title.equals("")) {
                    handler.dispose();
                    return false;
                }
                final String weaAlert = handleWeaAlert(reader);
                final Map<String, String> weaWeather = handleWeaWeather(reader);
                final Map<String, String> weaAbout = handleWeaAbout(reader);
                final String weaTips = handleWeaTips(reader);
                assert weaWeather != null;
                assert weaAbout != null;
                action.setWeather(new WeatherEntity(weaAlert,
                        Integer.parseInt(weaWeather.get("temp")),
                        weaAbout.get("humidity"),
                        weaWeather.get("weather"),
                        weaAbout.get("wind"),
                        weaWeather.get("time"),
                        weaTips));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                action.setWeather(WeatherEntity.nullWeatherEntity);
                return false;
            } finally {
                handler.dispose();
            }
        } else {
            return false;
        }
    }

    private String handleWeaTips(BufferedReader reader) throws IOException {

        final Pattern tipsPattern = Pattern.compile("\\s*<em>[^<]+</em>\\s*");
        final Pattern tipsGroupPattern = Pattern.compile("(\\s*<em>)([^<]+)(</em>\\s*)");
        final String str = getDivByClassname(reader, divClasses[3]);
        return match(str, tipsPattern, tipsGroupPattern);
    }

    private Map<String, String> handleWeaAbout(BufferedReader reader) throws IOException {
        final Map<String, String> map = new HashMap<>();

        final Pattern humidityPattern = Pattern.compile("\\s*<span>[^<]+</span>\\s*");
        final Pattern humidityGroupPattern = Pattern.compile("(\\s*<span>)([^<]+)(</span>\\s*)");
        final Pattern windPattern = Pattern.compile("\\s*<em>[^<]+</em>\\s*");
        final Pattern windGroupPattern = Pattern.compile("(\\s*<em>)([^<]+)(</em>\\s*)");

        final String str = getDivByClassname(reader, divClasses[2]);

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

        final String str = getDivByClassname(reader, divClasses[1]);

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
        Matcher alertMatcher = alertPattern.matcher(getDivByClassname(reader, divClasses[0]));
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
