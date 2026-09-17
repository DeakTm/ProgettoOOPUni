package controller;

import boundary.ui.FormAttivitaView;
import controller.AttivitaController;
import entity.enums.TipoAttivita;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class FormAttivitaController {

    private Stage dialogStage;
    private FormAttivitaView view;
    private AttivitaController control;
    private int idProgettoCorrente;

    public FormAttivitaController(Stage ownerStage, int idProgetto) {
        this.idProgettoCorrente = idProgetto;
        this.control = new AttivitaController();
        this.view = new FormAttivitaView();
        this.dialogStage = new Stage();

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Assegna Nuova Attività");

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
        
        view.getBtnAnnulla().setOnAction(event -> dialogStage.close());


        view.getBtnSalva().setOnAction(event -> {
            String descrizione = view.getTxtDescrizione().getText();
            TipoAttivita tipo = view.getCmbTipo().getValue();
            
            LocalDate scadenza = null;
            try {
                scadenza = view.getDataScadenza().getValue();
                if (scadenza == null && view.getDataScadenza().getEditor().getText() != null && !view.getDataScadenza().getEditor().getText().isEmpty()) {
                    view.getDataScadenza().commitValue();
                    scadenza = view.getDataScadenza().getValue();
                }
            } catch (Exception e) {
                System.out.println("Errore UI: Formato data non valido!");
                return;
            }

            String matricola = view.getCmbStudenteAssegnato().getValue();

            if (descrizione == null || descrizione.trim().isEmpty() || tipo == null || matricola == null) {
                System.out.println("Errore UI: Descrizione, Tipo e Studente sono obbligatori!");
                return; 
            }

            boolean successo = control.creaAttivita(descrizione, tipo, scadenza, idProgettoCorrente, matricola);

            if (successo) {
                System.out.println("Attività creata e salvata con successo nel Database!");
                dialogStage.close();
            } else {
                System.out.println("Salvataggio fallito. I Trigger del database hanno bloccato l'operazione.");
            }
        }); 
    } 


    public void mostra() {
        dialogStage.showAndWait();
    }
}