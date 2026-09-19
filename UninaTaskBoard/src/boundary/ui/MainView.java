package boundary.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;

public class MainView {

    private BorderPane root;
    
    // Sidebar
    private HBox itemDashboard;
    private HBox itemProgetti;
    private HBox itemAttivita;
    private HBox itemCommenti;
    
    // Topbar
    private Button btnLogout;
    private Label lblAvatarTopbar;
    private Label lblNomeTopbar;
    
    // Dashboard (Home)
    private Label lblBenvenuto;
    private Button btnVaiAiProgetti;
    private Label lblAvatarHome;

    // TabPane centrale
    private TabPane tabPane;
    
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
        VBox sidebar = new VBox(20);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);

        HBox brandBox = createBrandBox();
        
        VBox navMenu = new VBox(4);
        navMenu.getStyleClass().add("sidebar__nav");
        itemDashboard = createNavItem("🏠", "Home", true);
        itemProgetti = createNavItem("📁", "Progetti", false);
        itemAttivita = createNavItem("✓", "Attività", false);
        itemCommenti = createNavItem("💬", "Commenti", false);
        
        navMenu.getChildren().addAll(itemDashboard, itemProgetti, itemAttivita, itemCommenti);

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
        Tab tabReport = createTabReport();

        tabPane.getTabs().addAll(tabDashboard, tabReport);
        root.setCenter(tabPane);
    }

    private Tab createTabDashboard() {
        Tab tab = new Tab("Home");
        VBox content = new VBox(32);
        content.getStyleClass().add("app-content");
        content.setAlignment(javafx.geometry.Pos.TOP_LEFT);

        HBox welcomeBox = new HBox(20);
        welcomeBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        StackPane pfp = new StackPane();
        pfp.getStyleClass().add("avatar");
        pfp.setPrefSize(60, 60);
        lblAvatarHome = new Label("UT");
        lblAvatarHome.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        pfp.getChildren().add(lblAvatarHome);

        VBox textVBox = new VBox(4);
        Label title = new Label("UninaTaskBoard");
        title.getStyleClass().add("page-title");
        
        lblBenvenuto = new Label("Benvenuto");
        lblBenvenuto.getStyleClass().add("page-subtitle");
        textVBox.getChildren().addAll(title, lblBenvenuto);

        welcomeBox.getChildren().addAll(pfp, textVBox);

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
        topbar.setPrefHeight(56);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(12);
        userBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        StackPane miniAvatar = new StackPane();
        miniAvatar.getStyleClass().add("avatar");
        miniAvatar.setPrefSize(32, 32);
        lblAvatarTopbar = new Label("UT");
        lblAvatarTopbar.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        miniAvatar.getChildren().add(lblAvatarTopbar);

        lblNomeTopbar = new Label("Utente");
        lblNomeTopbar.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Separator sep = new Separator(javafx.geometry.Orientation.VERTICAL);
        sep.setPrefHeight(16);

        btnLogout = new Button("🚪 Esci");
        btnLogout.getStyleClass().addAll("button", "btn--secondary");

        userBox.getChildren().addAll(miniAvatar, lblNomeTopbar, sep, btnLogout);

        topbar.getChildren().addAll(spacer, userBox);
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
        if (lblNomeTopbar != null) {
            lblNomeTopbar.setText(matricola);
        }
        
        String initials = (matricola != null && matricola.length() >= 2) 
            ? matricola.substring(0, 2).toUpperCase() 
            : (matricola != null ? matricola.toUpperCase() : "UT");

        if (lblAvatarHome != null) lblAvatarHome.setText(initials);
        if (lblAvatarTopbar != null) lblAvatarTopbar.setText(initials);
    }
    
    public void mostraAttivitaView(AttivitaView attivitaView) {
        root.setCenter(attivitaView.getRoot());
    }
    
    public void mostraCommentiView(CommentoView commentoView) {
        root.setCenter(commentoView.getRoot());
    }

    public BorderPane getRoot() { return root; }
    public HBox getItemDashboard() { return itemDashboard; }
    public HBox getItemProgetti() { return itemProgetti; }
    public HBox getItemAttivita() { return itemAttivita; }
    public Button getBtnLogout() { return btnLogout; }
    public Button getBtnVaiAiProgetti() { return btnVaiAiProgetti; }
    public TabPane getTabPane() { return tabPane; }
    public HBox getItemCommenti() { return itemCommenti; }
    
    public Label getStatTotale() { return statTotale; }
    public Label getStatCompletate() { return statCompletate; }
    public Label getStatInCorso() { return statInCorso; }
    public Label getStatSviluppo() { return statSviluppo; }
    public PieChart getPieChart() { return pieChart; }
    public BarChart<String, Number> getBarChart() { return barChart; }
}