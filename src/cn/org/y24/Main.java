package cn.org.y24;

import cn.org.y24.ui.framework.SceneManager;
import cn.org.y24.ui.framework.StageManager;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static final String primarySceneManagerName = "primaryStage";
    public static final String primarySceneName = "loginScene";
    public static final String registerSceneName = "registerScene";
    public static final String mainSceneManagerName = "mainStage";
    private static final StageManager stageManager = new StageManager();


    @Override
    public void start(Stage primaryStage) {
        SceneManager primarySceneManager = new SceneManager(primaryStage);
        Parent rootParent = primarySceneManager.init("LoginView.fxml", stageManager);
        Scene rootScene = new Scene(rootParent, 900, 600);
        primarySceneManager.add(rootScene, primarySceneName);
        primarySceneManager.select(primarySceneName);
        stageManager.add(primarySceneManager, primarySceneManagerName);
        stageManager.showOnly(primarySceneManagerName);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
