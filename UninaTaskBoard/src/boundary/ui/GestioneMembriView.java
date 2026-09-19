package boundary.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestioneMembriView {

    private Stage stage;
    private BorderPane root;

    private ListView<String> listaStudenti;
    private Map<String, CheckBox> checkboxMap = new HashMap<>();

    private Button btnSalva;
    private Button btnAnnulla;
    private Label lblTitolo;

    public GestioneMembriView(Stage owner) {
        stage = new Stage();
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setTitle("Gestione Membri Attività");

        root = new BorderPane();
        root.getStyleClass().add("app-shell");
        root.setPadding(new Insets(24));

        lblTitolo = new Label("Seleziona i membri da assegnare");
        lblTitolo.getStyleClass().add("page-title");

        VBox header = new VBox(6);
        Label sub = new Label("Spunta gli studenti del progetto che vuoi assegnare a questa attività");
        sub.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(lblTitolo, sub);
        root.setTop(header);

        listaStudenti = new ListView<>();
        listaStudenti.setPlaceholder(new Label("Nessuno studente nel progetto"));
        listaStudenti.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String matricola, boolean empty) {
                super.updateItem(matricola, empty);
                if (empty || matricola == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                CheckBox cb = checkboxMap.get(matricola);
                if (cb == null) {
                    cb = new CheckBox(matricola);
                    cb.setStyle("-fx-font-size: 13px;");
                    checkboxMap.put(matricola, cb);
                }
                setGraphic(cb);
                setText(null);
            }
        });

        VBox.setVgrow(listaStudenti, Priority.ALWAYS);
        BorderPane.setMargin(listaStudenti, new Insets(16, 0, 16, 0));
        root.setCenter(listaStudenti);

        btnSalva = new Button("💾 Salva");
        btnSalva.getStyleClass().addAll("button", "btn--primary");

        btnAnnulla = new Button("Annulla");
        btnAnnulla.getStyleClass().add("button");

        HBox actions = new HBox(12);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        actions.getChildren().addAll(btnAnnulla, btnSalva);
        root.setBottom(actions);
    }

    public void caricaStudenti(List<String> tuttiGliStudenti, List<String> studentiAssegnati) {
        listaStudenti.getItems().clear();
        checkboxMap.clear();

        if (tuttiGliStudenti == null) return;

        for (String matricola : tuttiGliStudenti) {
            listaStudenti.getItems().add(matricola);
            CheckBox cb = new CheckBox(matricola);
            cb.setStyle("-fx-font-size: 13px;");
            cb.setSelected(studentiAssegnati != null && studentiAssegnati.contains(matricola));
            checkboxMap.put(matricola, cb);
        }
    }

    public List<String> getStudentiSelezionati() {
        List<String> selezionati = new ArrayList<>();
        for (Map.Entry<String, CheckBox> entry : checkboxMap.entrySet()) {
            if (entry.getValue().isSelected()) {
                selezionati.add(entry.getKey());
            }
        }
        return selezionati;
    }

    public void mostra() {
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 500, 600);
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
    public Button getBtnSalva() { return btnSalva; }
    public Button getBtnAnnulla() { return btnAnnulla; }
    public BorderPane getRoot() { return root; }
}