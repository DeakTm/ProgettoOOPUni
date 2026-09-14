package main;

import boundary.ui.MainViewBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Uninataskboard extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println("Avvio applicazione e test database...");

        DatabaseManager dbManager = DatabaseManager.getDatabaseManager();
        Connection conn = dbManager.getConnection();
        if (conn == null) {
            System.err.println("Attenzione: Connessione al database fallita.");
        }

        // Istanziazione della vista principale dal package boundary.ui
        MainViewBuilder viewBuilder = new MainViewBuilder();
        BorderPane root = viewBuilder.createMainView();

        Scene scene = new Scene(root, 1200, 750);
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato in /css/style.css");
        }

        stage.setTitle("UninaTaskBoard");
        stage.setScene(scene);
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