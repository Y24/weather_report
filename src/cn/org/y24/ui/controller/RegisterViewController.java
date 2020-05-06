package cn.org.y24.ui.controller;

import cn.org.y24.Main;
import cn.org.y24.actions.AccountAction;
import cn.org.y24.entity.AccountEntity;
import cn.org.y24.enums.AccountActionType;
import cn.org.y24.manager.AccountManager;
import cn.org.y24.ui.framework.BaseStageController;
import cn.org.y24.ui.framework.StageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegisterViewController extends BaseStageController {
    private final AccountManager accountManager = new AccountManager();
    @FXML
    Label successLabel;
    @FXML
    Label failureLabel;
    private StageManager stageManager;
    @FXML
    Button registerButtonID;
    @FXML
    TextField userTextFieldID;
    @FXML
    PasswordField passwordID;

    @FXML
    Hyperlink loginLink;


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
        registerButtonID.requestFocus();
    }

    public void registerAction() {
        if (userTextFieldID.getText().equals("") || passwordID.getText().equals("")) {
            failureLabel.setVisible(true);
            successLabel.setVisible(false);
            return;
        }
        final AccountEntity account = new AccountEntity(userTextFieldID.getText(), passwordID.getText());
        final boolean result = accountManager.execute(new AccountAction(AccountActionType.register,
                account));
        failureLabel.setVisible(!result);
        successLabel.setVisible(result);

    }

    public void switchToLoginView() {
        failureLabel.setVisible(false);
        successLabel.setVisible(false);
        stageManager.get(Main.primarySceneManagerName).select(Main.primarySceneName);
    }
}
