package controller;

import boundary.persistence.dao.AttivitaDAO;
import boundary.persistence.jdbc.AttivitaBoundaryJdbc;
import boundary.persistence.dao.ProgettoDAO;
import boundary.persistence.jdbc.ProgettoBoundaryJdbc;
import boundary.ui.MainView;
import entity.Attivita;
import entity.Progetto;
import entity.enums.StatoAvanzamento;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainController {

    private MainView view;
    private Stage stage;
    private String matricolaLoggata;
    private ProgettoController progettoController; 

    public MainController(Stage stage, String matricolaLoggata) {
        this.stage = stage;
        this.matricolaLoggata = matricolaLoggata;

        this.view = new MainView();
        this.view.setUtenteLoggato(matricolaLoggata);

        Scene scene = new Scene(view.getRoot(), 1200, 800);

        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (NullPointerException e) {
            System.err.println("Errore: File CSS non trovato in /css/style.css");
        }

        this.stage.setScene(scene);
        this.stage.setTitle("UninaTaskBoard - Dashboard");

        inizializzaEventi();
        
        // Carica le scadenze imminenti all'avvio della dashboard
        caricaScadenzeDashboard();
    }

    private void inizializzaEventi() {
        // Evento Quick Action: Crea nuovo progetto dalla Home
        view.getBtnNuovoProgettoQuick().setOnAction(event -> {
            apriFormNuovoProgettoQuick();
        });

        view.getItemDashboard().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemDashboard().getStyleClass().add("is-active");
            view.mostraDashboard(); 
            caricaScadenzeDashboard(); // Aggiorna le scadenze al ritorno sulla home
        });

        view.getItemProgetti().setOnMouseClicked(event -> {   
            rimuoviAttivi();
            view.getItemProgetti().getStyleClass().add("is-active");
            mostraProgetti();
        });

        view.getItemAttivita().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemAttivita().getStyleClass().add("is-active");
            new AttivitaController(view, matricolaLoggata);
        });
        
        view.getItemCommenti().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemCommenti().getStyleClass().add("is-active");
            new CommentoController(view, matricolaLoggata);
        });
        
        view.getBtnLogout().setOnAction(event -> {
            System.out.println("Logout effettuato per l'utente: " + matricolaLoggata);
            new LoginController(stage);
        });
    }

    private void caricaScadenzeDashboard() {
        try {
            AttivitaDAO attivitaDAO = new AttivitaBoundaryJdbc();
            List<Attivita> tutteLeAttivita = attivitaDAO.getAttivitaStudente(matricolaLoggata);

            List<Attivita> inScadenza = tutteLeAttivita.stream()
                .filter(a -> a.getDataScadenza() != null)
                .filter(a -> {
                    LocalDate oggi = LocalDate.now();
                    LocalDate scadenza = a.getDataScadenza();
                    long giorni = ChronoUnit.DAYS.between(oggi, scadenza);
                    // Filtra attività che scadono da oggi fino ai prossimi 7 giorni (escludendo quelle già completate se lo stato è noto)
                    boolean nonCompletata = a.getStato() == null || !a.getStato().toString().toLowerCase().contains("completat");
                    return giorni >= 0 && giorni <= 7 && nonCompletata;
                })
                .collect(Collectors.toList());

            view.mostraScadenzeImminenti(inScadenza);
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento delle scadenze in dashboard: " + e.getMessage());
        }
    }

    private void apriFormNuovoProgettoQuick() {
        Dialog<Progetto> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Progetto");
        dialog.setHeaderText("Inserisci i dettagli del nuovo progetto:");

        ButtonType btnCrea = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCrea, ButtonType.CANCEL);

        TextField txtNome = new TextField();
        txtNome.setPromptText("Es. Sviluppo Backend");

        DatePicker datePicker = new DatePicker(LocalDate.now().plusWeeks(2));

        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Nome Progetto:"), txtNome,
            new Label("Data di Scadenza:"), datePicker
        );
        content.setPadding(new Insets(16));
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCrea) {
                String nome = txtNome.getText();
                LocalDate scadenza = datePicker.getValue();
                if (nome != null && !nome.trim().isEmpty() && scadenza != null) {
                    return new Progetto(StatoAvanzamento.Creato, scadenza, nome.trim());
                }
            }
            return null;
        });

        Optional<Progetto> risultato = dialog.showAndWait();
        if (risultato.isEmpty()) return;

        Progetto nuovo = risultato.get();
        ProgettoDAO progettoDAO = new ProgettoBoundaryJdbc();
        int id = progettoDAO.creaProgetto(nuovo);

        if (id > 0) {
            progettoDAO.assegnaStudenteProgetto(matricolaLoggata, id);
            caricaScadenzeDashboard(); // Aggiorna eventuali scadenze/vista
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText(null);
            alert.setContentText("Errore nella creazione del progetto nel database.");
            alert.showAndWait();
        }
    }

    private void rimuoviAttivi() {
        view.getItemDashboard().getStyleClass().remove("is-active");
        view.getItemProgetti().getStyleClass().remove("is-active");
        view.getItemAttivita().getStyleClass().remove("is-active");
        view.getItemCommenti().getStyleClass().remove("is-active");
    }

    private void mostraProgetti() {
        this.progettoController = new ProgettoController(view, matricolaLoggata);
    }
}