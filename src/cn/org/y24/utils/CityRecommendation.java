package cn.org.y24.utils;

import cn.org.y24.interfaces.IRecommend;
import cn.org.y24.entity.CityEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Recommend cities when the user is typing.
 */
public class CityRecommendation implements IRecommend<String, List<CityEntity>> {
    final List<CityEntity> cities;

    public CityRecommendation(List<CityEntity> cities) {
        this.cities = cities;
    }

    @Override
    public List<CityEntity> recommend(String target) {
        List<CityEntity> result = new ArrayList<>();
        cities.forEach(city -> {
            if (city.getChinese().contains(target) || city.getProvince().contains(target) || city.getName().contains(target))
                result.add(city);
        });
        return result;
    }
}
