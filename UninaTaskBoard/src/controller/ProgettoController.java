package controller;

import boundary.ui.FormProgettoView;
import boundary.ui.MainView;
import boundary.ui.ProgettiView;
import boundary.persistence.dao.ProgettoDAO;
import boundary.persistence.jdbc.ProgettoBoundaryJdbc;   
import entity.Progetto;
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
    private List<Progetto> listaProgetti;

    public ProgettoController(MainView mainView, String matricolaUtente) {
        this.mainView = mainView;
        this.matricolaUtente = matricolaUtente;
        this.progettoDAO = new ProgettoBoundaryJdbc();  // ✅ IL TUO NAMING
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

    private void apriDettaglioProgetto(Progetto progetto) {
        System.out.println("Apro progetto #" + progetto.getId());
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

        if (scadenza == null) return;  // utente ha annullato

        Progetto nuovo = new Progetto(StatoAvanzamento.Creato, scadenza);
        int id = progettoDAO.creaProgetto(nuovo);

        if (id > 0) {
            // Aggiungo automaticamente il creatore come membro
            progettoDAO.assegnaStudenteProgetto(matricolaUtente, id);
            refresh();  // ricarica la griglia
        } else {
            mostraErrore("Errore nella creazione del progetto");
        }
    }

    private void mostraErrore(String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
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

	public void refresh() {
        caricaProgetti();
    }

    public ProgettiView getView() {
        return view;
    }
}