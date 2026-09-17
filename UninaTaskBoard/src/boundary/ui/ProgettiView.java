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

    // COMPONENTI PRINCIPALI (campi, non variabili locali)
    private BorderPane root;

    // Header
    private Label lblTitolo;
    private Label lblSottotitolo;
    private TextField searchField;
    private Button btnNuovoProgetto;

    // Griglia dei progetti
    private FlowPane grigliaProgetti;

    // COSTRUTTORE: costruisce la UI
    public ProgettiView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        // --- HEADER (in alto) ---
        VBox header = createHeader();
        root.setTop(header);

        // --- CENTRO: griglia di card ---
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        grigliaProgetti = new FlowPane();
        grigliaProgetti.setHgap(20);
        grigliaProgetti.setVgap(20);
        grigliaProgetti.getStyleClass().add("app-content");
        grigliaProgetti.setAlignment(Pos.TOP_LEFT);

        scroll.setContent(grigliaProgetti);
        root.setCenter(scroll);
    }

    // ============================================
    // CREAZIONE HEADER
    // ============================================
    private VBox createHeader() {
        VBox header = new VBox(24);
        header.getStyleClass().add("app-content");

        // Titolo + sottotitolo
        VBox titles = new VBox(8);
        lblTitolo = new Label("I Miei Progetti");
        lblTitolo.getStyleClass().add("page-title");
        lblSottotitolo = new Label("Tutti i progetti a cui partecipi");
        lblSottotitolo.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(lblTitolo, lblSottotitolo);

        // Toolbar: search + bottone
        HBox toolbar = new HBox();
        toolbar.getStyleClass().add("toolbar");

        HBox searchBox = new HBox();
        searchBox.getStyleClass().add("topbar__search");
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("text-muted");
        searchField = new TextField();
        searchField.setPromptText("Cerca progetto...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchBox.setMaxWidth(360);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnNuovoProgetto = new Button("+ Nuovo Progetto");
        btnNuovoProgetto.getStyleClass().addAll("button", "btn--primary");

        toolbar.getChildren().addAll(searchBox, spacer, btnNuovoProgetto);

        header.getChildren().addAll(titles, toolbar);
        return header;
    }

    // ============================================
    // CREAZIONE CARD PROGETTO
    // ============================================
    private VBox createProjectCard(Progetto progetto) {
        VBox card = new VBox(8);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(280);
        card.setMinHeight(140);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-cursor: hand;");

        // Click sulla card → apro il progetto
        card.setOnMouseClicked(e -> {
            if (onProgettoClick != null) {
                onProgettoClick.accept(progetto);
            }
        });

        // Titolo + stato
        HBox headerBox = new HBox(8);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label lblId = new Label("Progetto #" + progetto.getId());
        lblId.getStyleClass().add("card__title");
        HBox.setHgrow(lblId, Priority.ALWAYS);

        Label lblStato = new Label(progetto.getStato().toString());
        lblStato.getStyleClass().add("stat-card__trend");

        headerBox.getChildren().addAll(lblId, lblStato);

        // Scadenza
        Label lblScadenza = new Label("📅 Scadenza: " + formatScadenza(progetto));
        lblScadenza.getStyleClass().add("stat-card__label");

        // Hint
        Label lblHint = new Label("Clicca per aprire il progetto");
        lblHint.getStyleClass().add("text-muted");

        card.getChildren().addAll(headerBox, lblScadenza, lblHint);
        return card;
    }

    private String formatScadenza(Progetto progetto) {
        if (progetto.getScadenze() == null) return "Nessuna";           // ✅
        return progetto.getScadenze().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));  // ✅
    }
    
    public void mostraProgetti(List<Progetto> progetti) {
        grigliaProgetti.getChildren().clear();

        if (progetti == null || progetti.isEmpty()) {
            Label vuoto = new Label("Nessun progetto trovato. Creane uno nuovo!");
            vuoto.getStyleClass().add("page-subtitle");
            grigliaProgetti.getChildren().add(vuoto);
            return;
        }

        for (Progetto p : progetti) {
            grigliaProgetti.getChildren().add(createProjectCard(p));
        }
    }

    /**
     * Imposta il listener per il click su una card progetto.
     */
    public void setOnProgettoClick(Consumer<Progetto> handler) {
        this.onProgettoClick = handler;
    }

    private Consumer<Progetto> onProgettoClick;

    // ============================================
    // GETTER PER IL CONTROLLER
    // ============================================
    public BorderPane getRoot() { return root; }
    public Label getLblTitolo() { return lblTitolo; }
    public Label getLblSottotitolo() { return lblSottotitolo; }
    public TextField getSearchField() { return searchField; }
    public Button getBtnNuovoProgetto() { return btnNuovoProgetto; }
    public FlowPane getGrigliaProgetti() { return grigliaProgetti; }
}