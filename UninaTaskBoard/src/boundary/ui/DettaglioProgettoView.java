package boundary.ui;

import entity.Progetto;
import entity.Studente;
import entity.Attivita;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class DettaglioProgettoView {

    private BorderPane root;
    private Progetto progetto;

    private Button btnTornaIndietro;
    private Button btnAggiungiMembro;
    private Button btnNuovaAttivita;
    private Button btnGeneraReport;

    private Label lblStatTotale;
    private Label lblStatCompletate;
    private Label lblStatInCorso;

    private VBox containerMembri;
    private VBox containerAttivita;
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

        VBox headerBox = createHeader();
        GridPane statsGrid = createStatsGrid();
        VBox membriBox = createMembriSection();
        VBox attivitaBox = createAttivitaSection(); // Nuova sezione attività

        mainContent.getChildren().addAll(headerBox, statsGrid, membriBox, attivitaBox);
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

        btnGeneraReport = new Button("📄 Genera Report");
        btnGeneraReport.getStyleClass().addAll("button", "btn--secondary");

        btnNuovaAttivita = new Button("+ Nuova Attività");
        btnNuovaAttivita.getStyleClass().addAll("button", "btn--primary");

        titleBox.getChildren().addAll(lblTitolo, lblStato, spacer, btnGeneraReport, btnNuovaAttivita);

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
        lblSectionTitle.getStyleClass().add("section-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnAggiungiMembro = new Button("+ Aggiungi Studenti");
        btnAggiungiMembro.getStyleClass().addAll("button", "btn--secondary");

        header.getChildren().addAll(lblSectionTitle, spacer, btnAggiungiMembro);

        containerMembri = new VBox(10);

        card.getChildren().addAll(header, new Separator(), containerMembri);
        return card;
    }

    // --- NUOVA SEZIONE: ATTIVITA' DEL PROGETTO ---
    private VBox createAttivitaSection() {
        VBox card = new VBox(16);
        card.getStyleClass().add("chart-card");
        card.setPadding(new Insets(24));

        Label lblSectionTitle = new Label("Attività del Progetto");
        lblSectionTitle.getStyleClass().add("section-title");

        containerAttivita = new VBox(10);

        card.getChildren().addAll(lblSectionTitle, new Separator(), containerAttivita);
        return card;
    }

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
            item.getStyleClass().add("card");

            StackPane avatar = new StackPane();
            avatar.getStyleClass().add("avatar");
            Label lblInit = new Label(s.getMatricola().substring(0, Math.min(2, s.getMatricola().length())).toUpperCase());
            avatar.getChildren().add(lblInit);

            VBox info = new VBox(2);
            Label lblNome = new Label(s.getNome() + " " + s.getCognome());
            lblNome.getStyleClass().add("form-label");
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

    // --- POPOLAMENTO DINAMICO DELLE ATTIVITA' ---
    public void mostraAttivita(List<Attivita> attivitaList) {
        containerAttivita.getChildren().clear();

        if (attivitaList == null || attivitaList.isEmpty()) {
            Label vuoto = new Label("Nessuna attività presente. Clicca su '+ Nuova Attività' per iniziare.");
            vuoto.getStyleClass().add("text-muted");
            containerAttivita.getChildren().add(vuoto);
            return;
        }

        for (Attivita a : attivitaList) {
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(12, 16, 12, 16));
            item.getStyleClass().add("card");

            VBox infoBox = new VBox(4);
            Label lblDesc = new Label(a.getDescrizione() != null ? a.getDescrizione() : "Senza descrizione");
            lblDesc.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            Label lblTipo = new Label("Tipologia: " + (a.getTipo() != null ? a.getTipo().toString() : "N/D"));
            lblTipo.getStyleClass().add("text-muted");
            infoBox.getChildren().addAll(lblDesc, lblTipo);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox statusBox = new VBox(6);
            statusBox.setAlignment(Pos.CENTER_RIGHT);
            
            Label lblStato = new Label(a.getStato() != null ? a.getStato().toString() : "Nuova");
            lblStato.getStyleClass().addAll("badge", "badge--in-corso");
            
            Label lblScadenza = new Label("Scadenza: " + (a.getDataScadenza() != null ? a.getDataScadenza().toString() : "-"));
            lblScadenza.setStyle("-fx-font-size: 11px;");
            lblScadenza.getStyleClass().add("text-muted");
            
            statusBox.getChildren().addAll(lblStato, lblScadenza);

            item.getChildren().addAll(infoBox, spacer, statusBox);
            containerAttivita.getChildren().add(item);
        }
    }

    public List<Studente> mostraDialogAggiungiMembri(List<Studente> studentiDisponibili) {
        Dialog<List<Studente>> dialog = new Dialog<>();
        dialog.setTitle("Aggiungi Membri al Progetto");
        dialog.setHeaderText("Cerca e seleziona gli studenti da aggiungere:");

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStyleClass().add("dialog-pane");

        ButtonType btnAggiungiType = new ButtonType("Aggiungi Selezionati", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(btnAggiungiType, ButtonType.CANCEL);

        TextField searchField = new TextField();
        searchField.setPromptText("🔍 Cerca per nome, cognome o matricola...");
        searchField.getStyleClass().add("text-field");

        ObservableList<Studente> observableList = FXCollections.observableArrayList(studentiDisponibili);
        FilteredList<Studente> filteredData = new FilteredList<>(observableList, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(studente -> {
                if (newVal == null || newVal.trim().isEmpty()) { return true; }
                String filter = newVal.toLowerCase().trim();
                return studente.getNome().toLowerCase().contains(filter) ||
                       studente.getCognome().toLowerCase().contains(filter) ||
                       studente.getMatricola().toLowerCase().contains(filter);
            });
        });

        ListView<Studente> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(filteredData);
        listView.setPrefHeight(260);
        listView.getStyleClass().add("table-view");

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

        VBox content = new VBox(14);
        Label hint = new Label("💡 Tieni premuto CTRL per selezionare più studenti.");
        hint.getStyleClass().add("text-muted");
        hint.setStyle("-fx-font-size: 12px;");

        content.getChildren().addAll(searchField, listView, hint);
        content.setPadding(new Insets(20));
        content.setPrefWidth(420);
        
        dialogPane.setContent(content);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAggiungiType) {
                return listView.getSelectionModel().getSelectedItems();
            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    public void aggiornaStatistiche(int totali, int completate, int inCorso) {
        if (lblStatTotale != null) lblStatTotale.setText(String.valueOf(totali));
        if (lblStatCompletate != null) lblStatCompletate.setText(String.valueOf(completate));
        if (lblStatInCorso != null) lblStatInCorso.setText(String.valueOf(inCorso));
    }

    public void mostraReportDialog(int totale, int completate, int inCorso, int nonIniziate, int sviluppo, double mediaRevisioni, Map<String, Integer> completatePerMembro) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Report di Progetto");
        dialog.setHeaderText("Report Completo: " + progetto.getNome());

        DialogPane dialogPane = dialog.getDialogPane();
        
        ButtonType btnChiudiType = new ButtonType("Chiudi", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogPane.getButtonTypes().add(btnChiudiType);
        
        Node closeButton = dialogPane.lookupButton(btnChiudiType);
        if (closeButton != null) {
            closeButton.getStyleClass().addAll("button", "btn--secondary");
        }
        
        try {
            String css = getClass().getResource("/css/style.css").toExternalForm();
            dialogPane.getStylesheets().add(css);
            dialogPane.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        } catch (Exception ignored) {}

        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.setPrefWidth(750);
        content.getStyleClass().add("card");

        VBox datiTestuali = new VBox(10);
        datiTestuali.getChildren().addAll(
            creaRigaReport("Totale attività:", String.valueOf(totale)),
            new Separator(),
            creaRigaReport("Completate:", String.valueOf(completate)),
            creaRigaReport("In corso:", String.valueOf(inCorso)),
            creaRigaReport("Non iniziate:", String.valueOf(nonIniziate)),
            new Separator(),
            creaRigaReport("Di sviluppo:", String.valueOf(sviluppo)),
            creaRigaReport("Media revisioni per file di codice:", String.format("%.2f", mediaRevisioni))
        );

        HBox graficiBox = new HBox(20);
        graficiBox.setAlignment(Pos.CENTER);

        PieChart pieChart = new PieChart();
        pieChart.setTitle("Stato Attività");
        pieChart.getData().add(new PieChart.Data("Completate", completate));
        pieChart.getData().add(new PieChart.Data("In Corso", inCorso));
        pieChart.getData().add(new PieChart.Data("Non Iniziate", nonIniziate));
        pieChart.setPrefSize(320, 250);
        pieChart.setLegendSide(javafx.geometry.Side.BOTTOM);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Completate per Membro");
        barChart.setLegendVisible(false);
        barChart.setPrefSize(350, 250);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        if (completatePerMembro != null && !completatePerMembro.isEmpty()) {
            for (Map.Entry<String, Integer> entry : completatePerMembro.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
        } else {
            series.getData().add(new XYChart.Data<>("Nessun dato", 0));
        }
        barChart.getData().add(series);

        graficiBox.getChildren().addAll(pieChart, barChart);

        content.getChildren().addAll(datiTestuali, new Separator(), graficiBox);
        dialogPane.setContent(content);
        
        dialog.showAndWait();
    }

    private HBox creaRigaReport(String etichetta, String valore) {
        HBox row = new HBox();
        Label lblEtichetta = new Label(etichetta);
        lblEtichetta.getStyleClass().add("form-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label lblValore = new Label(valore);
        lblValore.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        row.getChildren().addAll(lblEtichetta, spacer, lblValore);
        return row;
    }

    public BorderPane getRoot() { return root; }
    public Button getBtnTornaIndietro() { return btnTornaIndietro; }
    public Button getBtnAggiungiMembro() { return btnAggiungiMembro; }
    public Button getBtnNuovaAttivita() { return btnNuovaAttivita; }
    public Button getBtnGeneraReport() { return btnGeneraReport; }
    public Label getLblStatTotale() { return lblStatTotale; }
    public Label getLblStatCompletate() { return lblStatCompletate; }
    public Label getLblStatInCorso() { return lblStatInCorso; }
    public void setOnRimuoviMembroClick(Consumer<Studente> listener) { this.onRimuoviMembroClick = listener; }
}