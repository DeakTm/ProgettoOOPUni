package controller;

import boundary.ui.CommentoView;
import boundary.ui.MainView;
import boundary.persistence.dao.CommentoDAO;
import boundary.persistence.jdbc.CommentoBoundaryJdbc;
import entity.Commento;

import java.util.List;
import java.util.stream.Collectors;

public class CommentoController {

    private CommentoView view;
    private MainView mainView;
    private String matricolaUtente;
    private CommentoDAO commentoDAO;
    private List<Commento> listaCommenti;

    public CommentoController(MainView mainView, String matricolaUtente) {
        this.mainView = mainView;
        this.matricolaUtente = matricolaUtente;
        this.commentoDAO = new CommentoBoundaryJdbc();
        this.view = new CommentoView();

        configuraListener();
        caricaCommenti();

        mainView.mostraCommentiView(view);
    }

    private void configuraListener() {
        view.getSearchField().textProperty().addListener(
            (obs, oldV, newV) -> cercaCommenti(newV)
        );
        view.getBtnRicarica().setOnAction(e -> caricaCommenti());
    }

    private void caricaCommenti() {
        listaCommenti = commentoDAO.leggiCommentiStudente(matricolaUtente);
        view.mostraCommenti(listaCommenti);
    }

    private void cercaCommenti(String query) {
        if (listaCommenti == null) return;
        if (query == null || query.trim().isEmpty()) {
            view.mostraCommenti(listaCommenti);
            return;
        }
        String q = query.toLowerCase().trim();
        List<Commento> filtrati = listaCommenti.stream()
            .filter(c -> {
                boolean matchTesto = c.getTesto() != null && c.getTesto().toLowerCase().contains(q);
                boolean matchAtt = c.getId_attivita() != null 
                    && c.getId_attivita().getDescrizione() != null
                    && c.getId_attivita().getDescrizione().toLowerCase().contains(q);
                return matchTesto || matchAtt;
            })
            .collect(Collectors.toList());
        view.mostraCommenti(filtrati);
    }

    public CommentoView getView() {
        return view;
    }
}