package controller;

import boundary.ui.LoginView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController {

    private LoginView view;
    private Stage stage;

    public LoginController(Stage stage) {
        this.stage = stage;
        this.view = new LoginView();
        Scene scene = new Scene(view.getRoot(), 800, 600);

        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato.");
        }

        this.stage.setScene(scene);
        inizializzaEventi();
    }

    private void inizializzaEventi() {
        
        // EVENTO: Click su Accedi
        view.getBtnAccedi().setOnAction(event -> {
            String matricola = view.getTxtMatricola().getText();
            String password = view.getTxtPassword().getText();
            
            // Per ora fingiamo che il login sia sempre corretto se inserisci qualcosa
            if (!matricola.isEmpty()) {
                System.out.println("Login effettuato con successo. Matricola: " + matricola);
                
                // CAMBIO SCENA! Sovrascrivo la finestra attuale con il Main
                new MainController(stage, matricola);
            } else {
                System.out.println("Inserisci la matricola per entrare!");
            }
        });
    }
}