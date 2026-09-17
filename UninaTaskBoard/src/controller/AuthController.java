package controller;

import boundary.persistence.dao.StudenteDAO;
import boundary.persistence.jdbc.StudenteBoundaryJdbc;
import boundary.ui.LoginView;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class AuthController {

    private Stage stage;
    private LoginView view;

    public AuthController(Stage stage) {
        this.stage = stage;
        this.view = new LoginView();

        Scene scene = new Scene(view.getRoot(), 1200, 800);
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato in /css/style.css");
        }

        this.stage.setScene(scene);
        this.stage.setTitle("UninaTaskBoard - Login");
        this.stage.show();

        inizializzaEventi();
    }

    private void inizializzaEventi() {
        view.getBtnAccedi().setOnAction(event -> {
            String matricola = view.getTxtMatricola().getText().trim();
            String password = view.getTxtPassword().getText();

            if (matricola.isEmpty() || password.isEmpty()) {
                mostraAlert("Campi obbligatori", "Inserisci sia la matricola che la password.");
                return;
            }

            boolean credenzialiValide = chiamaFunzioneLoginDB(matricola, password);

            if (credenzialiValide) {
                System.out.println("Login effettuato con successo per lo studente: " + matricola);
                new MainController(stage, matricola);
            } else {
                mostraAlert("Accesso Negato", "Matricola o password non corrette.");
                view.getTxtPassword().clear();
            }
        });
    }

    private boolean chiamaFunzioneLoginDB(String matricola, String password) {
        StudenteDAO studenteDao = new StudenteBoundaryJdbc();
        return studenteDao.verificaLogin(matricola, password);
    }

    private void mostraAlert(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di Autenticazione");
        alert.setHeaderText(titolo);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}