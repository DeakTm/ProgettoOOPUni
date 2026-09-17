package boundary.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;

import java.time.LocalDate;

public class FormProgettoView extends Dialog<LocalDate> {

    private DatePicker datePicker;

    public FormProgettoView() {
        setTitle("Nuovo Progetto");
        setHeaderText("Inserisci la scadenza del progetto");

        // Blocca la finestra principale finché non chiudi
        initModality(Modality.APPLICATION_MODAL);

        // Contenuto
        VBox content = new VBox(12);
        content.setPadding(new Insets(20));

        Label lbl = new Label("Scadenza:");
        datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now().plusMonths(1));
        datePicker.setPrefWidth(250);

        content.getChildren().addAll(lbl, datePicker);

        getDialogPane().setContent(content);

        // Bottoni
        ButtonType btnCrea = new ButtonType("Crea", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnAnnulla = new ButtonType("Annulla", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnCrea, btnAnnulla);

        // Converti il risultato
        setResultConverter(button -> {
            if (button == btnCrea) {
                return datePicker.getValue();
            }
            return null;
        });
    }
}