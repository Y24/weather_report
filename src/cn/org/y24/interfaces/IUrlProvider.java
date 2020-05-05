package cn.org.y24.interfaces;

import java.util.HashMap;
import java.util.Map;

public interface IUrlProvider {
    //    static final String url = "http://y24.org.cn:2424/";
    String baseUrl = "http://localhost:2424/";
    Map<String, String> emptyOptions = new HashMap<>();

    String getUrl();
}
