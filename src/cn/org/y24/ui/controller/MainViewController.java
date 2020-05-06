package cn.org.y24.ui.controller;

import cn.org.y24.Main;
import cn.org.y24.actions.AccountAction;
import cn.org.y24.actions.CityAction;
import cn.org.y24.actions.CityWeatherAction;
import cn.org.y24.actions.HistoryAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.entity.CityEntity;
import cn.org.y24.entity.QueryHistoryEntity;
import cn.org.y24.entity.WeatherEntity;
import cn.org.y24.enums.AccountActionType;
import cn.org.y24.enums.CityActionType;
import cn.org.y24.enums.CityWeatherActionType;
import cn.org.y24.enums.HistoryActionType;
import cn.org.y24.interfaces.IManager;
import cn.org.y24.manager.AccountManager;
import cn.org.y24.manager.CityManager;
import cn.org.y24.manager.CityWeatherManager;
import cn.org.y24.manager.QueryHistoryManager;
import cn.org.y24.ui.framework.BaseStageController;
import cn.org.y24.ui.framework.Deliverer;
import cn.org.y24.ui.framework.StageManager;
import cn.org.y24.utils.AboutMessage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static cn.org.y24.utils.CityUtil.getCity;
import static cn.org.y24.utils.CityUtil.shouldRecommend;

public class MainViewController extends BaseStageController implements Initializable {

    private StageManager stageManager;
    private boolean isFirstQuery;

    private IManager<CityWeatherAction> cityWeatherManager;
    private IManager<CityAction> cityManager;
    private IManager<AccountAction> accountManager;
    private IManager<HistoryAction> historyManager;
    private AccountEntity account;

    private List<CityEntity> cityList;
    private List<CityEntity> recommendCityList;
    private List<QueryHistoryEntity> localSide;
    private List<QueryHistoryEntity> cloudSide;

    private StringProperty temp, weather, humidity, wind, tips, time, queryStatusStr, cityName;
    private BooleanProperty queryResult;
    private ObservableList<String> recommendCityNameList;
    @FXML
    private Label cityNameLabelId;
    @FXML
    private Label weatherLabelId;
    @FXML
    private AnchorPane queryResultContainerId;
    @FXML
    private Label tempLabelId;
    @FXML
    private Label humidityLabelId;
    @FXML
    private Label windLabelId;
    @FXML
    private Label timeLabelId;
    @FXML
    private Label queryResultLabelId;
    @FXML
    private Label tipsLabelId;
    @FXML
    private TextField cityInputTextId;
    @FXML
    private ListView<String> recommendListViewId;

    @Override
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        localSide = new ArrayList<>();
        cloudSide = new ArrayList<>();
        cityList = new ArrayList<>();
        isFirstQuery = true;
        cityWeatherManager = new CityWeatherManager();
        cityManager = new CityManager();
        accountManager = new AccountManager();
        historyManager = new QueryHistoryManager();
        temp = new SimpleStringProperty();
        weather = new SimpleStringProperty();
        humidity = new SimpleStringProperty();
        wind = new SimpleStringProperty();
        time = new SimpleStringProperty();
        tips = new SimpleStringProperty();
        queryResult = new SimpleBooleanProperty();
        queryStatusStr = new SimpleStringProperty();
        cityName = new SimpleStringProperty();
        tempLabelId.textProperty().bind(temp);
        weatherLabelId.textProperty().bind(weather);
        humidityLabelId.textProperty().bind(humidity);
        windLabelId.textProperty().bind(wind);
        timeLabelId.textProperty().bind(time);
        tipsLabelId.textProperty().bind(tips);
        queryResultContainerId.visibleProperty().bind(queryResult);
        queryResult.addListener((observableValue, oldValue, newValue) -> {
            queryStatusStr.set(newValue ? "成功" : "失败");
            queryResultLabelId.setStyle("-fx-text-fill: " + (newValue ? "green" : "red"));
        });
        queryResultLabelId.textProperty().bind(queryStatusStr);
        recommendCityNameList = FXCollections.observableArrayList();
        recommendListViewId.setItems(recommendCityNameList);
        recommendListViewId.setStyle("-fx-font-size: 18px");
        recommendListViewId.setVisible(false);
        recommendListViewId.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    if (newValue == null) {
                        return;
                    }
                    System.out.println(newValue);
                    for (final var i : recommendCityList) {
                        if (i.getChinese().equals(newValue)) {
                            cityInputTextId.setText(i.toString());
                            break;
                        }
                    }
                    doQuery();
                });
    }

    @FXML
    private void doLogout() {
        receiveAccount();
        accountManager.execute(new AccountAction(AccountActionType.logout, account));
        stageManager.convertTo(Main.primarySceneManagerName);
    }

    @FXML
    private void doDispose() {
        receiveAccount();
        accountManager.execute(new AccountAction(AccountActionType.dispose, account));
        stageManager.convertTo(Main.primarySceneManagerName);
    }

    @FXML
    private void showHistory() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("History List");
        ListView<String> historyListView = new ListView<>();
        final List<String> content = new ArrayList<>();
        content.addAll(localSide.parallelStream().map(QueryHistoryEntity::toString).collect(Collectors.toList()));
        content.addAll(cloudSide.parallelStream().map(QueryHistoryEntity::toString).collect(Collectors.toList()));
        historyListView.setItems(FXCollections.observableArrayList(content));
        historyListView.setFixedCellSize(50);
        historyListView.setStyle("-fx-font-size: 18px");
        historyListView.setPrefWidth(1000);
        alert.setGraphic(historyListView);
        alert.setHeaderText("History");
        alert.showAndWait();
    }

    @FXML
    private void doPull() {
        receiveAccount();
        cloudSide.clear();
        Alert alert = new Alert(Alert.AlertType.NONE);
        final var action = new HistoryAction(HistoryActionType.fetch, account);
        if (historyManager.execute(action)) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("Pull succeed!");
            cloudSide = action.getHistoryList();
        } else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("Pull fails!");
        }
        alert.showAndWait();
    }

    @FXML
    private void doPush() {
        receiveAccount();
        Alert alert = new Alert(Alert.AlertType.NONE);
        if (localSide.isEmpty()) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("No need to push!");
        } else {
            if (historyManager.execute(new HistoryAction(HistoryActionType.push, account, localSide))) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("Push succeed!");
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("Push fails!");
            }
        }
        localSide.clear();
        alert.showAndWait();
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About this Application");
        alert.setContentText(new AboutMessage("Y24", "1.0.0", "https://github.com/y24/weather_report", "Linux JavaSE 14/Intellij IDEA Ultimate/OpenJFX 14", "light-weight Weather report powered by Moji.").toString());
        alert.showAndWait();
    }

    @FXML
    private void doQuery() {
        recommendListViewId.setVisible(false);
        final CityEntity cityEntity = getCity(cityInputTextId.getText());
        final CityWeatherAction action = new CityWeatherAction(CityWeatherActionType.fetch, cityEntity);
        if (cityEntity != CityEntity.nullCity && cityWeatherManager.execute(action)) {
            final WeatherEntity weatherEntity = action.getWeather();
            temp.set(weatherEntity.getTemperature() + "℃");
            weather.set(weatherEntity.getWeather());
            humidity.set(weatherEntity.getHumidity().substring(2));
            wind.set(weatherEntity.getWindInfo());
            time.set(weatherEntity.getLastUpdateTime());
            tips.set(weatherEntity.getTips());
            localSide.add(new QueryHistoryEntity(cityEntity, new Date(), weatherEntity));
            queryResult.setValue(true);
            for (final var i : recommendCityList) {
                if (i.equals(cityEntity)) {
                    cityName.set(i.getChinese());
                    break;
                }
            }
            return;
        }
        queryResult.setValue(false);
        if (isFirstQuery) {
            isFirstQuery = false;
            queryResult.setValue(true);
            queryResult.setValue(false);
        }
    }


    @FXML
    private void doRecommend() {
        final String current = cityInputTextId.getText().toLowerCase();
        recommendListViewId.setVisible(false);
        recommendCityNameList.clear();
        recommendCityList = cityList.parallelStream().filter(cityEntity -> shouldRecommend(cityEntity, current)).collect(Collectors.toList());
        recommendCityNameList.addAll(recommendCityList.stream().map(CityEntity::getChinese).collect(Collectors.toList()));
        if (!recommendCityNameList.isEmpty())
            recommendListViewId.setVisible(true);
    }

    private void receiveAccount() {
        if (account == null) {
            account = (AccountEntity) ((Deliverer) stageManager.receiveBroadcastMessage().toArray()[0]).getMessage();
        }
    }


    @FXML
    private void showAllPlaces() {
        final var action = new CityAction(CityActionType.fetch);
        cityManager.execute(action);
        cityList.clear();
        cityList.addAll(action.getCityList());
        cityNameLabelId.visibleProperty().bind(queryResult);
        cityNameLabelId.textProperty().bind(cityName);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Places");
        ListView<String> cityListView = new ListView<>();
        final List<String> content = cityList.parallelStream().map(cityEntity -> cityEntity.getChinese() + "   ->   " + cityEntity.toString()).collect(Collectors.toList());
        cityListView.setItems(FXCollections.observableArrayList(content));
        cityListView.setFixedCellSize(50);
        cityListView.setStyle("-fx-font-size: 18px");
        cityListView.setPrefWidth(1000);
        alert.setGraphic(cityListView);
        alert.setHeaderText("Places");
        alert.showAndWait();

    }
}
