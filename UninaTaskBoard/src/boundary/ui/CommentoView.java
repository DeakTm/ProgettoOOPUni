package boundary.ui;

import entity.Commento;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class CommentoView {

    private BorderPane root;

    private Label lblTitolo;
    private Label lblSottotitolo;
    private Label lblTotale;
    private TextField searchField;
    private Button btnRicarica;

    private TableView<Commento> tabellaCommenti;

    public CommentoView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        VBox header = createHeader();
        root.setTop(header);

        tabellaCommenti = createTabella();
        VBox.setVgrow(tabellaCommenti, Priority.ALWAYS);

        VBox content = new VBox(tabellaCommenti);
        content.setPadding(new Insets(0, 24, 24, 24));
        VBox.setVgrow(tabellaCommenti, Priority.ALWAYS);

        root.setCenter(content);
    }

    private VBox createHeader() {
        VBox header = new VBox(20);
        header.setPadding(new Insets(24, 24, 16, 24));

        VBox titles = new VBox(6);
        HBox titleRow = new HBox(12);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        lblTitolo = new Label("I Miei Commenti");
        lblTitolo.getStyleClass().add("page-title");

        lblTotale = new Label("0");
        lblTotale.getStyleClass().add("badge");

        titleRow.getChildren().addAll(lblTitolo, lblTotale);

        lblSottotitolo = new Label("Tutti i commenti che hai scritto, ordinati dal più recente");
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
        searchField.setPromptText("Cerca commento o attività...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchBox.setMaxWidth(340);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        btnRicarica = new Button("🔄 Ricarica");
        btnRicarica.getStyleClass().add("button");

        toolbar.getChildren().addAll(searchBox, spacer, btnRicarica);

        header.getChildren().addAll(titles, toolbar);
        return header;
    }

    private TableView<Commento> createTabella() {
        TableView<Commento> table = new TableView<>();
        table.getStyleClass().add("table-view");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("Non hai ancora scritto commenti"));

        // Colonna Data
        TableColumn<Commento, String> colData = new TableColumn<>("Data");
        colData.setPrefWidth(160);
        colData.setCellValueFactory(c -> {
            if (c.getValue().getDataCommento() == null) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
            return new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDataCommento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        });

        // Colonna Attività
        TableColumn<Commento, String> colAttivita = new TableColumn<>("Attività");
        colAttivita.setPrefWidth(280);
        colAttivita.setCellValueFactory(c -> {
            Commento comm = c.getValue();
            if (comm.getId_attivita() == null) {
                return new javafx.beans.property.SimpleStringProperty("—");
            }
            String desc = comm.getId_attivita().getDescrizione();
            String id = String.valueOf(comm.getId_attivita().getId());
            return new javafx.beans.property.SimpleStringProperty("#" + id + " - " + desc);
        });

        // Colonna Testo
        TableColumn<Commento, String> colTesto = new TableColumn<>("Commento");
        colTesto.setPrefWidth(500);
        colTesto.setCellValueFactory(c ->
            new javafx.beans.property.SimpleStringProperty(c.getValue().getTesto()));
        colTesto.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String testo, boolean empty) {
                super.updateItem(testo, empty);
                if (empty || testo == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label lbl = new Label(testo);
                lbl.setWrapText(true);
                lbl.setStyle("-fx-font-size: 12px;");
                setGraphic(lbl);
                setText(null);
            }
        });

        table.getColumns().addAll(colData, colAttivita, colTesto);
        return table;
    }

    public void mostraCommenti(List<Commento> commenti) {
        tabellaCommenti.getItems().clear();
        if (commenti != null) {
            tabellaCommenti.getItems().addAll(commenti);
            lblTotale.setText(String.valueOf(commenti.size()));
        } else {
            lblTotale.setText("0");
        }
    }

    public Commento getCommentoSelezionato() {
        return tabellaCommenti.getSelectionModel().getSelectedItem();
    }

    public BorderPane getRoot() { return root; }
    public TableView<Commento> getTabellaCommenti() { return tabellaCommenti; }
    public TextField getSearchField() { return searchField; }
    public Button getBtnRicarica() { return btnRicarica; }
}