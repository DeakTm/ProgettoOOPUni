package boundary.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;

public class MainView {

    // COMPONENTI PRINCIPALI
    private BorderPane root;
    
    // Sidebar (Solo le voci richieste)
    private HBox itemDashboard;
    private HBox itemProgetti;
    private HBox itemAttivita;
    
    // Topbar
    private TextField searchField;
    private Button btnNotif;
    private Label lblAvatar;
    
    // Dashboard (Home)
    private Label lblBenvenuto;
    private Button btnVaiAiProgetti;

    // TabPane centrale
    private TabPane tabPane;
    
    // Tab Report e Metriche (Mantenuti i campi nel caso servano in futuro)
    private Label statTotale;
    private Label statCompletate;
    private Label statInCorso;
    private Label statSviluppo;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;

    public MainView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        // --- SIDEBAR MINIMALE ---
        VBox sidebar = new VBox(20); // Spaziatura generosa tra logo e menu
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);

        HBox brandBox = createBrandBox();
        
        VBox navMenu = new VBox(4);
        navMenu.getStyleClass().add("sidebar__nav");
        // Modificato in "Home" con un'icona adatta
        itemDashboard = createNavItem("🏠", "Home", true);
        itemProgetti = createNavItem("📁", "Progetti", false);
        itemAttivita = createNavItem("✓", "Attività", false);
        
        navMenu.getChildren().addAll(itemDashboard, itemProgetti, itemAttivita);

        sidebar.getChildren().addAll(brandBox, navMenu);
        root.setLeft(sidebar);

        // --- TOPBAR ---
        HBox topbar = createTopbar();
        root.setTop(topbar);

        // --- CENTRO: TabPane ---
        this.tabPane = new TabPane();
        tabPane.getStyleClass().add("app-main");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabDashboard = createTabDashboard();
        Tab tabReport = createTabReport(); // Mantenuto nascosto nel tabpane per eventuali usi futuri

        tabPane.getTabs().addAll(tabDashboard, tabReport);
        root.setCenter(tabPane);
    }

    // CREAZIONE TAB DASHBOARD (Home)
    private Tab createTabDashboard() {
        Tab tab = new Tab("Home");
        VBox content = new VBox(32);
        content.getStyleClass().add("app-content");
        content.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        // --- INTESTAZIONE BENVENUTO CON AVATAR ---
        HBox welcomeBox = new HBox(20);
        welcomeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        StackPane pfp = new StackPane();
        pfp.getStyleClass().add("avatar");
        pfp.setPrefSize(60, 60);
        lblAvatar = new Label("UT");
        lblAvatar.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        pfp.getChildren().add(lblAvatar);

        VBox textVBox = new VBox(4);
        Label title = new Label("UninaTaskBoard");
        title.getStyleClass().add("page-title");
        
        lblBenvenuto = new Label("Benvenuto");
        lblBenvenuto.getStyleClass().add("page-subtitle");
        textVBox.getChildren().addAll(title, lblBenvenuto);

        welcomeBox.getChildren().addAll(pfp, textVBox);

        // --- CARD CENTRALE "I TUOI PROGETTI" ---
        VBox projectCard = new VBox(16);
        projectCard.getStyleClass().add("chart-card");
        projectCard.setMaxWidth(500);
        projectCard.setPadding(new Insets(24));

        Label cardTitle = new Label("I tuoi Progetti");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label cardDesc = new Label("Accedi all'elenco completo dei progetti assegnati e monitora lo stato delle attività.");
        cardDesc.getStyleClass().add("text-muted");

        btnVaiAiProgetti = new Button("Visualizza i tuoi Progetti →");
        btnVaiAiProgetti.getStyleClass().addAll("button", "btn--primary");

        projectCard.getChildren().addAll(cardTitle, cardDesc, btnVaiAiProgetti);

        content.getChildren().addAll(welcomeBox, new Separator(), projectCard);
        tab.setContent(content);
        return tab;
    }

    // CREAZIONE TAB REPORT (Nascosto ma disponibile)
    private Tab createTabReport() {
        Tab tab = new Tab("Report e Metriche");
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(24);
        content.getStyleClass().add("app-content");

        VBox header = new VBox(8);
        Label title = new Label("Report di Progetto");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Panoramica avanzamento e metriche di collaborazione");
        sub.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, sub);

        GridPane statGrid = new GridPane();
        statGrid.setHgap(20);
        statGrid.setVgap(20);
        
        statTotale = new Label("0");
        statCompletate = new Label("0");
        statInCorso = new Label("0");
        statSviluppo = new Label("0");
        
        statGrid.add(createStatCard("Totale Attività", statTotale), 0, 0);
        statGrid.add(createStatCard("Completate", statCompletate), 1, 0);
        statGrid.add(createStatCard("In Corso", statInCorso), 2, 0);
        statGrid.add(createStatCard("Attività di Sviluppo", statSviluppo), 3, 0);

        GridPane chartGrid = new GridPane();
        chartGrid.setHgap(20);
        chartGrid.setVgap(20);
        
        pieChart = new PieChart();
        pieChart.setPrefHeight(300);
        VBox pieCard = createChartCard("Distribuzione per Stato", pieChart);
        
        barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.setPrefHeight(300);
        VBox barCard = createChartCard("Attività Completate per Membro", barChart);

        chartGrid.add(pieCard, 0, 0);
        chartGrid.add(barCard, 1, 0);

        content.getChildren().addAll(header, statGrid, chartGrid);
        scroll.setContent(content);
        tab.setContent(scroll);
        return tab;
    }

    // METODI DI SUPPORTO
    private HBox createBrandBox() {
        HBox brandBox = new HBox();
        brandBox.getStyleClass().add("sidebar__brand");
        StackPane logoMark = new StackPane();
        logoMark.getStyleClass().add("sidebar__logo-mark");
        Label logoText = new Label("UT");
        logoMark.getChildren().add(logoText);
        Label brandName = new Label("UninaTaskBoard");
        brandName.getStyleClass().add("sidebar__brand-name");
        brandBox.getChildren().addAll(logoMark, brandName);
        return brandBox;
    }

    private HBox createNavItem(String icon, String labelText, boolean isActive) {
        HBox item = new HBox(12);
        item.getStyleClass().add("sidebar__nav-item");
        if (isActive) item.getStyleClass().add("is-active");
        Label iconLbl = new Label(icon);
        iconLbl.getStyleClass().add("sidebar__nav-icon");
        Label textLbl = new Label(labelText);
        textLbl.getStyleClass().add("sidebar__nav-label");
        item.getChildren().addAll(iconLbl, textLbl);
        return item;
    }

    private HBox createTopbar() {
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox searchBox = new HBox();
        searchBox.getStyleClass().add("topbar__search");
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("text-muted");
        searchField = new TextField();
        searchField.setPromptText("Cerca attività, progetti o membri...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchBox.setMaxWidth(360);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox actions = new HBox(16);
        actions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        btnNotif = new Button("🔔");
        btnNotif.getStyleClass().add("topbar__icon-btn");
        
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar");
        Label avatarLabel = new Label("UT");
        avatar.getChildren().add(avatarLabel);
        
        actions.getChildren().addAll(btnNotif, avatar);
        topbar.getChildren().addAll(searchBox, spacer, actions);
        return topbar;
    }

    private VBox createStatCard(String label, Label valueLabel) {
        VBox card = new VBox(3);
        card.getStyleClass().add("stat-card");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-card__label");
        valueLabel.getStyleClass().add("stat-card__value");
        card.getChildren().addAll(lbl, valueLabel);
        return card;
    }

    private VBox createChartCard(String title, javafx.scene.Node chart) {
        VBox card = new VBox(12);
        card.getStyleClass().add("chart-card");
        HBox header = new HBox();
        header.getStyleClass().add("chart-card__header");
        Label titleLbl = new Label(title);
        titleLbl.getStyleClass().add("card__title");
        header.getChildren().add(titleLbl);
        card.getChildren().addAll(header, chart);
        return card;
    }

    public void mostraProgettiView(ProgettiView progettiView) {
        root.setCenter(progettiView.getRoot());
    }

    public void mostraTabPane() {
        root.setCenter(tabPane);
    }

    public void setUtenteLoggato(String matricola) {
        if (lblBenvenuto != null) {
            lblBenvenuto.setText("Benvenuto, " + matricola);
        }
        if (lblAvatar != null && matricola != null && !matricola.isEmpty()) {
            String initials = matricola.length() >= 2 ? matricola.substring(0, 2).toUpperCase() : matricola.toUpperCase();
            lblAvatar.setText(initials);
        }
    }
    
    public void mostraAttivitaView(AttivitaView attivitaView) {
        root.setCenter(attivitaView.getRoot());
    }

    // GETTER PER IL CONTROLLER
    public BorderPane getRoot() { return root; }
    public HBox getItemDashboard() { return itemDashboard; }
    public HBox getItemProgetti() { return itemProgetti; }
    public HBox getItemAttivita() { return itemAttivita; }
    public TextField getSearchField() { return searchField; }
    public Button getBtnNotif() { return btnNotif; }
    public Button getBtnVaiAiProgetti() { return btnVaiAiProgetti; }
    public TabPane getTabPane() { return tabPane; }
    
    // I getter per i componenti del report non sono più essenziali, 
    // ma li manteniamo in caso tu voglia integrarli successivamente altrove.
    public Label getStatTotale() { return statTotale; }
    public Label getStatCompletate() { return statCompletate; }
    public Label getStatInCorso() { return statInCorso; }
    public Label getStatSviluppo() { return statSviluppo; }
    public PieChart getPieChart() { return pieChart; }
    public BarChart<String, Number> getBarChart() { return barChart; }
}