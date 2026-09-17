package controller;

import boundary.ui.FormAttivitaView;
import entity.enums.TipoAttivita;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;

public class FormAttivitaController {

    private Stage dialogStage;
    private FormAttivitaView view;

    public FormAttivitaController(Stage ownerStage) {
        this.view = new FormAttivitaView();
        this.dialogStage = new Stage();

        // Configuro la finestra come Modale (blocca l'interazione con la MainView)
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Assegna Nuova Attività");

        Scene scene = new Scene(view.getRoot());
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato in /css/style.css");
        }

        dialogStage.setScene(scene);
        inizializzaEventi();
    }

    private void inizializzaEventi() {
        
        // EVENTO: Tasto Annulla
        view.getBtnAnnulla().setOnAction(event -> dialogStage.close());

        // EVENTO: Tasto Salva
        view.getBtnSalva().setOnAction(event -> {
            String descrizione = view.getTxtDescrizione().getText();
            TipoAttivita tipo = view.getCmbTipo().getValue();
            LocalDate scadenza = view.getDataScadenza().getValue();
            String matricola = view.getTxtMatricolaAssegnata().getText();

            // Validazione essenziale
            if (descrizione.trim().isEmpty() || tipo == null) {
                System.out.println("Errore: Descrizione e Tipo sono obbligatori!");
                return; // Ferma il salvataggio
            }

            System.out.println("--- PRONTO PER IL DATABASE ---");
            System.out.println("Descrizione: " + descrizione);
            System.out.println("Tipo: " + tipo);
            System.out.println("Scadenza: " + scadenza);
            System.out.println("Studente: " + matricola);

            // TODO: Qui chiameremo GestioneProgettiControl per passare i dati al DAO
            
            dialogStage.close();
        });
    }

    // Metodo pubblico per innescare l'apertura
    public void mostra() {
        dialogStage.showAndWait();
    }
}