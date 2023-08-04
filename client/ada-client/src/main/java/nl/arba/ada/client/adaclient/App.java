package nl.arba.ada.client.adaclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;
import nl.arba.ada.client.adaclient.utils.OAuthUtils;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        if (System.getProperty("user") != null && System.getProperty("password") != null) {
            if (OAuthUtils.getToken(System.getProperty("oauth.token"), System.getProperty("user"), System.getProperty("password"))) {
                FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("main.fxml"));
                fxmlLoader.setController(new AppController(stage));
                fxmlLoader.setResources(InternationalizationUtils.getResources());
                Scene scene = new Scene(fxmlLoader.load(), 293, 152);
                stage.setTitle(InternationalizationUtils.getAppTitle());
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.show();
            }
            else {
                login(stage);
            }
        }
        else {
            login(stage);
        }
    }

    private void login(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login.fxml"));
            fxmlLoader.setController(new LoginController(stage));
            Scene scene = new Scene(fxmlLoader.load(), 293, 152);
            stage.setTitle("Inloggen");
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception err) {
            err.printStackTrace();
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        for (String arg: args) {
            if (arg.contains("=")) {
                System.setProperty(arg.split(Pattern.quote("="))[0], arg.split(Pattern.quote("="))[1]);
            }
        }
        launch();
    }
}