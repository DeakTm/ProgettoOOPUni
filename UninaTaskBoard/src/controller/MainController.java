package controller;

public class MainController {

<<<<<<< Updated upstream
}
=======
    private MainView view;
    private Stage stage;
    private String matricolaLoggata;

    public MainController(Stage stage, String matricolaLoggata) {
        this.stage = stage;
        this.matricolaLoggata = matricolaLoggata;

        this.view = new MainView();

        Scene scene = new Scene(view.getRoot(), 1200, 800);
        this.stage.setScene(scene);

        inizializzaEventi();
    }

    private void inizializzaEventi() {
        
        view.getBtnNuovaAttivita().setOnAction(event -> {
            System.out.println("Hai cliccato Nuova Attività! Presto si aprirà il form.");

        });

        view.getItemDashboard().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemDashboard().getStyleClass().add("is-active");
            System.out.println("Navigazione: Dashboard");
        });

        view.getItemProgetti().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemProgetti().getStyleClass().add("is-active");
            System.out.println("Navigazione: Progetti");
        });

        view.getItemAttivita().setOnMouseClicked(event -> {
            rimuoviAttivi();
            view.getItemAttivita().getStyleClass().add("is-active");
            System.out.println("Navigazione: Attività");
        });
        
        // Clic sulla campanella
        view.getBtnNotif().setOnAction(event -> {
            System.out.println("Apertura pannello notifiche per: " + matricolaLoggata);
        });
    }

    private void rimuoviAttivi() {
        view.getItemDashboard().getStyleClass().remove("is-active");
        view.getItemProgetti().getStyleClass().remove("is-active");
        view.getItemAttivita().getStyleClass().remove("is-active");
        view.getItemReport().getStyleClass().remove("is-active");
        view.getItemNotifiche().getStyleClass().remove("is-active");
    }
}
>>>>>>> Stashed changes
