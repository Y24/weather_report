package cn.org.y24.ui.controller;

import cn.org.y24.Main;
import cn.org.y24.actions.AccountAction;
import cn.org.y24.actions.CityAction;
import cn.org.y24.actions.CityWeatherAction;
import cn.org.y24.actions.HistoryAction;
import cn.org.y24.entity.*;
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
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.*;
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

    private ObservableList<Weather> query7DaysResult;

    private StringProperty temp, weather, humidity, wind, tips, time, queryStatusStr, cityName;
    private BooleanProperty queryResult, do7DaysQuery, recommendListViewVisibility;
    private ObservableList<String> recommendCityNameList;
    @FXML
    private MenuBar menuBarId;
    @FXML
    private CheckBox choiceBoxId;
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
    @FXML
    private TableView<Weather> tableViewId;
    @FXML
    private TableColumn<Weather, String> date7DaysId;
    @FXML
    private TableColumn<Weather, String> weather7DaysId;
    @FXML
    private TableColumn<Weather, String> temp7DaysId;
    @FXML
    private TableColumn<Weather, String> updateTime7DaysId;


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
        do7DaysQuery = new SimpleBooleanProperty();
        recommendListViewVisibility = new SimpleBooleanProperty();
        queryStatusStr = new SimpleStringProperty();
        cityName = new SimpleStringProperty();
        tempLabelId.textProperty().bind(temp);
        weatherLabelId.textProperty().bind(weather);
        humidityLabelId.textProperty().bind(humidity);
        windLabelId.textProperty().bind(wind);
        timeLabelId.textProperty().bind(time);
        tipsLabelId.textProperty().bind(tips);
        recommendListViewId.visibleProperty().bind(recommendListViewVisibility);
        queryResult.addListener((observableValue, oldValue, newValue) -> {
            queryStatusStr.set(newValue ? "成功" : "失败");
            queryResultLabelId.setStyle("-fx-text-fill: " + (newValue ? "green" : "red"));
            if (do7DaysQuery.getValue()) {
                queryResultContainerId.setVisible(false);
                tableViewId.setVisible(newValue);
            } else {
                tableViewId.setVisible(false);
                queryResultContainerId.setVisible(newValue);
            }
        });
        do7DaysQuery.addListener((observableValue, oldValue, newValue) -> {
            if (newValue) {
                queryResultContainerId.setVisible(false);
            } else {
                tableViewId.setVisible(false);
            }
        });
        tableViewId.setStyle("-fx-font-size: 24px");
        final List<Weather> list = new ArrayList<>(7);
        for (int i = 0; i < 7; i++) {
            list.add(new Weather());
        }
        query7DaysResult = FXCollections.observableArrayList(list);
        tableViewId.setItems(query7DaysResult);
        date7DaysId.setCellValueFactory(p -> p.getValue().dateProperty());
        weather7DaysId.setCellValueFactory(p -> p.getValue().weatherProperty());
        temp7DaysId.setCellValueFactory(p -> p.getValue().temperatureProperty());
        updateTime7DaysId.setCellValueFactory(p -> p.getValue().lastUpdateTimeProperty());
        queryResultLabelId.textProperty().bind(queryStatusStr);
        menuBarId.setStyle("-fx-font-size: 18px");
        tableViewId.setStyle("-fx-font-size: 18px");

        choiceBoxId.selectedProperty().bindBidirectional(do7DaysQuery);
        recommendCityNameList = FXCollections.observableArrayList();
        recommendListViewId.setItems(recommendCityNameList);
        recommendListViewId.setStyle("-fx-font-size: 18px");
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
        alert.setTitle("历史记录");
        ListView<String> historyListView = new ListView<>();
        final List<String> content = new ArrayList<>();
        content.addAll(localSide.parallelStream().map(QueryHistoryEntity::toString).collect(Collectors.toList()));
        content.addAll(cloudSide.parallelStream().map(QueryHistoryEntity::toString).collect(Collectors.toList()));
        historyListView.setItems(FXCollections.observableArrayList(content));
        historyListView.setFixedCellSize(50);
        historyListView.setStyle("-fx-font-size: 18px");
        historyListView.setPrefWidth(1000);
        alert.setGraphic(historyListView);
        alert.setHeaderText("查询历史");
        alert.showAndWait();
    }

    @FXML
    private void doPull() {
        receiveAccount();
        cloudSide.clear();
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("结果");
        alert.setTitle("结果");
        final var action = new HistoryAction(HistoryActionType.fetch, account);
        if (historyManager.execute(action)) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("成功从云端获取数据!");
            cloudSide = action.getHistoryList();
        } else {
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setContentText("拉取失败!");
        }
        alert.showAndWait();
    }

    @FXML
    private void doPush() {
        receiveAccount();
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setHeaderText("结果");
        alert.setTitle("结果");
        if (localSide.isEmpty()) {
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setContentText("无需同步!");
        } else {
            if (historyManager.execute(new HistoryAction(HistoryActionType.push, account, localSide))) {
                alert.setAlertType(Alert.AlertType.INFORMATION);
                alert.setContentText("成功同步数据到云端!");
            } else {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setContentText("同步失败!");
            }
        }
        localSide.clear();
        alert.showAndWait();
    }

    @FXML
    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于");
        alert.setHeaderText("相关信息");
        alert.setContentText(new AboutMessage("Y24", "1.0.0", "https://github.com/y24/weather_report", "Linux JavaSE 14/Intellij IDEA Ultimate/OpenJFX 14", "小偷天气预报").toString());
        alert.showAndWait();
    }

    @FXML
    private void doQuery() {
        recommendListViewVisibility.setValue(false);
        final CityEntity cityEntity = getCity(cityInputTextId.getText());
        if (do7DaysQuery.getValue()) {
            final CityWeatherAction action = new CityWeatherAction(CityWeatherActionType.fetch7days, cityEntity);
            if (cityEntity != CityEntity.nullCity && cityWeatherManager.execute(action)) {
                final List<WeatherEntity> weatherEntityList = action.getWeather();
                for (int i = 0; i < 7; i++) {
                    query7DaysResult.get(i).setDate(weatherEntityList.get(i).getTips());
                    query7DaysResult.get(i).setTemperature(weatherEntityList.get(i).getTemperature());
                    query7DaysResult.get(i).setLastUpdateTime(weatherEntityList.get(i).getLastUpdateTime());
                    query7DaysResult.get(i).setWeather(weatherEntityList.get(i).getWeather());
                }
                localSide.add(new QueryHistoryEntity(cityEntity, new Date(), weatherEntityList));
                queryResult.setValue(false);
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
        } else {
            final CityWeatherAction action = new CityWeatherAction(CityWeatherActionType.fetchToday, cityEntity);
            if (cityEntity != CityEntity.nullCity && cityWeatherManager.execute(action)) {
                final WeatherEntity weatherEntity = action.getWeather().get(0);
                temp.set(weatherEntity.getTemperature() + "℃");
                weather.set(weatherEntity.getWeather());
                humidity.set(weatherEntity.getHumidity().substring(2));
                wind.set(weatherEntity.getWindInfo());
                time.set(weatherEntity.getLastUpdateTime());
                tips.set(weatherEntity.getTips());
                localSide.add(new QueryHistoryEntity(cityEntity, new Date(), Collections.singletonList(weatherEntity)
                ));
                queryResult.setValue(false);
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
        }
        if (isFirstQuery) {
            isFirstQuery = false;
            queryResult.setValue(true);
            queryResult.setValue(false);
        }
    }


    @FXML
    private void doRecommend() {
        final String current = cityInputTextId.getText().toLowerCase();
        recommendListViewVisibility.setValue(false);
        recommendCityNameList.clear();
        recommendCityList = cityList.parallelStream().filter(cityEntity -> shouldRecommend(cityEntity, current)).collect(Collectors.toList());
        recommendCityNameList.addAll(recommendCityList.stream().map(CityEntity::getChinese).collect(Collectors.toList()));
        if (!recommendCityNameList.isEmpty())
            recommendListViewVisibility.setValue(true);
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
        alert.setTitle("所有地点");
        ListView<String> cityListView = new ListView<>();
        final List<String> content = cityList.parallelStream().map(cityEntity -> cityEntity.getChinese() + "   ->   " + cityEntity.toString()).collect(Collectors.toList());
        cityListView.setItems(FXCollections.observableArrayList(content));
        cityListView.setFixedCellSize(50);
        cityListView.setStyle("-fx-font-size: 18px");
        cityListView.setPrefWidth(1000);
        alert.setGraphic(cityListView);
        alert.setHeaderText("清单");
        alert.showAndWait();

    }
}
