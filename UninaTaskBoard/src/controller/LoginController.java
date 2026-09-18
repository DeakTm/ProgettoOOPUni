package controller;

import boundary.ui.LoginView;
import boundary.persistence.dao.StudenteDAO;
import boundary.persistence.jdbc.StudenteBoundaryJdbc;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LoginController {

    private LoginView view;
    private Stage stage;
    private StudenteDAO studenteDAO;

    public LoginController(Stage stage) {
        this.stage = stage;
        this.view = new LoginView();
        this.studenteDAO = new StudenteBoundaryJdbc();
        
        Scene scene = new Scene(view.getRoot(), 800, 600);

        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato.");
        }

        this.stage.setScene(scene);
        this.stage.setTitle("UninaTaskBoard - Login");
        this.stage.show();
        
        inizializzaEventi();
    }

    private void inizializzaEventi() {
        view.getBtnAccedi().setOnAction(event -> {
            String matricola = view.getTxtMatricola().getText();
            String password = view.getTxtPassword().getText();
            
            if (matricola == null || matricola.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                mostraErrore("Inserisci matricola e password per effettuare l'accesso.");
                return;
            }

            boolean credenzialiValide = studenteDAO.verificaLogin(matricola.trim(), password);

            if (credenzialiValide) {
                System.out.println("Login effettuato con successo. Matricola: " + matricola);
                new MainController(stage, matricola.trim());
            } else {
                mostraErrore("Matricola o password non corrette.");
            }
        });
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di Autenticazione");
        alert.setHeaderText("Accesso Negato");
        alert.setContentText(messaggio);

        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            alert.getDialogPane().getStylesheets().add(css);
        } catch (Exception ignored) {}

        alert.showAndWait();
    }
}