package controller;

import boundary.ui.FormAttivitaView;
import control.GestioneAttivitaControl;
import entity.enums.TipoAttivita;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class FormAttivitaController {

    private Stage dialogStage;
    private FormAttivitaView view;
    private GestioneAttivitaControl control;
    private int idProgettoCorrente;

    // CORRETTO: Accetta due parametri come richiede il MainController
    public FormAttivitaController(Stage ownerStage, int idProgetto) {
        this.idProgettoCorrente = idProgetto;
        this.control = new GestioneAttivitaControl();
        this.view = new FormAttivitaView();
        this.dialogStage = new Stage();

        // Configuro la finestra come Modale
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Assegna Nuova Attività");

        // Carica dinamicamente gli studenti associati a questo progetto nella ComboBox
        List<String> studenti = control.getStudentiProgetto(idProgettoCorrente);
        if (studenti != null && view.getCmbStudenteAssegnato() != null) {
            view.getCmbStudenteAssegnato().getItems().addAll(studenti);
        }

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
            
            // Preleviamo la matricola scelta dalla ComboBox
            String matricola = view.getCmbStudenteAssegnato().getValue();

            // Validazione formale di base (UI)
            if (descrizione == null || descrizione.trim().isEmpty() || tipo == null || matricola == null) {
                System.out.println("Errore UI: Descrizione, Tipo e Studente sono obbligatori!");
                return; 
            }

            // Chiamata al Control che invoca le funzioni PL/pgSQL e subisce i Trigger del DB
            boolean successo = control.creaAttivita(descrizione, tipo, scadenza, idProgettoCorrente, matricola);

            if (successo) {
                System.out.println("Attività creata e salvata con successo nel Database!");
                dialogStage.close();
            } else {
                System.out.println("Salvataggio fallito. I Trigger del database hanno bloccato l'operazione.");
            }
        });
    }

    // Metodo pubblico per innescare l'apertura
    public void mostra() {
        dialogStage.showAndWait();
    }
}