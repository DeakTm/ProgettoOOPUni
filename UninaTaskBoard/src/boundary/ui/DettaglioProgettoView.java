package boundary.ui;

import entity.Progetto;
import entity.Studente;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.List;
import java.util.function.Consumer;

public class DettaglioProgettoView {

    private BorderPane root;
    private Progetto progetto;

    private Button btnTornaIndietro;
    private Button btnAggiungiMembro;
    private Button btnNuovaAttivita;

    private Label lblStatTotale;
    private Label lblStatCompletate;
    private Label lblStatInCorso;

    private VBox containerMembri;
    private Consumer<Studente> onRimuoviMembroClick;

    public DettaglioProgettoView(Progetto progetto) {
        this.progetto = progetto;
        
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        VBox mainContent = new VBox(28);
        mainContent.getStyleClass().add("app-content");
        mainContent.setPadding(new Insets(32, 36, 36, 36));

        // 1. Navigation + Header Progetto
        VBox headerBox = createHeader();

        // 2. Statistiche
        GridPane statsGrid = createStatsGrid();

        // 3. Sezione Membri del Progetto
        VBox membriBox = createMembriSection();

        mainContent.getChildren().addAll(headerBox, statsGrid, membriBox);
        scroll.setContent(mainContent);
        
        root.setCenter(scroll);
    }

    private VBox createHeader() {
        VBox header = new VBox(12);

        btnTornaIndietro = new Button("← Torna ai Progetti");
        btnTornaIndietro.getStyleClass().addAll("button", "btn--secondary");

        HBox titleBox = new HBox(16);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label lblTitolo = new Label(progetto.getNome());
        lblTitolo.getStyleClass().add("page-title");

        Label lblStato = new Label(progetto.getStato() != null ? progetto.getStato().toString() : "Attivo");
        lblStato.getStyleClass().addAll("badge", "badge--in-corso");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnNuovaAttivita = new Button("+ Nuova Attività");
        btnNuovaAttivita.getStyleClass().addAll("button", "btn--primary");

        titleBox.getChildren().addAll(lblTitolo, lblStato, spacer, btnNuovaAttivita);

        header.getChildren().addAll(btnTornaIndietro, titleBox);
        return header;
    }

    private GridPane createStatsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);

        lblStatTotale = new Label("0");
        lblStatCompletate = new Label("0");
        lblStatInCorso = new Label("0");

        grid.add(createStatCard("Attività Totali", lblStatTotale), 0, 0);
        grid.add(createStatCard("Completate", lblStatCompletate), 1, 0);
        grid.add(createStatCard("In Corso", lblStatInCorso), 2, 0);

        return grid;
    }

    private VBox createStatCard(String title, Label valLabel) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card");
        card.setPrefWidth(220);

        Label lblTitle = new Label(title);
        lblTitle.getStyleClass().add("stat-card__label");

        valLabel.getStyleClass().add("stat-card__value");

        card.getChildren().addAll(lblTitle, valLabel);
        return card;
    }

    private VBox createMembriSection() {
        VBox card = new VBox(16);
        card.getStyleClass().add("chart-card");
        card.setPadding(new Insets(24));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblSectionTitle = new Label("Membri del Team");
        lblSectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnAggiungiMembro = new Button("+ Aggiungi Studenti");
        btnAggiungiMembro.getStyleClass().addAll("button", "btn--secondary");

        header.getChildren().addAll(lblSectionTitle, spacer, btnAggiungiMembro);

        containerMembri = new VBox(10);

        card.getChildren().addAll(header, new Separator(), containerMembri);
        return card;
    }

    // Mostra la lista degli studenti associati al progetto
    public void mostraMembri(List<Studente> studenti) {
        containerMembri.getChildren().clear();

        if (studenti == null || studenti.isEmpty()) {
            Label vuoto = new Label("Nessun membro assegnato a questo progetto.");
            vuoto.getStyleClass().add("text-muted");
            containerMembri.getChildren().add(vuoto);
            return;
        }

        for (Studente s : studenti) {
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(8, 12, 8, 12));
            item.setStyle("-fx-background-color: -color-surface-alt; -fx-background-radius: 6px;");

            StackPane avatar = new StackPane();
            avatar.getStyleClass().add("avatar");
            Label lblInit = new Label(s.getMatricola().substring(0, Math.min(2, s.getMatricola().length())).toUpperCase());
            avatar.getChildren().add(lblInit);

            VBox info = new VBox(2);
            Label lblNome = new Label(s.getNome() + " " + s.getCognome());
            lblNome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            Label lblMatricola = new Label("Matricola: " + s.getMatricola());
            lblMatricola.getStyleClass().add("text-muted");
            info.getChildren().addAll(lblNome, lblMatricola);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button btnRimuovi = new Button("Rimuovi");
            btnRimuovi.getStyleClass().add("btn--icon-danger-solid");
            btnRimuovi.setOnAction(e -> {
                if (onRimuoviMembroClick != null) {
                    onRimuoviMembroClick.accept(s);
                }
            });

            item.getChildren().addAll(avatar, info, spacer, btnRimuovi);
            containerMembri.getChildren().add(item);
        }
    }

 // Dialog per Selezione Multipla di Studenti (con Ricerca)
    public List<Studente> mostraDialogAggiungiMembri(List<Studente> studentiDisponibili) {
        Dialog<List<Studente>> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Membri al Progetto");
        dialog.setHeaderText("Cerca e seleziona gli studenti da aggiungere:");

        ButtonType btnAggiungiType = new ButtonType("Aggiungi Selezionati", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAggiungiType, ButtonType.CANCEL);

        // --- BARRA DI RICERCA ---
        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Cerca per nome, cognome o matricola...");
        searchField.getStyleClass().add("text-field");

        // --- LISTA OSSERVABILE E FILTRABILE ---
        ObservableList<Studente> observableList = FXCollections.observableArrayList(studentiDisponibili);
        FilteredList<Studente> filteredData = new FilteredList<>(observableList, p -> true);

        // Aggiungo il listener per filtrare dinamicamente mentre si digita
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(studente -> {
                if (newVal == null || newVal.trim().isEmpty()) {
                    return true;
                }
                String filter = newVal.toLowerCase().trim();
                return studente.getNome().toLowerCase().contains(filter) ||
                       studente.getCognome().toLowerCase().contains(filter) ||
                       studente.getMatricola().toLowerCase().contains(filter);
            });
        });

        // --- 3. LISTVIEW ---
        ListView<Studente> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(filteredData); // Imposto i dati filtrati, non la lista base
        listView.setPrefHeight(250);

        // Visualizzazione personalizzata delle celle
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Studente item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getMatricola() + " - " + item.getNome() + " " + item.getCognome());
                }
            }
        });

        // --- 4. LAYOUT ---
        VBox content = new VBox(12);
        Label hint = new Label("Tieni premuto CTRL per selezionare più studenti.");
        hint.getStyleClass().add("text-muted");
        hint.setStyle("-fx-font-size: 11px;");

        content.getChildren().addAll(searchField, listView, hint);
        content.setPadding(new Insets(16));
        content.setPrefWidth(350);
        
        dialog.getDialogPane().setContent(content);

        // Gestione del risultato
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAggiungiType) {
                // Ritorna gli elementi attualmente selezionati nella vista
                return listView.getSelectionModel().getSelectedItems();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    // Getter e Setter
    public BorderPane getRoot() { return root; }
    public Button getBtnTornaIndietro() { return btnTornaIndietro; }
    public Button getBtnAggiungiMembro() { return btnAggiungiMembro; }
    public Button getBtnNuovaAttivita() { return btnNuovaAttivita; }
    public Label getLblStatTotale() { return lblStatTotale; }
    public Label getLblStatCompletate() { return lblStatCompletate; }
    public Label getLblStatInCorso() { return lblStatInCorso; }
    public void setOnRimuoviMembroClick(Consumer<Studente> listener) { this.onRimuoviMembroClick = listener; }
}