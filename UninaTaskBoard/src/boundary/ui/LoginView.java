package boundary.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {

    private StackPane root;

    private TextField txtMatricola;
    private PasswordField txtPassword;
    private Button btnAccedi;

    public LoginView() {
        root = new StackPane();
        root.getStyleClass().add("app-shell"); 
        root.setAlignment(Pos.CENTER);

        VBox loginCard = new VBox(24);
        loginCard.setMaxWidth(400);
        loginCard.setPadding(new Insets(40));
        loginCard.getStyleClass().add("app-content"); 
        loginCard.setAlignment(Pos.CENTER);
        
        HBox brandBox = new HBox(12);
        brandBox.setAlignment(Pos.CENTER);
        StackPane logoMark = new StackPane();
        logoMark.getStyleClass().add("sidebar__logo-mark");
        Label logoText = new Label("UT");
        logoMark.getChildren().add(logoText);
        Label brandName = new Label("UninaTaskBoard");
        brandName.getStyleClass().add("sidebar__brand-name");
        brandName.setStyle("-fx-text-fill: -color-navy-900;");   
        brandBox.getChildren().addAll(logoMark, brandName);

        VBox headerBox = new VBox(8);
        headerBox.setAlignment(Pos.CENTER);
        Label title = new Label("Bentornato");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("Accedi per gestire i tuoi progetti");
        subtitle.getStyleClass().add("page-subtitle");
        headerBox.getChildren().addAll(title, subtitle);

        VBox formBox = new VBox(16);
        
        VBox matricolaBox = new VBox(4);
        Label lblMatricola = new Label("Matricola");
        txtMatricola = new TextField();
        txtMatricola.setPromptText("Es. M12345");
        txtMatricola.getStyleClass().add("text-field");
        matricolaBox.getChildren().addAll(lblMatricola, txtMatricola);

        VBox passwordBox = new VBox(4);
        Label lblPassword = new Label("Password");
        txtPassword = new PasswordField();
        txtPassword.setPromptText("Inserisci la tua password");
        txtPassword.getStyleClass().add("text-field");
        passwordBox.getChildren().addAll(lblPassword, txtPassword);

        formBox.getChildren().addAll(matricolaBox, passwordBox);

        btnAccedi = new Button("Accedi");
        btnAccedi.setMaxWidth(Double.MAX_VALUE); // Il bottone si allunga a tutta larghezza
        btnAccedi.getStyleClass().addAll("button", "btn--primary");

        
        loginCard.getChildren().addAll(brandBox, headerBox, formBox, btnAccedi);
        root.getChildren().add(loginCard);
    }

    public StackPane getRoot() { return root; }
    public TextField getTxtMatricola() { return txtMatricola; }
    public PasswordField getTxtPassword() { return txtPassword; }
    public Button getBtnAccedi() { return btnAccedi; }
}