package controller;

import boundary.persistence.dao.AttivitaDAO;
import boundary.persistence.dao.ProgettoDAO;
import boundary.persistence.jdbc.AttivitaBoundaryJdbc;
import boundary.persistence.jdbc.ProgettoBoundaryJdbc;
import entity.Attivita;
import entity.Progetto;
import entity.enums.TipoAttivita;

import java.time.LocalDate;
import java.util.List;

public class AttivitaController {

    private AttivitaDAO attivitaDAO;
    private ProgettoDAO progettoDAO;

    public AttivitaController() {
        this.attivitaDAO = new AttivitaBoundaryJdbc();
        this.progettoDAO = new ProgettoBoundaryJdbc();
    }

    public List<String> getStudentiProgetto(int idProgetto) {
        return progettoDAO.getStudentiByProgetto(idProgetto);
    }

    public boolean creaAttivita(String descrizione, TipoAttivita tipo, LocalDate scadenza, int idProgetto, String matricola) {
        
        Attivita nuovaAttivita = new Attivita();
        nuovaAttivita.setDescrizione(descrizione);
        nuovaAttivita.setTipo(tipo);
        nuovaAttivita.setDataScadenza(scadenza);

        Progetto progettoRiferimento = new Progetto(idProgetto, null, null);
        nuovaAttivita.setProgetto(progettoRiferimento);

        return attivitaDAO.inserisciAttivitaConAssegnazione(nuovaAttivita, matricola);
    }
}