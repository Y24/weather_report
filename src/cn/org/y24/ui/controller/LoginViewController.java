package cn.org.y24.ui.controller;

import cn.org.y24.Main;
import cn.org.y24.actions.AccountAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.AccountActionType;
import cn.org.y24.manager.AccountManager;
import cn.org.y24.ui.framework.BaseStageController;
import cn.org.y24.ui.framework.SceneManager;
import cn.org.y24.ui.framework.StageManager;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class LoginViewController extends BaseStageController {
    private final AccountManager accountManager = new AccountManager();
    private StageManager stageManager;
    @FXML
    Hyperlink registerLink;
    @FXML
    TextField userTextFieldID;
    @FXML
    PasswordField passwordID;
    @FXML
    Button loginButtonID;
    @FXML
    Label messageLabel;

    @Override
    public void setStageManager(StageManager stageManager) {
        this.stageManager = stageManager;
    }

    @FXML
    void ActionForUser() {
        passwordID.requestFocus();
    }

    @FXML
    void ActionForPassword() {
        loginButtonID.requestFocus();
    }

    @FXML
    void switchToRegisterView() {
        messageLabel.setVisible(false);
        SceneManager sceneManager = stageManager.get(Main.primarySceneManagerName);
        Parent registerParent = sceneManager.init("RegisterView.fxml", stageManager);
        Scene registerScene = new Scene(registerParent, 1000, 600);
        sceneManager.delete(Main.registerSceneName);
        sceneManager.add(registerScene, Main.registerSceneName);
        sceneManager.select(Main.registerSceneName);
    }

    @FXML
    void loginAction() {
        if (userTextFieldID.getText().equals("")) {
            messageLabel.setVisible(true);
            return;
        }
        final AccountEntity account = new AccountEntity(userTextFieldID.getText(), passwordID.getText());
        if (accountManager.execute(new AccountAction(AccountActionType.login,
                account))) {
            SceneManager sceneManager = stageManager.get(Main.primarySceneManagerName);
            stageManager.sendBroadcastMessage(sceneManager.getCurrentScene().hashCode(), account);
            Parent mainParent = sceneManager.init("MainView.fxml", stageManager);
            Scene mainScene = new Scene(mainParent, 1000, 800);
            Stage mainStage = new Stage();
            mainStage.setScene(mainScene);
            SceneManager mainSceneManager = new SceneManager(mainStage);
            stageManager.delete(Main.mainSceneManagerName);
            stageManager.add(mainSceneManager, Main.mainSceneManagerName);
            mainSceneManager.add(mainScene, "main");
            mainSceneManager.select("main");
            mainSceneManager.getOwnerStage().setTitle("Weather Report");
            stageManager.convertTo(Main.mainSceneManagerName);
        } else messageLabel.setVisible(true);
    }

}
