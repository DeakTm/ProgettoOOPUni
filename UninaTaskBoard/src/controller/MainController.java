package controller;

import boundary.ui.MainView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    private MainView view;
    private Stage stage;
    private String matricolaLoggata;

    public MainController(Stage stage, String matricolaLoggata) {
        this.stage = stage;
        this.matricolaLoggata = matricolaLoggata;
        
        this.view = new MainView();

        Scene scene = new Scene(view.getRoot(), 1200, 800);
        
        // RICARICA IL CSS (indispensabile per non perdere gli stili dopo il Login)
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato in /css/style.css");
        }
        
        this.stage.setScene(scene);
        
        inizializzaEventi();
    }

    private void inizializzaEventi() {
        
        // 1. Apertura del form Nuova Attività (Popup)
        view.getBtnNuovaAttivita().setOnAction(event -> {
            System.out.println("Apertura popup in corso...");
            // Passiamo 1 come ID progetto temporaneo per far funzionare la tendina degli studenti
            int idProgettoCorrente = 1; 

        });

        // 2. Navigazione laterale
        view.getItemDashboard().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemDashboard().getStyleClass().add("is-active");
            System.out.println("Navigazione: Dashboard");
        });

        view.getItemProgetti().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemProgetti().getStyleClass().add("is-active");
            System.out.println("Navigazione: Progetti");
        });

        view.getItemAttivita().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemAttivita().getStyleClass().add("is-active");
            System.out.println("Navigazione: Attività");
        });

        // 3. Notifiche
        view.getBtnNotif().setOnAction(event -> {
            System.out.println("Apertura pannello notifiche per: " + matricolaLoggata);
        });
    }

    private void rimuoviAttivi() {
        view.getItemDashboard().getStyleClass().remove("is-active");
        view.getItemProgetti().getStyleClass().remove("is-active");
        view.getItemAttivita().getStyleClass().remove("is-active");
        view.getItemReport().getStyleClass().remove("is-active");
        view.getItemNotifiche().getStyleClass().remove("is-active");
    }
}