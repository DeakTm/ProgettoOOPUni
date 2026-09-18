package controller;

import boundary.ui.MainView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    private MainView view;
    private Stage stage;
    private String matricolaLoggata;
    private ProgettoController progettoController; 

    public MainController(Stage stage, String matricolaLoggata) {
        this.stage = stage;
        this.matricolaLoggata = matricolaLoggata;

        this.view = new MainView();
        
        // Impostiamo subito la matricola e l'avatar nella nuova Dashboard
        this.view.setUtenteLoggato(matricolaLoggata);

        Scene scene = new Scene(view.getRoot(), 1200, 800);

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

        //  Bottone centrale nella card della Dashboard ("Visualizza i tuoi Progetti")
        view.getBtnVaiAiProgetti().setOnAction(event -> {
            rimuoviAttivi();
            view.getItemProgetti().getStyleClass().add("is-active");
            mostraProgetti();
        });

        //  Navigazione laterale
        view.getItemDashboard().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemDashboard().getStyleClass().add("is-active");
            view.mostraTabPane();
            view.getTabPane().getSelectionModel().select(0);
        });

        view.getItemProgetti().setOnMouseClicked(event -> {   
            rimuoviAttivi();
            view.getItemProgetti().getStyleClass().add("is-active");
            mostraProgetti();
        });

        view.getItemAttivita().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemAttivita().getStyleClass().add("is-active");
            new AttivitaController(view, matricolaLoggata);
        });

     

        // Notifiche (bottone topbar)
        view.getBtnNotif().setOnAction(event -> {
            System.out.println("Apertura pannello notifiche per: " + matricolaLoggata);
        });
    }

    private void rimuoviAttivi() {
        view.getItemDashboard().getStyleClass().remove("is-active");
        view.getItemProgetti().getStyleClass().remove("is-active");
        view.getItemAttivita().getStyleClass().remove("is-active");
    }

    private void mostraProgetti() {
        this.progettoController = new ProgettoController(view, matricolaLoggata);
    }
}