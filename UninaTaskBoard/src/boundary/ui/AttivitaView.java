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

    // HEADER

    private VBox createHeader() {
        VBox header = new VBox(20);
        header.setPadding(new Insets(24, 24, 16, 24));

        VBox titles = new VBox(6);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        lblTitolo = new Label("Le Mie Attività");
        lblTitolo.getStyleClass().add("page-title");

        lblTotale = new Label("0");
        lblTotale.getStyleClass().add("badge");
        lblTotale.setStyle(
            "-fx-background-color: -color-primary-50; " +
            "-fx-text-fill: -color-primary-700; " +
            "-fx-border-color: -color-primary-100;"
        );

        titleRow.getChildren().addAll(lblTitolo, lblTotale);

        lblSottotitolo = new Label("Tutte le attività a cui sei assegnato, anche su progetti diversi");
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
        searchBox.setMaxWidth(320);

        filtroStato = new ComboBox<>();
        filtroStato.setPromptText("Stato");
        filtroStato.setPrefWidth(160);

        filtroTipo = new ComboBox<>();
        filtroTipo.setPromptText("Tipo");
        filtroTipo.setPrefWidth(160);

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

        TableColumn<Attivita, Void> colDesc = new TableColumn<>("Attività");
        colDesc.setPrefWidth(340);
        colDesc.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Attivita a = getTableRow().getItem();

                VBox box = new VBox(2);
                Label desc = new Label(a.getDescrizione());
                desc.getStyleClass().add("font-semibold");
                desc.setStyle("-fx-font-size: 13px;");

                Label meta = new Label("ID #" + a.getId());
                meta.getStyleClass().add("text-muted");
                meta.setStyle("-fx-font-size: 11px;");

                box.getChildren().addAll(desc, meta);
                setGraphic(box);
            }
        });

        TableColumn<Attivita, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setPrefWidth(150);
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
                        "-fx-background-color: -color-indigo-bg; " +
                        "-fx-text-fill: -color-indigo-text; " +
                        "-fx-border-color: -color-indigo-border;"
                    );
                } else {
                    badge.setStyle(
                        "-fx-background-color: -color-violet-bg; " +
                        "-fx-text-fill: -color-violet-text; " +
                        "-fx-border-color: -color-violet-border;"
                    );
                }
                setGraphic(badge);
                setText(null);
            }
        });

        TableColumn<Attivita, String> colStato = new TableColumn<>("Stato");
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
                    badge.getStyleClass().add("badge--completata");
                } else if (s.contains("corso")) {
                    badge.getStyleClass().add("badge--in-corso");
                } else {
                    // Non_Iniziata → neutro
                    badge.setStyle(
                        "-fx-background-color: -color-neutral-bg; " +
                        "-fx-text-fill: -color-neutral-text; " +
                        "-fx-border-color: -color-neutral-border;"
                    );
                }
                setGraphic(badge);
                setText(null);
            }
        });

        TableColumn<Attivita, LocalDate> colScadenza = new TableColumn<>("Scadenza");
        colScadenza.setPrefWidth(180);
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
                Label dataLbl = new Label(data.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
                dataLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                Label relLbl = new Label(relativeTime(data));
                relLbl.setStyle("-fx-font-size: 11px;");

        
                long giorni = ChronoUnit.DAYS.between(LocalDate.now(), data);
                if (giorni < 0) {
                    dataLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: -color-danger-text;");
                    relLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: -color-danger-text;");
                } else if (giorni <= 3) {
                    dataLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: -color-warning-text;");
                    relLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: -color-warning-text;");
                } else {
                    relLbl.getStyleClass().add("text-muted");
                }

                box.getChildren().addAll(dataLbl, relLbl);
                setGraphic(box);
                setText(null);
            }
        });

 
        TableColumn<Attivita, Void> colProgetto = new TableColumn<>("Progetto");
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
                    Label lbl = new Label(nomeProgetto);
                    lbl.setStyle(
                        "-fx-background-color: -color-surface-alt; " +
                        "-fx-text-fill: -color-text-secondary; " +
                        "-fx-border-color: -color-border; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6; " +
                        "-fx-padding: 3 10; " +
                        "-fx-font-size: 12px; " +
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

        Label desc = new Label("Non sei ancora assegnato a nessuna attività.\nQuando il tuo gruppo ti assegnerà un compito, apparirà qui.");
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