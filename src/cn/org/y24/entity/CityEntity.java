package cn.org.y24.entity;

import cn.org.y24.interfaces.IEntity;

import java.util.HashMap;
import java.util.Map;

public class CityEntity implements IEntity {
    public static CityEntity nullCity = new CityEntity("", "");
    private final String province;
    private final String name;
    private final String chinese;


    public CityEntity(String province, String name) {
        this.province = province;
        this.name = name;
        this.chinese = "";
    }

    /// regex = province,city
    /// jiangxi,fuzhou
    public CityEntity(String regex) {
        final String[] both = regex.split(",", 2);
        province = both[0];
        name = both[1];
        chinese = null;
    }

    public CityEntity(String province, String name, String chinese) {
        this.province = province;
        this.name = name;
        this.chinese = chinese;
    }


    public String getChinese() {
        return chinese;
    }

    public String getProvince() {
        return province;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return province + "," + name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CityEntity
                && province.equals(((CityEntity) obj).province)
                && name.equals(((CityEntity) obj).name);
    }

    @Override
    public Map<String, String> toMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("province", province);
        return map;
    }
}
