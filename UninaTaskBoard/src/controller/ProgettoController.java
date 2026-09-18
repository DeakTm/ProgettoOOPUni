package controller;

import boundary.ui.DettaglioProgettoView;
import boundary.ui.FormProgettoView;
import boundary.ui.MainView;
import boundary.ui.ProgettiView;
import boundary.persistence.dao.ProgettoDAO;
import boundary.persistence.jdbc.ProgettoBoundaryJdbc;
import boundary.persistence.dao.StudenteDAO;
import boundary.persistence.jdbc.StudenteBoundaryJdbc;
import entity.Progetto;
import entity.Studente;
import entity.enums.StatoAvanzamento;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProgettoController {

    private ProgettiView view;
    private MainView mainView;
    private String matricolaUtente;
    private ProgettoDAO progettoDAO;
    private StudenteDAO studenteDAO;
    private List<Progetto> listaProgetti;

    public ProgettoController(MainView mainView, String matricolaUtente) {
        this.mainView = mainView;
        this.matricolaUtente = matricolaUtente;
        this.progettoDAO = new ProgettoBoundaryJdbc();
        this.studenteDAO = new StudenteBoundaryJdbc();
        this.view = new ProgettiView();

        configuraListener();
        caricaProgetti();

        mainView.mostraProgettiView(view);
    }

    private void configuraListener() {
        view.getBtnNuovoProgetto().setOnAction(e -> apriFormNuovoProgetto());
        view.setOnProgettoClick(this::apriDettaglioProgetto);
        view.getSearchField().textProperty().addListener((obs, oldVal, newVal) -> {
            cercaProgetti(newVal);
        });
        view.setOnEliminaClick(this::eliminaProgetto);
    }

    private void caricaProgetti() {
        listaProgetti = progettoDAO.getProgettiStudente(matricolaUtente);
        view.mostraProgetti(listaProgetti);
    }

    private void cercaProgetti(String query) {
        if (listaProgetti == null) return;
        if (query == null || query.trim().isEmpty()) {
            view.mostraProgetti(listaProgetti);
            return;
        }
        String q = query.toLowerCase().trim();
        List<Progetto> filtrati = listaProgetti.stream()
            .filter(p -> 
                String.valueOf(p.getId()).contains(q) ||
                (p.getStato() != null && p.getStato().toString().toLowerCase().contains(q))
            )
            .collect(Collectors.toList());
        view.mostraProgetti(filtrati);
    }
    
    private void apriFormNuovoProgetto() {
        FormProgettoView form = new FormProgettoView();
        LocalDate scadenza = form.showAndWait().orElse(null);

        if (scadenza == null) return;

        Progetto nuovo = new Progetto(StatoAvanzamento.Creato, scadenza);
        int id = progettoDAO.creaProgetto(nuovo);

        if (id > 0) {
            progettoDAO.assegnaStudenteProgetto(matricolaUtente, id);
            refresh();
        } else {
            mostraErrore("Errore nella creazione del progetto");
        }
    }

    private void eliminaProgetto(Progetto progetto) {
        Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
        conferma.setTitle("Conferma eliminazione");
        conferma.setHeaderText(null);
        conferma.setContentText("Sei sicuro di voler eliminare il progetto #" 
            + progetto.getId() + "?");

        Optional<ButtonType> risposta = conferma.showAndWait();
        if (risposta.isPresent() && risposta.get() == ButtonType.OK) {
            progettoDAO.eliminaProgetto(progetto.getId());
            refresh();
        }
    }

    private void apriDettaglioProgetto(Progetto progetto) {
        DettaglioProgettoView dettaglioView = new DettaglioProgettoView(progetto);
        
        dettaglioView.getBtnTornaIndietro().setOnAction(e -> {
            mainView.mostraProgettiView(view);
        });

       
        List<Studente> membriAttuali = progettoDAO.leggiStudentiPerProgetto(progetto.getId());
        dettaglioView.mostraMembri(membriAttuali);

        dettaglioView.getBtnAggiungiMembro().setOnAction(e -> {
            List<Studente> tuttiStudenti = studenteDAO.leggiTuttiStudenti(); 
            List<Studente> membriAttualiList = progettoDAO.leggiStudentiPerProgetto(progetto.getId());
            
            // FILTRO: Tiene solo gli studenti che NON sono già nel progetto
            List<Studente> studentiDisponibili = tuttiStudenti.stream()
                .filter(s -> membriAttualiList.stream().noneMatch(m -> m.getMatricola().equals(s.getMatricola())))
                .collect(Collectors.toList());

            if (studentiDisponibili.isEmpty()) {
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Info");
                info.setHeaderText(null);
                info.setContentText("Tutti gli studenti registrati fanno già parte di questo progetto.");
                info.showAndWait();
                return;
            }

            List<Studente> selezionati = dettaglioView.mostraDialogAggiungiMembri(studentiDisponibili);
            
            if (selezionati != null && !selezionati.isEmpty()) {
                for (Studente s : selezionati) {
                    progettoDAO.assegnaStudenteProgetto(s.getMatricola(), progetto.getId());
                }
                dettaglioView.mostraMembri(progettoDAO.leggiStudentiPerProgetto(progetto.getId()));
            }
        });

        dettaglioView.setOnRimuoviMembroClick(studente -> {
            Alert conferma = new Alert(Alert.AlertType.CONFIRMATION);
            conferma.setTitle("Rimuovi Studente");
            conferma.setHeaderText(null);
            conferma.setContentText("Vuoi rimuovere lo studente " + studente.getMatricola() + " dal progetto?");
            
            Optional<ButtonType> risposta = conferma.showAndWait();
            if (risposta.isPresent() && risposta.get() == ButtonType.OK) {

                progettoDAO.rimuoviStudenteProgetto(studente.getMatricola(), progetto.getId());
                dettaglioView.mostraMembri(progettoDAO.leggiStudentiPerProgetto(progetto.getId()));
            }
        });

        mainView.getRoot().setCenter(dettaglioView.getRoot());
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void refresh() {
        caricaProgetti();
    }

    public ProgettiView getView() {
        return view;
    }
}