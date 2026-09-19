package boundary.ui;

import entity.Attivita;
import entity.Commento;
import entity.FileCodice;
import entity.Revisione;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DettagliAttivitaView {

    private Stage stage;
    private BorderPane root;

    private Label lblDescrizione;
    private Label lblTipo;
    private Label lblStato;
    private Label lblScadenza;

    // File
    private ListView<FileCodice> listaFile;
    private Button btnImportaFile;
    private Button btnEliminaFile;

    // Editor
    private TextArea txtContenuto;
    private Button btnSalvaFile;
    private Label lblFileCorrente;

    // Revisioni
    private TableView<Revisione> tabellaRevisioni;
    private Button btnAggiungiRevisione;
    private Label lblNomeFileSelezionato;

    // Commenti
    private ListView<Commento> listaCommenti;
    private TextArea txtNuovoCommento;
    private Button btnInviaCommento;
    private Button btnEliminaCommento;

    // TabPane
    private TabPane tabPane;
    private Tab tabFileRevisioni;
    private Tab tabCommenti;

    public DettagliAttivitaView(Stage owner) {
        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Dettaglio Attività");

        root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setPadding(new Insets(24));

        root.setTop(createHeader());

        // --- TABPANE ---
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("app-main");

        tabFileRevisioni = createTabFileRevisioni();
        tabCommenti = createTabCommenti();

        tabPane.getTabs().addAll(tabFileRevisioni, tabCommenti);
        BorderPane.setMargin(tabPane, new Insets(16, 0, 0, 0));

        root.setCenter(tabPane);
    }


    private VBox createHeader() {
        VBox box = new VBox(6);

        lblDescrizione = new Label("");
        lblDescrizione.getStyleClass().add("page-title");

        HBox meta = new HBox(12);
        meta.setAlignment(Pos.CENTER_LEFT);

        lblTipo = new Label("");
        lblTipo.getStyleClass().add("badge");
        lblStato = new Label("");
        lblStato.getStyleClass().add("badge");
        lblScadenza = new Label("");
        lblScadenza.getStyleClass().add("text-muted");

        meta.getChildren().addAll(lblTipo, lblStato, lblScadenza);
        box.getChildren().addAll(lblDescrizione, meta);
        return box;
    }


    private Tab createTabFileRevisioni() {
        Tab tab = new Tab("📄 File e Revisioni");

        SplitPane split = new SplitPane();
        split.setDividerPositions(0.45);
        split.getItems().addAll(createLeftBox(), createRightBox());
        split.setPadding(new Insets(16));

        tab.setContent(split);
        return tab;
    }

    private VBox createLeftBox() {
        VBox box = new VBox(10);

        HBox titoloRow = new HBox(12);
        titoloRow.setAlignment(Pos.CENTER_LEFT);
        Label titolo = new Label("📄 File di Codice");
        titolo.getStyleClass().add("card__title");
        HBox.setHgrow(titolo, Priority.ALWAYS);

        btnImportaFile = new Button("📁 Importa file");
        btnImportaFile.getStyleClass().addAll("button", "btn--primary");

        titoloRow.getChildren().addAll(titolo, btnImportaFile);

        listaFile = new ListView<>();
        listaFile.setPlaceholder(new Label("Nessun file associato"));
        listaFile.setPrefHeight(150);

        listaFile.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(FileCodice f, boolean empty) {
                super.updateItem(f, empty);
                if (empty || f == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                HBox cell = new HBox(8);
                cell.setAlignment(Pos.CENTER_LEFT);
                Label nome = new Label("📎 " + f.getNome_file());
                nome.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                HBox.setHgrow(nome, Priority.ALWAYS);
                Label lang = new Label(f.getLinguaggio() != null ? f.getLinguaggio().toString() : "—");
                lang.getStyleClass().add("text-muted");
                lang.setStyle("-fx-font-size: 11px;");
                cell.getChildren().addAll(nome, lang);
                setGraphic(cell);
            }
        });

        lblFileCorrente = new Label("Seleziona un file per modificarlo");
        lblFileCorrente.getStyleClass().add("text-muted");

        txtContenuto = new TextArea();
        txtContenuto.setPromptText("Il contenuto del file apparirà qui...");
        txtContenuto.setPrefRowCount(12);
        txtContenuto.setStyle("-fx-font-family: 'Consolas', 'Monospaced';");
        VBox.setVgrow(txtContenuto, Priority.ALWAYS);

        btnSalvaFile = new Button("💾 Salva modifiche");
        btnSalvaFile.getStyleClass().addAll("button", "btn--primary");
        btnSalvaFile.setDisable(true);

        btnEliminaFile = new Button("🗑 Elimina file");
        btnEliminaFile.getStyleClass().add("button");
        btnEliminaFile.setDisable(true);

        HBox fileActions = new HBox(8);
        fileActions.setAlignment(Pos.CENTER_RIGHT);
        fileActions.getChildren().add(btnEliminaFile);

        box.getChildren().addAll(titoloRow, listaFile, fileActions, lblFileCorrente, txtContenuto, btnSalvaFile);
        return box;
    }

    private VBox createRightBox() {
        VBox box = new VBox(10);

        HBox titoloRow = new HBox(12);
        titoloRow.setAlignment(Pos.CENTER_LEFT);

        lblNomeFileSelezionato = new Label("Revisioni");
        lblNomeFileSelezionato.getStyleClass().add("card__title");
        HBox.setHgrow(lblNomeFileSelezionato, Priority.ALWAYS);

        btnAggiungiRevisione = new Button("➕ Aggiungi revisione");
        btnAggiungiRevisione.getStyleClass().addAll("button", "btn--primary");
        btnAggiungiRevisione.setDisable(true);

        titoloRow.getChildren().addAll(lblNomeFileSelezionato, btnAggiungiRevisione);

        tabellaRevisioni = new TableView<>();
        tabellaRevisioni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabellaRevisioni.setPlaceholder(new Label("Nessuna revisione"));
        VBox.setVgrow(tabellaRevisioni, Priority.ALWAYS);

        TableColumn<Revisione, String> colData = new TableColumn<>("Data");
        colData.setPrefWidth(140);
        colData.setCellValueFactory(c -> {
            if (c.getValue().getData() == null) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
            return new javafx.beans.property.SimpleStringProperty(
                c.getValue().getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
        });

        TableColumn<Revisione, String> colAutore = new TableColumn<>("Autore");
        colAutore.setPrefWidth(120);
        colAutore.setCellValueFactory(c -> {
            Revisione r = c.getValue();
            if (r.getMatricola() == null) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
            return new javafx.beans.property.SimpleStringProperty(r.getMatricola().getMatricola());
        });

        TableColumn<Revisione, String> colNota = new TableColumn<>("Nota");
        colNota.setPrefWidth(300);
        colNota.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getNota()));

        tabellaRevisioni.getColumns().addAll(colData, colAutore, colNota);

        box.getChildren().addAll(titoloRow, tabellaRevisioni);
        return box;
    }


    private Tab createTabCommenti() {
        Tab tab = new Tab("💬 Commenti");

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(16));

        listaCommenti = new ListView<>();
        listaCommenti.setPlaceholder(new Label("Nessun commento"));
        listaCommenti.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Commento c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                VBox cell = new VBox(4);
                cell.setPadding(new Insets(8));

                HBox headerBox = new HBox(8);
                headerBox.setAlignment(Pos.CENTER_LEFT);

                String matricola = (c.getMatricola() != null) ? c.getMatricola().getMatricola() : "Anonimo";
                Label autore = new Label("👤 " + matricola);
                autore.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
                HBox.setHgrow(autore, Priority.ALWAYS);

                String dataStr = (c.getDataCommento() != null)
                    ? c.getDataCommento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    : "";
                Label data = new Label(dataStr);
                data.getStyleClass().add("text-muted");
                data.setStyle("-fx-font-size: 11px;");

                headerBox.getChildren().addAll(autore, data);

                Label testo = new Label(c.getTesto());
                testo.setWrapText(true);
                testo.setStyle("-fx-font-size: 13px;");

                cell.getChildren().addAll(headerBox, testo);
                setGraphic(cell);
            }
        });
        content.setCenter(listaCommenti);


        VBox bottomBox = new VBox(10);
        bottomBox.setPadding(new Insets(12, 0, 0, 0));

        btnEliminaCommento = new Button("🗑 Elimina commento selezionato");
        btnEliminaCommento.getStyleClass().add("button");
        btnEliminaCommento.setDisable(true);

        txtNuovoCommento = new TextArea();
        txtNuovoCommento.setPromptText("Scrivi un commento...");
        txtNuovoCommento.setPrefRowCount(3);
        txtNuovoCommento.setWrapText(true);

        btnInviaCommento = new Button("📨 Invia commento");
        btnInviaCommento.getStyleClass().addAll("button", "btn--primary");
        btnInviaCommento.setMaxWidth(Double.MAX_VALUE);

        bottomBox.getChildren().addAll(
            new Separator(),
            btnEliminaCommento,
            txtNuovoCommento,
            btnInviaCommento
        );
        content.setBottom(bottomBox);

        tab.setContent(content);
        return tab;
    }


    public void setAttivita(Attivita a) {
        lblDescrizione.setText(a.getDescrizione());
        lblTipo.setText(a.getTipo() != null ? a.getTipo().toString() : "—");
        lblStato.setText(a.getStato() != null ? a.getStato().toString() : "—");
        if (a.getDataScadenza() != null) {
            lblScadenza.setText("📅 " + a.getDataScadenza().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblScadenza.setText("📅 Nessuna scadenza");
        }
    }

    public void setFile(List<FileCodice> files) {
        listaFile.getItems().clear();
        if (files != null) listaFile.getItems().addAll(files);
    }

    public void setRevisioni(FileCodice file, List<Revisione> revisioni) {
        lblNomeFileSelezionato.setText("Revisioni di: " + file.getNome_file());
        tabellaRevisioni.getItems().clear();
        if (revisioni != null) tabellaRevisioni.getItems().addAll(revisioni);
    }

    public void setContenutoEditor(FileCodice file) {
        if (file == null) {
            lblFileCorrente.setText("Seleziona un file per modificarlo");
            txtContenuto.clear();
            btnSalvaFile.setDisable(true);
            btnAggiungiRevisione.setDisable(true);
            btnEliminaFile.setDisable(true);
        } else {
            lblFileCorrente.setText("File: " + file.getNome_file());
            txtContenuto.setText(file.getContenuto() != null ? file.getContenuto() : "");
            btnSalvaFile.setDisable(false);
            btnAggiungiRevisione.setDisable(false);
            btnEliminaFile.setDisable(false);
        }
    }

    public void setCommenti(List<Commento> commenti) {
        listaCommenti.getItems().clear();
        if (commenti != null) listaCommenti.getItems().addAll(commenti);
    }

    public Commento getCommentoSelezionato() {
        return listaCommenti.getSelectionModel().getSelectedItem();
    }

    public String getTestoNuovoCommento() {
        return txtNuovoCommento.getText();
    }

    public void pulisciCampoCommento() {
        txtNuovoCommento.clear();
    }


    public void mostraSoloDocumentazione() {
        tabPane.getTabs().remove(tabFileRevisioni);
        tabPane.getSelectionModel().select(tabCommenti);
    }

    public void mostra() {
        Scene scene = new Scene(root, 1200, 750);
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(css);
        } catch (Exception e) {
            System.err.println("CSS non trovato");
        }
        stage.setScene(scene);
        stage.show();
    }

 
    public Stage getStage() { return stage; }
    public BorderPane getRoot() { return root; }
    public ListView<FileCodice> getListaFile() { return listaFile; }
    public Button getBtnImportaFile() { return btnImportaFile; }
    public Button getBtnEliminaFile() { return btnEliminaFile; }
    public Button getBtnSalvaFile() { return btnSalvaFile; }
    public Button getBtnAggiungiRevisione() { return btnAggiungiRevisione; }
    public TextArea getTxtContenuto() { return txtContenuto; }
    public ListView<Commento> getListaCommenti() { return listaCommenti; }
    public Button getBtnInviaCommento() { return btnInviaCommento; }
    public Button getBtnEliminaCommento() { return btnEliminaCommento; }
}