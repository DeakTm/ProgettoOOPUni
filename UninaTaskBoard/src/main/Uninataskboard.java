package main;

import boundary.ui.MainView;
import controller.LoginController;
import controller.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.DatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Uninataskboard extends Application {

    private MainController mainController;

    @Override
    public void start(Stage stage) {
        System.out.println("Avvio applicazione e test database...");

        DatabaseManager dbManager = DatabaseManager.getDatabaseManager();
        Connection conn = dbManager.getConnection();
        if (conn == null) {
            System.err.println("Attenzione: Connessione al database fallita.");
            return; 
        }

        new LoginController(stage);

        stage.setTitle("UninaTaskBoard");
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("Chiusura applicazione in corso...");
        DatabaseManager dbManager = DatabaseManager.getDatabaseManager();
        Connection conn = dbManager.getConnection();

        if (conn != null) {
            try {
                conn.close();
                System.out.println("Connessione al database chiusa correttamente.");
            } catch (SQLException e) {
                System.err.println("Errore durante la chiusura della connessione.");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}