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
    private Button btnGestisciMembri;
    private Button btnCambiaStato;

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
        VBox box = new VBox(8);

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

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnCambiaStato = new Button("🔄 Segna come Completata");
        btnCambiaStato.getStyleClass().addAll("button", "btn--primary");

        btnGestisciMembri = new Button("👥 Gestisci membri");
        btnGestisciMembri.getStyleClass().add("button");
        btnGestisciMembri.setStyle(
            "-fx-background-color: #e0f2fe; " + 
            "-fx-text-fill: #0369a1; " +       
            "-fx-border-color: #bae6fd; " +     
            "-fx-border-width: 1; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6;" //hard-coddado lo style 
        );

        meta.getChildren().addAll(lblTipo, lblStato, lblScadenza, spacer, btnCambiaStato, btnGestisciMembri);
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
        btnEliminaFile.setStyle(
            "-fx-background-color: #fee2e2; " + 
            "-fx-text-fill: #991b1b; " +        
            "-fx-border-color: #fecaca; " +     
            "-fx-border-width: 1; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6;"
        );

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
        tabellaRevisioni.getStyleClass().add("table-view");
        tabellaRevisioni.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabellaRevisioni.setPlaceholder(new Label("Nessuna revisione"));
        VBox.setVgrow(tabellaRevisioni, Priority.ALWAYS);

        // Colonna Data
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
        colData.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(null);
                }
            }
        });

        // Colonna Autore
        TableColumn<Revisione, String> colAutore = new TableColumn<>("Autore");
        colAutore.setPrefWidth(120);
        colAutore.setCellValueFactory(c -> {
            Revisione r = c.getValue();
            if (r.getMatricola() == null) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
            return new javafx.beans.property.SimpleStringProperty(r.getMatricola().getMatricola());
        });
        colAutore.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(null);
                }
            }
        });

        // Colonna Nota
        TableColumn<Revisione, String> colNota = new TableColumn<>("Nota");
        colNota.setPrefWidth(300);
        colNota.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getNota()));
        colNota.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    setGraphic(null);
                }
            }
        });

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
        btnEliminaCommento.setStyle(
            "-fx-background-color: #fee2e2; " +
            "-fx-text-fill: #991b1b; " +     
            "-fx-border-color: #fecaca; " +      
            "-fx-border-width: 1; " +
            "-fx-background-radius: 6; " +
            "-fx-border-radius: 6;"
        );

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
        
        String tipoStr = a.getTipo() != null ? a.getTipo().toString() : "—";
        lblTipo.setText(tipoStr);
        if ("Sviluppo".equalsIgnoreCase(tipoStr)) {
            lblTipo.setStyle("-fx-background-color: #e0e7ff; -fx-text-fill: #3730a3; -fx-border-color: #c7d2fe; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;");
        } else {
            lblTipo.setStyle("-fx-background-color: #f3e8ff; -fx-text-fill: #6b21a8; -fx-border-color: #e9d5ff; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;");
        }

        String statoStr = a.getStato() != null ? a.getStato().toString() : "—";
        lblStato.setText(prettifyStato(statoStr));
        if (statoStr.toLowerCase().contains("completata")) {
            lblStato.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-border-color: #a7f3d0; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;");
        } else if (statoStr.toLowerCase().contains("corso")) {
            lblStato.setStyle("-fx-background-color: #fef3c7; -fx-text-fill: #92400e; -fx-border-color: #fde68a; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;");
        } else {
            lblStato.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #1e40af; -fx-border-color: #bfdbfe; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;");
        }

        if (a.getDataScadenza() != null) {
            lblScadenza.setText("📅 " + a.getDataScadenza().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            lblScadenza.setText("📅 Nessuna scadenza");
        }
    }

    private String prettifyStato(String stato) {
        return stato.replace("_", " ");
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
    
    public void nascondiGestioneMembri() {
        if (btnGestisciMembri != null) {
            btnGestisciMembri.setVisible(false);
            btnGestisciMembri.setManaged(false);
        }
    }
    
    public void aggiornaLabelStato(String nuovoStato) {
        if (lblStato != null) {
            lblStato.setText(prettifyStato(nuovoStato));
            if (nuovoStato.toLowerCase().contains("completata")) {
                lblStato.setStyle("-fx-background-color: #d1fae5; -fx-text-fill: #065f46; -fx-border-color: #a7f3d0; -fx-padding: 3 10; -fx-background-radius: 12; -fx-border-radius: 12;");
            }
        }
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
    public Button getBtnGestisciMembri() { return btnGestisciMembri; }
    public Button getBtnCambiaStato() { return btnCambiaStato; }
}