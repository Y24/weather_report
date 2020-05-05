package cn.org.y24.actions;

import cn.org.y24.entity.CityEntity;
import cn.org.y24.enums.CityActionType;

import java.util.List;

public class CityAction {
    private final CityActionType type;
    private List<CityEntity> cityList;

    public CityActionType getType() {
        return type;
    }

    public void setCityList(List<CityEntity> cityList) {
        this.cityList = cityList;
    }

    public List<CityEntity> getCityList() {
        return cityList;
    }

    public CityAction(CityActionType type) {
        this.type = type;
    }

    public CityAction(CityActionType type, List<CityEntity> cityList) {
        this.type = type;
        this.cityList = cityList;
    }

}
