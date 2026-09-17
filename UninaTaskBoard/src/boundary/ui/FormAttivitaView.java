package boundary.ui;

import entity.enums.TipoAttivita;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FormAttivitaView {

    private VBox root;


    private TextArea txtDescrizione;
    private ComboBox<TipoAttivita> cmbTipo;
    private DatePicker dataScadenza;
    private ComboBox<String> cmbStudenteAssegnato; // <-- MODIFICATO: Da TextField a ComboBox

    private Button btnSalva;
    private Button btnAnnulla;

    public FormAttivitaView() {
        root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("app-content");


        Label title = new Label("Nuova Attività");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Aggiungi un'attività al progetto corrente");
        subtitle.getStyleClass().add("page-subtitle");
        
        VBox headerBox = new VBox(5, title, subtitle);

        // FORM
        GridPane grid = new GridPane();
        grid.setVgap(16);
        grid.setHgap(16);

        Label lblDescrizione = new Label("Descrizione:");
        txtDescrizione = new TextArea();
        txtDescrizione.setPrefRowCount(3); 
        txtDescrizione.setPromptText("Descrivi cosa c'è da fare...");
        txtDescrizione.getStyleClass().add("text-field");

        Label lblTipo = new Label("Tipo Attività:");
        cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll(TipoAttivita.values()); 
        cmbTipo.setPromptText("Seleziona tipo...");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        Label lblScadenza = new Label("Scadenza:");
        dataScadenza = new DatePicker();
        dataScadenza.setPromptText("gg/mm/aaaa");
        dataScadenza.setMaxWidth(Double.MAX_VALUE);


        Label lblMatricola = new Label("Assegna a:");
        cmbStudenteAssegnato = new ComboBox<>();
        cmbStudenteAssegnato.setPromptText("Seleziona membro del progetto...");
        cmbStudenteAssegnato.setMaxWidth(Double.MAX_VALUE);

        grid.add(lblDescrizione, 0, 0);
        grid.add(txtDescrizione, 1, 0);
        grid.add(lblTipo, 0, 1);
        grid.add(cmbTipo, 1, 1);
        grid.add(lblScadenza, 0, 2);
        grid.add(dataScadenza, 1, 2);
        grid.add(lblMatricola, 0, 3);
        grid.add(cmbStudenteAssegnato, 1, 3); 

        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS); 
        grid.getColumnConstraints().addAll(col1, col2);

        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        
        btnAnnulla = new Button("Annulla");
        btnAnnulla.getStyleClass().add("button");

        btnSalva = new Button("Salva Attività");
        btnSalva.getStyleClass().addAll("button", "btn--primary");

        buttonBox.getChildren().addAll(btnAnnulla, btnSalva);

        root.getChildren().addAll(headerBox, grid, buttonBox);
    }


    public VBox getRoot() { return root; }
    public TextArea getTxtDescrizione() { return txtDescrizione; }
    public ComboBox<TipoAttivita> getCmbTipo() { return cmbTipo; }
    public DatePicker getDataScadenza() { return dataScadenza; }
    public ComboBox<String> getCmbStudenteAssegnato() { return cmbStudenteAssegnato; }
    public Button getBtnSalva() { return btnSalva; }
    public Button getBtnAnnulla() { return btnAnnulla; }
}