package cn.org.y24.utils;

import cn.org.y24.entity.CityEntity;

public abstract class CityUtil {
    public static boolean shouldRecommend(CityEntity city, String target) {
        final String s = target.trim().toLowerCase();
        return (city.getName().contains(s) || city.getProvince().contains(s) || city.toString().contains(s));
    }

    public static CityEntity getCity(String string) {
        if (string.equals("") || !string.contains(",")) {
            return CityEntity.nullCity;
        } else
            return new CityEntity(string);
    }
}
