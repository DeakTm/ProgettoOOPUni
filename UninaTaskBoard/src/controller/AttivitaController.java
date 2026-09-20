package controller;

import boundary.ui.AttivitaView;
import boundary.ui.MainView;
import boundary.persistence.dao.AttivitaDAO;
import boundary.persistence.dao.ProgettoDAO;
import boundary.persistence.jdbc.AttivitaBoundaryJdbc;
import boundary.persistence.jdbc.ProgettoBoundaryJdbc;
import entity.Attivita;
import entity.Progetto;
import entity.enums.TipoAttivita;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AttivitaController {


    private AttivitaDAO attivitaDAO;
    private ProgettoDAO progettoDAO;


    private AttivitaView view;
    private MainView mainView;
    private String matricolaUtente;
    private List<Attivita> listaAttivita;


    public AttivitaController() {
        this.attivitaDAO = new AttivitaBoundaryJdbc();
        this.progettoDAO = new ProgettoBoundaryJdbc();
    }

    public AttivitaController(MainView mainView, String matricolaUtente) {
        this.mainView = mainView;
        this.matricolaUtente = matricolaUtente;
        this.attivitaDAO = new AttivitaBoundaryJdbc();
        this.progettoDAO = new ProgettoBoundaryJdbc();
        this.view = new AttivitaView();

        configuraListener();
        caricaAttivita();

        mainView.mostraAttivitaView(view);
    }


    public List<String> getStudentiProgetto(int idProgetto) {
        return progettoDAO.getStudentiByProgetto(idProgetto);
    }

    public boolean creaAttivita(String descrizione, TipoAttivita tipo, LocalDate scadenza, int idProgetto, String matricola) {

        Attivita nuovaAttivita = new Attivita();
        nuovaAttivita.setDescrizione(descrizione);
        nuovaAttivita.setTipo(tipo);
        nuovaAttivita.setDataScadenza(scadenza);

        Progetto progettoRiferimento = new Progetto(idProgetto, null, null, null);
        nuovaAttivita.setProgetto(progettoRiferimento);

        return attivitaDAO.inserisciAttivitaConAssegnazione(nuovaAttivita, matricola);
    }



    private void configuraListener() {
        view.getSearchField().textProperty().addListener((obs, oldVal, newVal) -> filtraAttivita());
        view.getFiltroStato().setOnAction(e -> filtraAttivita());
        view.getFiltroTipo().setOnAction(e -> filtraAttivita());

        view.setOnAttivitaDoppioClick(this::apriDettaglioAttivita);
    }


    private void caricaAttivita() {
        listaAttivita = attivitaDAO.getAttivitaStudente(matricolaUtente);
        view.mostraAttivita(listaAttivita);
    }


    private void filtraAttivita() {
        if (listaAttivita == null) return;

        String query = view.getSearchField().getText();
        String stato = view.getFiltroStato().getValue();
        String tipo = view.getFiltroTipo().getValue();

        List<Attivita> filtrati = listaAttivita.stream()
            .filter(a -> {
                if (query != null && !query.trim().isEmpty()) {
                    String q = query.toLowerCase().trim();
                    if (!a.getDescrizione().toLowerCase().contains(q)) return false;
                }
             
                if (stato != null && !"Tutti".equals(stato)) {
                    if (!a.getStato().toString().equals(stato)) return false;
                }
              
                if (tipo != null && !"Tutti".equals(tipo)) {
                    if (!a.getTipo().toString().equals(tipo)) return false;
                }
                return true;
            })
            .collect(Collectors.toList());

        view.mostraAttivita(filtrati);
    }

 
    private void apriDettaglioAttivita(Attivita attivita) {
        Stage owner = (Stage) view.getRoot().getScene().getWindow();
        new DettaglioAttivitaController(owner, attivita, matricolaUtente);
        refresh();
        
    }

 void refresh() {
        caricaAttivita();
    }


    public AttivitaView getView() {
        return view;
    }
}