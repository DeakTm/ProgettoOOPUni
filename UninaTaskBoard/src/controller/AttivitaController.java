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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class AttivitaController {

    // ============================================
    // CAMPI ESISTENTI
    // ============================================
    private AttivitaDAO attivitaDAO;
    private ProgettoDAO progettoDAO;

    // ============================================
    // CAMPI NUOVI (per la AttivitaView)
    // ============================================
    private AttivitaView view;
    private MainView mainView;
    private String matricolaUtente;
    private List<Attivita> listaAttivita;

    // ============================================
    // COSTRUTTORE ESISTENTE (invariato)
    // ============================================
    public AttivitaController() {
        this.attivitaDAO = new AttivitaBoundaryJdbc();
        this.progettoDAO = new ProgettoBoundaryJdbc();
    }

    // ============================================
    // NUOVO COSTRUTTORE (per la AttivitaView)
    // ============================================
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

    // ============================================
    // METODI ESISTENTI (invariati)
    // ============================================
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

    // ============================================
    // NUOVI METODI (per la AttivitaView)
    // ============================================

    /**
     * Collega i listener della view.
     */
    private void configuraListener() {
        view.getSearchField().textProperty().addListener((obs, oldVal, newVal) -> filtraAttivita());
        view.getFiltroStato().setOnAction(e -> filtraAttivita());
        view.getFiltroTipo().setOnAction(e -> filtraAttivita());
    }

    /**
     * Carica tutte le attività della matricola loggata.
     */
    private void caricaAttivita() {
        listaAttivita = attivitaDAO.getAttivitaStudente(matricolaUtente);
        view.mostraAttivita(listaAttivita);
    }

    /**
     * Filtra le attività in base a ricerca, stato e tipo.
     */
    private void filtraAttivita() {
        if (listaAttivita == null) return;

        String query = view.getSearchField().getText();
        String stato = view.getFiltroStato().getValue();
        String tipo = view.getFiltroTipo().getValue();

        List<Attivita> filtrati = listaAttivita.stream()
            .filter(a -> {
                // Ricerca testuale
                if (query != null && !query.trim().isEmpty()) {
                    String q = query.toLowerCase().trim();
                    if (!a.getDescrizione().toLowerCase().contains(q)) return false;
                }
                // Filtro stato
                if (stato != null && !"Tutti".equals(stato)) {
                    if (!a.getStato().toString().equals(stato)) return false;
                }
                // Filtro tipo
                if (tipo != null && !"Tutti".equals(tipo)) {
                    if (!a.getTipo().toString().equals(tipo)) return false;
                }
                return true;
            })
            .collect(Collectors.toList());

        view.mostraAttivita(filtrati);
    }

    /**
     * Apre il form per creare una nuova attività.
     * (Per ora placeholder; da collegare a FormAttivitaView)
     */
    private void apriFormNuovaAttivita() {
        System.out.println("Apro form nuova attività...");
        // TODO: quando avrai FormAttivitaView con selettore progetto:
        // FormAttivitaView form = new FormAttivitaView();
        // ... logica per creare ...
        // refresh();
    }

    /**
     * Ricarica la lista delle attività.
     */
    public void refresh() {
        caricaAttivita();
    }

    /**
     * Getter per la view (utile per il MainController o test).
     */
    public AttivitaView getView() {
        return view;
    }
}