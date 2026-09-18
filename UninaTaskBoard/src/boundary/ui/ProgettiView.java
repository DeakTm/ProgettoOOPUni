package boundary.ui;

import entity.Progetto;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

public class ProgettiView {

    // COMPONENTI PRINCIPALI
    private BorderPane root;

    // Header
    private Label lblTitolo;
    private Label lblSottotitolo;
    private TextField searchField;
    private Button btnNuovoProgetto;
    
    private Consumer<Progetto> onEliminaClick;
    private Consumer<Progetto> onProgettoClick;

    // Griglia dei progetti
    private FlowPane grigliaProgetti;

    // COSTRUTTORE
    public ProgettiView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        // --- HEADER 
        VBox header = createHeader();
        root.setTop(header);

        // --- CENTRO:
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        grigliaProgetti = new FlowPane();
        grigliaProgetti.setHgap(24);
        grigliaProgetti.setVgap(24);
        grigliaProgetti.getStyleClass().add("app-content");
        // Spaziatura generosa per staccarsi dai bordi e dalla sidebar
        grigliaProgetti.setPadding(new Insets(24, 36, 36, 36));
        grigliaProgetti.setAlignment(Pos.TOP_LEFT);

        scroll.setContent(grigliaProgetti);
        root.setCenter(scroll);
    }

    // ============================================
    // CREAZIONE HEADER
    // ============================================
    private VBox createHeader() {
        VBox header = new VBox(20);
        header.getStyleClass().add("app-content");
        header.setPadding(new Insets(32, 36, 16, 36));

        // Titolo + sottotitolo
        VBox titles = new VBox(6);
        lblTitolo = new Label("I Miei Progetti");
        lblTitolo.getStyleClass().add("page-title");
        lblSottotitolo = new Label("Tutti i progetti attivi a cui partecipi");
        lblSottotitolo.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(lblTitolo, lblSottotitolo);

        // Toolbar: search + bottone
        HBox toolbar = new HBox();
        toolbar.getStyleClass().add("toolbar");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        HBox searchBox = new HBox();
        searchBox.getStyleClass().add("topbar__search");
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("text-muted");
        searchField = new TextField();
        searchField.setPromptText("Cerca progetto per nome o ID...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchBox.setMaxWidth(380);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnNuovoProgetto = new Button("+ Nuovo Progetto");
        btnNuovoProgetto.getStyleClass().addAll("button", "btn--primary");

        toolbar.getChildren().addAll(searchBox, spacer, btnNuovoProgetto);

        header.getChildren().addAll(titles, toolbar);
        return header;
    }

    // ============================================
    // CREAZIONE CARD PROGETTO ELEGANTE
    // ============================================
    private VBox createProjectCard(Progetto progetto) {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setPrefWidth(300);
        card.setMinHeight(160);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-cursor: hand;");

        // Header card: ID Progetto + Badge stato colorato
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label lblId = new Label("Progetto #" + progetto.getId());
        lblId.getStyleClass().add("card__title");
        HBox.setHgrow(lblId, Priority.ALWAYS);

        // Badge dinamico basato sullo stato
        Label lblStato = new Label(progetto.getStato() != null ? progetto.getStato().toString() : "Attivo");
        lblStato.getStyleClass().add("badge");
        String statoStr = lblStato.getText().toLowerCase();
        if (statoStr.contains("completat")) {
            lblStato.getStyleClass().add("badge--completata");
        } else if (statoStr.contains("scad")) {
            lblStato.getStyleClass().add("badge--scaduta");
        } else {
            lblStato.getStyleClass().add("badge--in-corso");
        }

        headerBox.getChildren().addAll(lblId, lblStato);

        // Linea divisoria sottile interna
        Separator sep = new Separator();
        sep.getStyleClass().add("divider");

        // Dettaglio scadenza
        HBox scadenzaBox = new HBox(8);
        scadenzaBox.setAlignment(Pos.CENTER_LEFT);
        Label lblScadenzaIcon = new Label("📅");
        Label lblScadenzaText = new Label("Scadenza: " + formatScadenza(progetto));
        lblScadenzaText.getStyleClass().add("text-secondary");
        scadenzaBox.getChildren().addAll(lblScadenzaIcon, lblScadenzaText);

        // Footer: Bottone a sinistra, spaziatore al centro, Testo a destra
        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_LEFT);
        
        // 🗑 BOTTONE ELIMINA (ora è a sinistra)
        Button btnElimina = new Button("Elimina");
        btnElimina.setTooltip(new Tooltip("Elimina definitivamente il progetto"));
        btnElimina.getStyleClass().add("btn--icon-danger-solid");

        btnElimina.setOnAction(e -> {
            e.consume(); // Evita che il click apra il progetto
            if (onEliminaClick != null) {
                onEliminaClick.accept(progetto);
            }
        });

        // Spaziatore flessibile che allontana i due elementi
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Testo Hint (ora è spinto tutto a destra)
        Label lblHint = new Label("Apri dashboard ");
        lblHint.getStyleClass().add("text-muted");
        lblHint.setStyle("-fx-font-size: 12px;");

        // Aggiungiamo i componenti nell'ordine corretto
        footer.getChildren().addAll(btnElimina, spacer, lblHint);

        // Click sull'intera card → apre il progetto
        card.setOnMouseClicked(e -> {
            if (onProgettoClick != null) {
                onProgettoClick.accept(progetto);
            }
        });

        card.getChildren().addAll(headerBox, sep, scadenzaBox, footer);
        return card;
    } // <--- MANCAVA QUESTA GRAFFA!

    private String formatScadenza(Progetto progetto) {
        if (progetto.getScadenze() == null) return "Nessuna";
        return progetto.getScadenze().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
    public void mostraProgetti(List<Progetto> progetti) {
        grigliaProgetti.getChildren().clear();

        if (progetti == null || progetti.isEmpty()) {
            VBox vuotoBox = new VBox(12);
            vuotoBox.setAlignment(Pos.CENTER_LEFT);
            vuotoBox.setPadding(new Insets(20, 0, 0, 0));
            Label vuoto = new Label("Nessun progetto trovato.");
            vuoto.getStyleClass().add("section-title");
            Label vuotoSub = new Label("Crea il tuo primo progetto cliccando su '+ Nuovo Progetto' in alto a destra.");
            vuotoSub.getStyleClass().add("page-subtitle");
            vuotoBox.getChildren().addAll(vuoto, vuotoSub);
            grigliaProgetti.getChildren().add(vuotoBox);
            return;
        }

        for (Progetto p : progetti) {
            grigliaProgetti.getChildren().add(createProjectCard(p));
        }
    }

    // ============================================
    // LISTENER E GETTER
    // ============================================
    public void setOnProgettoClick(Consumer<Progetto> handler) {
        this.onProgettoClick = handler;
    }
    
    public void setOnEliminaClick(Consumer<Progetto> handler) {
        this.onEliminaClick = handler;
    }

    public BorderPane getRoot() { return root; }
    public Label getLblTitolo() { return lblTitolo; }
    public Label getLblSottotitolo() { return lblSottotitolo; }
    public TextField getSearchField() { return searchField; }
    public Button getBtnNuovoProgetto() { return btnNuovoProgetto; }
    public FlowPane getGrigliaProgetti() { return grigliaProgetti; }
}