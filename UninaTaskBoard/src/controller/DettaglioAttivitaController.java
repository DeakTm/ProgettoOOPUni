package controller;

import boundary.ui.DettagliAttivitaView;
import boundary.persistence.dao.FileCodiceDAO;
import boundary.persistence.dao.RevisioneDAO;
import boundary.persistence.jdbc.FileCodiceBoundaryJdbc;
import boundary.persistence.jdbc.RevisioneBoundaryJdbc;
import entity.Attivita;
import entity.FileCodice;
import entity.Revisione;
import entity.enums.TipoLinguaggio;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DettaglioAttivitaController {


    private DettagliAttivitaView view;
    private FileCodiceDAO fileDAO;
    private RevisioneDAO revisioneDAO;
    private Attivita attivita;
    private String matricolaUtente;
    private FileCodice fileCorrente;


    public DettaglioAttivitaController(Stage owner, Attivita attivita, String matricolaUtente) {
        this.attivita = attivita;
        this.matricolaUtente = matricolaUtente;
        this.view = new DettagliAttivitaView(owner);
        this.fileDAO = new FileCodiceBoundaryJdbc();
        this.revisioneDAO = new RevisioneBoundaryJdbc();

        view.setAttivita(attivita);
        configuraListener();   
        caricaFile();          

        view.mostra();
    }


    private void configuraListener() {
        view.getBtnImportaFile().setOnAction(e -> importaFile());

        view.getListaFile().getSelectionModel().selectedItemProperty().addListener(
            (obs, oldF, newF) -> {
                this.fileCorrente = newF;
                view.setContenutoEditor(newF);
                if (newF != null) {
                    List<Revisione> revs = revisioneDAO.getRevisioniFile(newF.getId());
                    view.setRevisioni(newF, revs);
                }
            }
        );

        view.getBtnSalvaFile().setOnAction(e -> salvaModifiche());

        view.getBtnAggiungiRevisione().setOnAction(e -> aggiungiRevisione());
        view.getBtnEliminaFile().setOnAction(e -> eliminaFile());
    }

 
    private void caricaFile() {
        List<FileCodice> files = fileDAO.leggiFilePerAttivita(attivita.getId());
        view.setFile(files);

        if (!files.isEmpty()) {
            view.getListaFile().getSelectionModel().select(0);
            this.fileCorrente = files.get(0);
            view.setContenutoEditor(fileCorrente);
            List<Revisione> revs = revisioneDAO.getRevisioniFile(fileCorrente.getId());
            view.setRevisioni(fileCorrente, revs);
        } else {
            this.fileCorrente = null;
            view.setContenutoEditor(null);
        }
    }


    private void importaFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona un file di codice");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Codice",
                "*.java", "*.py", "*.c", "*.cpp", "*.html", "*.css", "*.sql"),
            new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );

        File file = fileChooser.showOpenDialog(view.getStage());
        if (file == null) return;

        try {
           
            String contenuto = new String(Files.readAllBytes(file.toPath()));

           
            TipoLinguaggio linguaggio = rilevaLinguaggio(file.getName());

       
            FileCodice nuovo = new FileCodice();
            nuovo.setNome_file(file.getName());
            nuovo.setContenuto(contenuto);
            nuovo.setLinguaggio(linguaggio);
            nuovo.setDataUltimaModifica(LocalDateTime.now());

        
            int idGenerato = fileDAO.creaFile(nuovo, attivita.getId());

            if (idGenerato > 0) {
                caricaFile();
                mostraInfo("File importato con successo!");
            } else {
                mostraErrore("Errore nel salvataggio del file");
            }

        } catch (IOException e) {
            mostraErrore("Errore lettura file: " + e.getMessage());
        }
    }


    private void salvaModifiche() {
        if (fileCorrente == null) {
            mostraErrore("Seleziona prima un file");
            return;
        }

        String nuovoContenuto = view.getTxtContenuto().getText();
        fileCorrente.setContenuto(nuovoContenuto);
        fileCorrente.setDataUltimaModifica(LocalDateTime.now());

        fileDAO.aggiornaFile(fileCorrente);

     
        caricaFile();
        mostraInfo("Modifiche salvate!");
    }


    private void aggiungiRevisione() {
        if (fileCorrente == null) {
            mostraErrore("Seleziona prima un file");
            return;
        }

        // 1. Chiedi la nota
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuova Revisione");
        dialog.setHeaderText("Descrivi l'intervento effettuato su \"" + fileCorrente.getNome_file() + "\"");
        dialog.setContentText("Nota:");

        Optional<String> nota = dialog.showAndWait();
        if (nota.isEmpty() || nota.get().trim().isEmpty()) {
            mostraErrore("Devi inserire una nota");
            return;
        }

       
        Revisione rev = new Revisione();
        rev.setData(LocalDate.now());   

        int idRev = revisioneDAO.creaRevisione(rev, fileCorrente.getId(), matricolaUtente);

        if (idRev > 0) {
            
            List<Revisione> revs = revisioneDAO.getRevisioniFile(fileCorrente.getId());
            view.setRevisioni(fileCorrente, revs);
            mostraInfo("Revisione aggiunta con successo!");
        } else {
            mostraErrore("Errore nel salvataggio della revisione");
        }
    }

    private void eliminaFile() {
        if (fileCorrente == null) {
            mostraErrore("Seleziona prima un file");
            return;
        }

        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma eliminazione");
        conferma.setHeaderText(null);
        conferma.setContentText("Eliminare il file \"" + fileCorrente.getNome_file() + "\"?\n" +
                                "Verranno eliminate anche tutte le sue revisioni.");

        Optional<ButtonType> risposta = conferma.showAndWait();
        if (risposta.isPresent() && risposta.get() == ButtonType.OK) {
            fileDAO.eliminaFile(fileCorrente.getId());
            caricaFile();
        }
    }


    private TipoLinguaggio rilevaLinguaggio(String nomeFile) {
        String lower = nomeFile.toLowerCase();
        if (lower.endsWith(".java")) return TipoLinguaggio.Java;
        if (lower.endsWith(".py")) return TipoLinguaggio.Python;
        if (lower.endsWith(".c")) return TipoLinguaggio.C;
        if (lower.endsWith(".cpp")) return TipoLinguaggio.Cpp;
        if (lower.endsWith(".html")) return TipoLinguaggio.HTML;
        if (lower.endsWith(".css")) return TipoLinguaggio.CSS;
        if (lower.endsWith(".sql")) return TipoLinguaggio.SQL;
        return TipoLinguaggio.Altro;
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    private void mostraInfo(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}