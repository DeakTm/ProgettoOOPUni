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
    private ComboBox<String> cmbStudenteAssegnato; 

    private Button btnSalva;
    private Button btnAnnulla;

    public FormAttivitaView() {

        root = new VBox(18);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("card");
        root.setPrefWidth(460);

        VBox headerBox = new VBox(4);
        Label title = new Label("Nuova Attività");
        title.getStyleClass().add("section-title");
        
        Label subtitle = new Label("Definisci i dettagli e assegna l'attività a un membro");
        subtitle.getStyleClass().add("text-secondary");
        subtitle.setStyle("-fx-font-size: 12px;");
        headerBox.getChildren().addAll(title, subtitle);

        Separator sep = new Separator();
        sep.getStyleClass().add("divider");

        // Contenitore dei campi del form
        VBox formFieldsBox = new VBox(14);

        //  Descrizione 
        VBox descBox = new VBox(6);
        Label lblDescrizione = new Label("Descrizione Attività");
        lblDescrizione.getStyleClass().add("form-label");
        
        txtDescrizione = new TextArea();
        txtDescrizione.setPrefRowCount(3);
        txtDescrizione.setMaxHeight(85);
        txtDescrizione.setPromptText("Descrivi dettagliatamente cosa c'è da fare...");
        txtDescrizione.getStyleClass().add("text-area");

        descBox.getChildren().addAll(lblDescrizione, txtDescrizione);

        //  Tipo Attività e Data Scadenza 
        HBox row1 = new HBox(14);
        row1.setAlignment(Pos.CENTER_LEFT);

        VBox tipoBox = new VBox(6);
        Label lblTipo = new Label("Tipo Attività");
        lblTipo.getStyleClass().add("form-label");
        cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll(TipoAttivita.values());
        cmbTipo.setPromptText("Seleziona tipo...");
        cmbTipo.setMaxWidth(Double.MAX_VALUE);
        tipoBox.getChildren().addAll(lblTipo, cmbTipo);
        HBox.setHgrow(tipoBox, Priority.ALWAYS);

        VBox scadenzaBox = new VBox(6);
        Label lblScadenza = new Label("Data Scadenza");
        lblScadenza.getStyleClass().add("form-label");
        dataScadenza = new DatePicker();
        dataScadenza.setPromptText("gg/mm/aaaa");
        dataScadenza.setMaxWidth(Double.MAX_VALUE);
        scadenzaBox.getChildren().addAll(lblScadenza, dataScadenza);
        HBox.setHgrow(scadenzaBox, Priority.ALWAYS);

        row1.getChildren().addAll(tipoBox, scadenzaBox);

        VBox studenteBox = new VBox(6);
        Label lblMatricola = new Label("Assegna a Membro");
        lblMatricola.getStyleClass().add("form-label");
        cmbStudenteAssegnato = new ComboBox<>();
        cmbStudenteAssegnato.setPromptText("Seleziona membro del progetto...");
        cmbStudenteAssegnato.setMaxWidth(Double.MAX_VALUE);
        studenteBox.getChildren().addAll(lblMatricola, cmbStudenteAssegnato);

        formFieldsBox.getChildren().addAll(descBox, row1, studenteBox);

        // Footer 
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        btnAnnulla = new Button("Annulla");
        btnAnnulla.getStyleClass().addAll("button", "btn--secondary");

        btnSalva = new Button("Crea Attività");
        btnSalva.getStyleClass().addAll("button", "btn--primary");

        buttonBox.getChildren().addAll(btnAnnulla, btnSalva);

        root.getChildren().addAll(headerBox, sep, formFieldsBox, buttonBox);
    }

    public VBox getRoot() { return root; }
    public TextArea getTxtDescrizione() { return txtDescrizione; }
    public ComboBox<TipoAttivita> getCmbTipo() { return cmbTipo; }
    public DatePicker getDataScadenza() { return dataScadenza; }
    public ComboBox<String> getCmbStudenteAssegnato() { return cmbStudenteAssegnato; }
    public Button getBtnSalva() { return btnSalva; }
    public Button getBtnAnnulla() { return btnAnnulla; }
}