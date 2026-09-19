package controller;

import boundary.ui.GestioneMembriView;
import boundary.persistence.dao.AttivitaDAO;
import boundary.persistence.dao.ProgettoDAO;
import boundary.persistence.jdbc.AttivitaBoundaryJdbc;
import boundary.persistence.jdbc.ProgettoBoundaryJdbc;
import entity.Attivita;

import java.util.List;

public class GestioneMembriController {

    private GestioneMembriView view;
    private AttivitaDAO attivitaDAO;
    private ProgettoDAO progettoDAO;
    private Attivita attivita;
    private Runnable onSalvataggio;  

    public GestioneMembriController(Attivita attivita, Runnable onSalvataggio) {
        this.attivita = attivita;
        this.onSalvataggio = onSalvataggio;
        this.attivitaDAO = new AttivitaBoundaryJdbc();
        this.progettoDAO = new ProgettoBoundaryJdbc();

  
        javafx.stage.Stage owner = null;
        this.view = new GestioneMembriView(owner);

        configuraListener();
        caricaDati();

        view.mostra();
    }

    private void configuraListener() {
        view.getBtnSalva().setOnAction(e -> salvaModifiche());
        view.getBtnAnnulla().setOnAction(e -> view.getStage().close());
    }

    private void caricaDati() {
        int idProgetto = attivita.getProgetto() != null ? attivita.getProgetto().getId() : 0;

        List<String> tutti = progettoDAO.getStudentiByProgetto(idProgetto);
        List<String> assegnati = attivitaDAO.getAssegnatariAttivita(attivita.getId());

        view.caricaStudenti(tutti, assegnati);
    }

    private void salvaModifiche() {
        List<String> selezionati = view.getStudentiSelezionati();
        List<String> assegnati = attivitaDAO.getAssegnatariAttivita(attivita.getId());

        for (String matricola : selezionati) {
            if (!assegnati.contains(matricola)) {
                attivitaDAO.assegnaStudenteAttivita(matricola, attivita.getId());
            }
        }

        for (String matricola : assegnati) {
            if (!selezionati.contains(matricola)) {
                attivitaDAO.rimuoviStudenteAttivita(matricola, attivita.getId());
            }
        }

        if (onSalvataggio != null) onSalvataggio.run();
        view.getStage().close();
    }
}