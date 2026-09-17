package boundary.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;

public class MainView {

    // COMPONENTI PRINCIPALI (campi, non variabili locali)
    private BorderPane root;
    
    // Sidebar
    private HBox itemDashboard;
    private HBox itemProgetti;
    private HBox itemAttivita;
    private HBox itemReport;
    private HBox itemNotifiche;
    
    // Topbar
    private TextField searchField;
    private Button btnNotif;
    
    // Tab 1: Attività
    private ComboBox<String> filtroStato;
    private ComboBox<String> filtroTipo;
    private ComboBox<String> filtroScadenza;
    private ComboBox<String> filtroMembro;
    private Button btnNuovaAttivita;
    private TableView<Object> tabellaAttivita;
    
    // Tab 2: Report
    private Label statTotale;
    private Label statCompletate;
    private Label statInCorso;
    private Label statSviluppo;
    private PieChart pieChart;
    private BarChart<String, Number> barChart;

    // COSTRUTTORE: costruisce la UI

    public MainView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        // --- SIDEBAR ---
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);

        HBox brandBox = createBrandBox();
        Label lblPrinc = new Label("PRINCIPALE");
        lblPrinc.getStyleClass().add("sidebar__section-label");
        
        VBox navPrinc = new VBox(4);
        navPrinc.getStyleClass().add("sidebar__nav");
        itemDashboard = createNavItem("📊", "Dashboard", true);
        itemProgetti = createNavItem("📁", "Progetti", false);
        navPrinc.getChildren().addAll(itemDashboard, itemProgetti);

        Label lblCurr = new Label("PROGETTO CORRENTE");
        lblCurr.getStyleClass().add("sidebar__section-label");

        VBox navCurr = new VBox(4);
        navCurr.getStyleClass().add("sidebar__nav");
        itemAttivita = createNavItem("✓", "Attività", false);
        itemReport = createNavItem("📈", "Report e Metriche", false);
        itemNotifiche = createNavItem("🔔", "Notifiche", false);
        navCurr.getChildren().addAll(itemAttivita, itemReport, itemNotifiche);

        sidebar.getChildren().addAll(brandBox, lblPrinc, navPrinc, lblCurr, navCurr);
        root.setLeft(sidebar);

        // --- TOPBAR ---
        HBox topbar = createTopbar();
        root.setTop(topbar);

        // --- CENTRO: TabPane ---
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("app-main");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab tabAttivita = createTabAttivita();
        Tab tabReport = createTabReport();

        tabPane.getTabs().addAll(tabAttivita, tabReport);
        root.setCenter(tabPane);
    }

    // CREAZIONE TAB ATTIVITÀ
    private Tab createTabAttivita() {
        Tab tab = new Tab("Elenco Attività");
        VBox content = new VBox(24);
        content.getStyleClass().add("app-content");

        // Header
        VBox header = new VBox(8);
        Label title = new Label("Attività del Progetto");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Gestisci e monitora lo stato delle attività collaborative");
        sub.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, sub);

        // Toolbar filtri
        HBox toolbar = new HBox();
        toolbar.getStyleClass().add("toolbar");
        HBox filters = new HBox(12);
        filters.getStyleClass().add("toolbar__filters");
        
        filtroStato = new ComboBox<>();
        filtroStato.setPromptText("Stato");
        filtroTipo = new ComboBox<>();
        filtroTipo.setPromptText("Tipo");
        filtroScadenza = new ComboBox<>();
        filtroScadenza.setPromptText("Scadenza");
        filtroMembro = new ComboBox<>();
        filtroMembro.setPromptText("Membro");
        
        filters.getChildren().addAll(filtroStato, filtroTipo, filtroScadenza, filtroMembro);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        btnNuovaAttivita = new Button("+ Nuova Attività");
        btnNuovaAttivita.getStyleClass().addAll("button", "btn--primary");
        
        toolbar.getChildren().addAll(filters, spacer, btnNuovaAttivita);

        // Tabella
        tabellaAttivita = new TableView<>();
        tabellaAttivita.getStyleClass().add("table-view");
        VBox.setVgrow(tabellaAttivita, Priority.ALWAYS);

        content.getChildren().addAll(header, toolbar, tabellaAttivita);
        tab.setContent(content);
        return tab;
    }

    // CREAZIONE TAB REPORT
    private Tab createTabReport() {
        Tab tab = new Tab("Report e Metriche");
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");

        VBox content = new VBox(24);
        content.getStyleClass().add("app-content");

        // Header
        VBox header = new VBox(8);
        Label title = new Label("Report di Progetto");
        title.getStyleClass().add("page-title");
        Label sub = new Label("Panoramica avanzamento e metriche di collaborazione");
        sub.getStyleClass().add("page-subtitle");
        header.getChildren().addAll(title, sub);

        // Stat cards
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

        // Chart
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

    // METODI DI SUPPORTO (creazione componenti)
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
        Label avatarLabel = new Label("MR");
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

    // GETTER PER IL CONTROLLER
    public BorderPane getRoot() { return root; }
    public HBox getItemDashboard() { return itemDashboard; }
    public HBox getItemProgetti() { return itemProgetti; }
    public HBox getItemAttivita() { return itemAttivita; }
    public HBox getItemReport() { return itemReport; }
    public HBox getItemNotifiche() { return itemNotifiche; }
    public TextField getSearchField() { return searchField; }
    public Button getBtnNotif() { return btnNotif; }
    public ComboBox<String> getFiltroStato() { return filtroStato; }
    public ComboBox<String> getFiltroTipo() { return filtroTipo; }
    public ComboBox<String> getFiltroScadenza() { return filtroScadenza; }
    public ComboBox<String> getFiltroMembro() { return filtroMembro; }
    public Button getBtnNuovaAttivita() { return btnNuovaAttivita; }
    public TableView<Object> getTabellaAttivita() { return tabellaAttivita; }
    public Label getStatTotale() { return statTotale; }
    public Label getStatCompletate() { return statCompletate; }
    public Label getStatInCorso() { return statInCorso; }
    public Label getStatSviluppo() { return statSviluppo; }
    public PieChart getPieChart() { return pieChart; }
    public BarChart<String, Number> getBarChart() { return barChart; }
}