package nl.arba.ada.client.adaclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import nl.arba.ada.client.adaclient.utils.OAuthUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button btnOk;
    @FXML
    private TextField txtLogin;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnCancel;
    private Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                stage.close();
            }
        });

        btnOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                trylogin();
            }
        });

        txtLogin.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)
                    trylogin();
            }
        });

        txtPassword.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER)
                    trylogin();
            }
        });


    }

    private void trylogin() {
        if (OAuthUtils.getToken(System.getProperty("oauth.token"), txtLogin.getText(), txtPassword.getText())) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));
                fxmlLoader.setController(new AppController(stage));
                Scene scene = new Scene(fxmlLoader.load(), 293, 152);
                stage.setTitle("Ada 5.0");
                stage.setScene(scene);
                stage.setMaximized(true);
            }
            catch (Exception err) {}
        }
        else
            System.out.println("Login failed");
    }
}
