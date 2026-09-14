package boundary.ui;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;

public class MainViewBuilder {

    public BorderPane createMainView() {
        // --- 1. ROOT LAYOUT (BorderPane principale) ---
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-shell");

        // --- 2. SIDEBAR (Sinistra) ---
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(260);

        // Brand Sidebar
        HBox brandBox = new HBox();
        brandBox.getStyleClass().add("sidebar__brand");
        StackPane logoMark = new StackPane();
        logoMark.getStyleClass().add("sidebar__logo-mark");
        Label logoText = new Label("UT");
        logoMark.getChildren().add(logoText);
        Label brandName = new Label("UninaTaskBoard");
        brandName.getStyleClass().add("sidebar__brand-name");
        brandBox.getChildren().addAll(logoMark, brandName);

        // Sezioni Navigazione Sidebar
        Label lblPrinc = new Label("PRINCIPALE");
        lblPrinc.getStyleClass().add("sidebar__section-label");
        
        VBox navPrinc = new VBox(4);
        navPrinc.getStyleClass().add("sidebar__nav");
        HBox itemDash = createNavItem("📊", "Dashboard", true);
        HBox itemProj = createNavItem("📁", "Progetti", false);
        navPrinc.getChildren().addAll(itemDash, itemProj);

        Label lblCurr = new Label("PROGETTO CORRENTE");
        lblCurr.getStyleClass().add("sidebar__section-label");

        VBox navCurr = new VBox(4);
        navCurr.getStyleClass().add("sidebar__nav");
        HBox itemTasks = createNavItem("✓", "Attività", false);
        HBox itemReport = createNavItem("📈", "Report e Metriche", false);
        HBox itemNotif = createNavItem("🔔", "Notifiche", false);
        navCurr.getChildren().addAll(itemTasks, itemReport, itemNotif);

        sidebar.getChildren().addAll(brandBox, lblPrinc, navPrinc, lblCurr, navCurr);
        root.setLeft(sidebar);

     // --- 3. TOPBAR (Alto) ---
        HBox topbar = new HBox();
        topbar.getStyleClass().add("topbar");
        topbar.setAlignment(javafx.geometry.Pos.CENTER_LEFT); 

        HBox searchBox = new HBox();
        searchBox.getStyleClass().add("topbar__search");
        Label searchIcon = new Label("🔍");
        searchIcon.getStyleClass().add("text-muted");
        TextField searchField = new TextField();
        searchField.setPromptText("Cerca attività, progetti o membri...");
        searchField.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchBox.getChildren().addAll(searchIcon, searchField);
        searchBox.setMaxWidth(360);

        // REGION SPACER: Spinge tutto ciò che segue a destra
        Region topbarSpacer = new Region();
        HBox.setHgrow(topbarSpacer, Priority.ALWAYS);

        HBox topbarActions = new HBox(16);
        topbarActions.getStyleClass().add("topbar__actions");
        topbarActions.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        
        Button btnNotif = new Button("🔔");
        btnNotif.getStyleClass().add("topbar__icon-btn");
        
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar");
        Label avatarLabel = new Label("MR");
        avatar.getChildren().add(avatarLabel);
        
        topbarActions.getChildren().addAll(btnNotif, avatar);

        topbar.getChildren().addAll(searchBox, topbarSpacer, topbarActions);
        root.setTop(topbar);

        // --- 4. CENTRO (TabPane con le due schermate) ---
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("app-main");
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // --- TAB 1: Elenco Attività ---
        Tab tabAttivita = new Tab("Elenco Attività");
        VBox contentTab1 = new VBox(24);
        contentTab1.getStyleClass().add("app-content");

        VBox headerTitles1 = new VBox(8);
        Label pageTitle1 = new Label("Attività del Progetto");
        pageTitle1.getStyleClass().add("page-title");
        Label pageSub1 = new Label("Gestisci e monitora lo stato delle attività collaborative");
        pageSub1.getStyleClass().add("page-subtitle");
        headerTitles1.getChildren().addAll(pageTitle1, pageSub1);

        // Toolbar filtri
        HBox toolbar = new HBox();
        toolbar.getStyleClass().add("toolbar");
        HBox filters = new HBox(12);
        filters.getStyleClass().add("toolbar__filters");
        filters.getChildren().addAll(
            new ComboBox<>(), new ComboBox<>(), new ComboBox<>(), new ComboBox<>()
        );
        // Imposta i prompt text dei filtri se vuoi
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnNuova = new Button("+ Nuova Attività");
        btnNuova.getStyleClass().addAll("button", "btn--primary");
        toolbar.getChildren().addAll(filters, spacer, btnNuova);

        // Tabella
        TableView<?> table = new TableView<>();
        table.getStyleClass().add("table-view");
        VBox.setVgrow(table, Priority.ALWAYS);
        // (Qui potrai aggiungere le TableColumn esattamente come facevi in FXML)

        contentTab1.getChildren().addAll(headerTitles1, toolbar, table);
        tabAttivita.setContent(contentTab1);

        // --- TAB 2: Report e Metriche ---
        Tab tabReport = new Tab("Report e Metriche");
        ScrollPane scrollReport = new ScrollPane();
        scrollReport.setFitToWidth(true);
        scrollReport.setStyle("-fx-background-color: transparent;");

        VBox contentTab2 = new VBox(24);
        contentTab2.getStyleClass().add("app-content");

        VBox headerTitles2 = new VBox(8);
        Label pageTitle2 = new Label("Report di Progetto");
        pageTitle2.getStyleClass().add("page-title");
        Label pageSub2 = new Label("Panoramica avanzamento e metriche di collaborazione");
        pageSub2.getStyleClass().add("page-subtitle");
        headerTitles2.getChildren().addAll(pageTitle2, pageSub2);

        // Griglia Stat Card
        GridPane statGrid = new GridPane();
        statGrid.setHgap(20);
        statGrid.setVgap(20);
        statGrid.add(createStatCard("Totale Attività", "xx"), 0, 0);
        statGrid.add(createStatCardWithTrend("Completate", "x", "↑ 12% vs mese scorso", "is-positive"), 1, 0);
        statGrid.add(createStatCard("In Corso", "x"), 2, 0);
        statGrid.add(createStatCard("Attività di Sviluppo", "x"), 3, 0);

        // Area Grafici
        GridPane chartGrid = new GridPane();
        chartGrid.setHgap(20);
        chartGrid.setVgap(20);
        
        PieChart pieChart = new PieChart();
        pieChart.setPrefHeight(300);
        VBox pieCard = createChartCard("Distribuzione per Stato", pieChart);
        
        BarChart<String, Number> barChart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        barChart.setPrefHeight(300);
        VBox barCard = createChartCard("Attività Completate per Membro", barChart);

        chartGrid.add(pieCard, 0, 0);
        chartGrid.add(barCard, 1, 0);

        contentTab2.getChildren().addAll(headerTitles2, statGrid, chartGrid);
        scrollReport.setContent(contentTab2);
        tabReport.setContent(scrollReport);

        tabPane.getTabs().addAll(tabAttivita, tabReport);
        root.setCenter(tabPane);

        return root;
    }

    // Metodi di supporto per non ripetere codice nella costruzione dei componenti
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

    private VBox createStatCard(String label, String value) {
        VBox card = new VBox(3);
        card.getStyleClass().add("stat-card");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-card__label");
        Label val = new Label(value);
        val.getStyleClass().add("stat-card__value");
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private VBox createStatCardWithTrend(String label, String value, String trendText, String trendClass) {
        VBox card = new VBox(3);
        card.getStyleClass().add("stat-card");
        Label lbl = new Label(label);
        lbl.getStyleClass().add("stat-card__label");
        Label val = new Label(value);
        val.getStyleClass().add("stat-card__value");
        Label trend = new Label(trendText);
        trend.getStyleClass().addAll("stat-card__trend", trendClass);
        card.getChildren().addAll(lbl, val, trend);
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
}