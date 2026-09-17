package controller;

import boundary.ui.MainView;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    private MainView view;
    private Stage stage;
    private String matricolaLoggata;
    private ProgettoController progettoController;  // tienilo vivo

    public MainController(Stage stage, String matricolaLoggata) {
        this.stage = stage;
        this.matricolaLoggata = matricolaLoggata;

        this.view = new MainView();

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

        // 1. Apertura del form Nuova Attività
        view.getBtnNuovaAttivita().setOnAction(event -> {
            System.out.println("Apertura popup nuova attività...");
            int idProgettoCorrente = 1;
            // TODO: apri FormAttivitaView
        });

        // 2. Navigazione laterale
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
            view.mostraTabPane();
            view.getTabPane().getSelectionModel().select(0);
        });

        view.getItemReport().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemReport().getStyleClass().add("is-active");
            view.mostraTabPane();
            view.getTabPane().getSelectionModel().select(1);
        });

        view.getItemNotifiche().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemNotifiche().getStyleClass().add("is-active");
            System.out.println("Navigazione: Notifiche");
        });

        // 3. Notifiche (bottone topbar)
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

    private void mostraProgetti() {
        // Il ProgettoController crea da solo la ProgettiView
        // e la mostra al centro della MainView
        this.progettoController = new ProgettoController(view, matricolaLoggata);
    }
}