package main;

import boundary.ui.MainView;
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

        // 1. Verifica connessione al DB
        DatabaseManager dbManager = DatabaseManager.getDatabaseManager();
        Connection conn = dbManager.getConnection();
        if (conn == null) {
            System.err.println("Attenzione: Connessione al database fallita.");
            return; // Blocco l'avvio se il DB non è disponibile
        }

        // 2. Esempio: matricola utente (in futuro verrà dal LoginController)
        String matricolaUtente = "M12345";  // TODO: sostituisci con login reale

        // 3. Creo il MainController che istanzia MainView e collega tutto
        mainController = new MainController(stage, matricolaUtente);

        // 4. Applico il CSS alla scena creata dal MainController
        Scene scene = stage.getScene();
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato in /css/style.css");
        }

        // 5. Titolo e show
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