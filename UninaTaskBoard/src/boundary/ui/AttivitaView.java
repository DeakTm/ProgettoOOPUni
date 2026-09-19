package boundary.ui;

import entity.Attivita;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Consumer;  

public class AttivitaView {

    private BorderPane root;

    private Label lblTitolo;
    private Label lblSottotitolo;
    private Label lblTotale;
    private TextField searchField;
    private ComboBox<String> filtroStato;
    private ComboBox<String> filtroTipo;
    
    private TableView<Attivita> tabellaAttivita;
    private Consumer<Attivita> onAttivitaDoppioClick;

    public AttivitaView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        VBox header = createHeader();
        root.setTop(header);

        tabellaAttivita = createTabella();
        VBox.setVgrow(tabellaAttivita, Priority.ALWAYS);

        VBox content = new VBox(tabellaAttivita);
        content.setPadding(new Insets(0, 24, 24, 24));
        VBox.setVgrow(tabellaAttivita, Priority.ALWAYS);

        root.setCenter(content);

        configuraFiltri();
    }

    private VBox createHeader() {
        VBox header = new VBox(16);
        header.setPadding(new Insets(28, 32, 20, 32));

        VBox titles = new VBox(4);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        lblTitolo = new Label("Le Mie Attività");
        lblTitolo.getStyleClass().add("page-title");

        lblTotale = new Label("0");
        lblTotale.getStyleClass().add("badge");
        lblTotale.setStyle(
            "-fx-background-color: -color-primary-50; " +
            "-fx-text-fill: -color-primary-700; " +
            "-fx-border-color: -color-primary-100; " +
            "-fx-font-weight: bold; -fx-padding: 2 8;"
        );

        titleRow.getChildren().addAll(lblTitolo, lblTotale);

        lblSottotitolo = new Label("Tutte le attività assegnate, monitorate in tempo reale sui diversi progetti");
        lblSottotitolo.getStyleClass().add("page-subtitle");
        titles.getChildren().addAll(titleRow, lblSottotitolo);

        HBox toolbar = new HBox(12);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        HBox searchBox = new HBox(8);
        searchBox.getStyleClass().add("topbar__search");
        searchBox.setAlignment(Pos.CENTER_LEFT);
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("text-muted");
        
        searchField = new TextField();
        searchField.setPromptText("Cerca per descrizione...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchBox.setPrefWidth(320);
        searchBox.setMaxWidth(320);

        filtroStato = new ComboBox<>();
        filtroStato.setPromptText("Filtra Stato");
        filtroStato.setPrefWidth(150);

        filtroTipo = new ComboBox<>();
        filtroTipo.setPromptText("Filtra Tipo");
        filtroTipo.setPrefWidth(150);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(searchBox, filtroStato, filtroTipo, spacer);
        header.getChildren().addAll(titles, toolbar);
        return header;
    }

    private TableView<Attivita> createTabella() {
        TableView<Attivita> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(createEmptyPlaceholder());

        // Colonna Attività (Senza ID)
        TableColumn<Attivita, Void> colDesc = new TableColumn<>("ATTIVITÀ");
        colDesc.setPrefWidth(320);
        colDesc.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Attivita a = getTableRow().getItem();

                VBox box = new VBox(3);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(6, 0, 6, 0));

                Label desc = new Label(a.getDescrizione());
                desc.getStyleClass().add("font-semibold");
                desc.setStyle("-fx-font-size: 13px; -fx-text-fill: -color-text-primary;");

                box.getChildren().add(desc);
                setGraphic(box);
            }
        });

        // Colonna Tipo 
        TableColumn<Attivita, String> colTipo = new TableColumn<>("TIPO");
        colTipo.setPrefWidth(140);
        colTipo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTipo().toString()));
        colTipo.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String tipo, boolean empty) {
                super.updateItem(tipo, empty);
                if (empty || tipo == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(tipo);
                badge.getStyleClass().add("badge");

                if ("Sviluppo".equalsIgnoreCase(tipo)) {
                    badge.setStyle(
                        "-fx-background-color: #e0e7ff; " +
                        "-fx-text-fill: #3730a3; " +
                        "-fx-border-color: #c7d2fe; " +
                        "-fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;"
                    );
                } else {
                    badge.setStyle(
                        "-fx-background-color: #f3e8ff; " +
                        "-fx-text-fill: #6b21a8; " +
                        "-fx-border-color: #e9d5ff; " +
                        "-fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;"
                    );
                }
                setGraphic(badge);
                setText(null);
            }
        });

        // Colonna Stato 
        TableColumn<Attivita, String> colStato = new TableColumn<>("STATO");
        colStato.setPrefWidth(150);
        colStato.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStato().toString()));
        colStato.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String stato, boolean empty) {
                super.updateItem(stato, empty);
                if (empty || stato == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(prettifyStato(stato));
                badge.getStyleClass().add("badge");

                String s = stato.toLowerCase();
                if (s.contains("completata")) {
                    badge.setStyle(
                        "-fx-background-color: #d1fae5; " +
                        "-fx-text-fill: #065f46; " +
                        "-fx-border-color: #a7f3d0; " +
                        "-fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;"
                    );
                } else if (s.contains("corso")) {
                    badge.setStyle(
                        "-fx-background-color: #fef3c7; " +
                        "-fx-text-fill: #92400e; " +
                        "-fx-border-color: #fde68a; " +
                        "-fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;"
                    );
                } else {
                    badge.setStyle(
                        "-fx-background-color: #eff6ff; " +
                        "-fx-text-fill: #1e40af; " +
                        "-fx-border-color: #bfdbfe; " +
                        "-fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;"
                    );
                }
                setGraphic(badge);
                setText(null);
            }
        });

        // Colonna Scadenza
        TableColumn<Attivita, LocalDate> colScadenza = new TableColumn<>("SCADENZA");
        colScadenza.setPrefWidth(170);
        colScadenza.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getDataScadenza()));
        colScadenza.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate data, boolean empty) {
                super.updateItem(data, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }
                if (data == null) {
                    Label lbl = new Label("—");
                    lbl.getStyleClass().add("text-muted");
                    setGraphic(lbl);
                    setText(null);
                    return;
                }

                VBox box = new VBox(2);
                box.setAlignment(Pos.CENTER_LEFT);
                Label dataLbl = new Label(data.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                Label relLbl = new Label(relativeTime(data));
                relLbl.setStyle("-fx-font-size: 11px;");

                long giorni = ChronoUnit.DAYS.between(LocalDate.now(), data);
                if (giorni < 0) {
                    dataLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #dc2626;");
                    relLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #dc2626;");
                } else if (giorni <= 3) {
                    dataLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #d97706;");
                    relLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #d97706;");
                } else {
                    dataLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
                    relLbl.getStyleClass().add("text-muted");
                }

                box.getChildren().addAll(dataLbl, relLbl);
                setGraphic(box);
                setText(null);
            }
        });

        // Colonna Progetto 
        TableColumn<Attivita, Void> colProgetto = new TableColumn<>("PROGETTO");
        colProgetto.setPrefWidth(160); 
        colProgetto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Attivita a = getTableRow().getItem();
                
                String nomeProgetto = null;
                if (a.getProgetto() != null && a.getProgetto().getNome() != null) {
                    nomeProgetto = a.getProgetto().getNome();
                }

                if (nomeProgetto == null || nomeProgetto.trim().isEmpty()) {
                    Label lbl = new Label("—");
                    lbl.getStyleClass().add("text-muted");
                    setGraphic(lbl);
                } else {
                    Label lbl = new Label("📁 " + nomeProgetto);
                    lbl.setStyle(
                        "-fx-background-color: #dbeafe; " +
                        "-fx-text-fill: #1d4ed8; " +
                        "-fx-border-color: #93c5fd; " +
                        "-fx-border-radius: 14; " +
                        "-fx-background-radius: 14; " +
                        "-fx-padding: 4 12; " +
                        "-fx-font-size: 11.5px; " +
                        "-fx-font-weight: bold;"
                    );
                    setGraphic(lbl);
                }
                setText(null);
            }
        });

        table.getColumns().addAll(colDesc, colTipo, colStato, colScadenza, colProgetto);

        table.setRowFactory(tv -> {
            TableRow<Attivita> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    if (onAttivitaDoppioClick != null) {
                        onAttivitaDoppioClick.accept(row.getItem());
                    }
                }
            });
            return row;
        });

        return table;
    }

    private VBox createEmptyPlaceholder() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));

        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 42px;");

        Label title = new Label("Nessuna attività trovata");
        title.getStyleClass().add("card__title");

        Label desc = new Label("Non sei ancora assegnato a nessuna attività.\nQuando ti verrà assegnato un compito, apparirà qui.");
        desc.getStyleClass().add("text-muted");
        desc.setStyle("-fx-text-alignment: center; -fx-font-size: 12px;");
        desc.setWrapText(true);
        desc.setMaxWidth(360);

        box.getChildren().addAll(icon, title, desc);
        return box;
    }

    private String prettifyStato(String stato) {
        return stato.replace("_", " ");
    }

    private String relativeTime(LocalDate data) {
        long giorni = ChronoUnit.DAYS.between(LocalDate.now(), data);
        if (giorni == 0) return "Oggi";
        if (giorni == 1) return "Domani";
        if (giorni == -1) return "Ieri";
        if (giorni < 0) return Math.abs(giorni) + " giorni fa";
        if (giorni <= 30) return "tra " + giorni + " giorni";
        long mesi = giorni / 30;
        return "tra ~" + mesi + (mesi == 1 ? " mese" : " mesi");
    }

    private void configuraFiltri() {
        filtroStato.getItems().addAll("Tutti", "Non_Iniziata", "In_Corso", "Completata");
        filtroTipo.getItems().addAll("Tutti", "Sviluppo", "Documentazione");
    }

    public void mostraAttivita(List<Attivita> lista) {
        tabellaAttivita.getItems().clear();
        if (lista != null) {
            tabellaAttivita.getItems().addAll(lista);
            lblTotale.setText(String.valueOf(lista.size()));
        } else {
            lblTotale.setText("0");
        }
    }

    public Attivita getAttivitaSelezionata() {
        return tabellaAttivita.getSelectionModel().getSelectedItem();
    }

    public void setOnAttivitaDoppioClick(Consumer<Attivita> handler) {
        this.onAttivitaDoppioClick = handler;
    }

    public BorderPane getRoot() { return root; }
    public TableView<Attivita> getTabellaAttivita() { return tabellaAttivita; }
    public TextField getSearchField() { return searchField; }
    public ComboBox<String> getFiltroStato() { return filtroStato; }
    public ComboBox<String> getFiltroTipo() { return filtroTipo; }
}