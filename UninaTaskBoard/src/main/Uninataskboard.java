package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import util.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;

public class Uninataskboard extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println("Avvio interfaccia grafica e test database...");

        DatabaseManager dbManager = DatabaseManager.getDatabaseManager();
        Connection conn = dbManager.getConnection();

        String testoBottone = "JavaFX funziona!\n";
        
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    testoBottone += "Database connesso: " + conn.getMetaData().getDatabaseProductName();
                    System.out.println("Connessione al database stabilita con successo.");
                }
            } catch (SQLException e) {
                testoBottone += "Errore nello stato della connessione.";
                e.printStackTrace();
            }
        } else {
            testoBottone += "Connessione al DB fallita (controlla la console).";
        }

        //interfaccia JavaFX
        Button btn = new Button(testoBottone);
        btn.setStyle("-fx-font-size: 14px; -fx-text-alignment: center;");

        StackPane root = new StackPane(btn);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("UninaTaskBoard - Test di avvio");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        System.out.println("\nChiusura applicazione in corso...");
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