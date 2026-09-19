package boundary.ui;

import entity.Attivita;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
    private VBox dashboardContent;
    private Label lblBenvenuto;
    private Label lblAvatarHome;
    
    // Quick Actions & Scadenze
    private Button btnNuovoProgettoQuick;
    private VBox containerScadenze;

    public MainView() {
        root = new BorderPane();
        root.getStyleClass().add("app-shell");

        // --- SIDEBAR ---
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
        
        this.dashboardContent = createDashboardContent();
        root.setCenter(dashboardContent);
    }

    private VBox createDashboardContent() {
        VBox content = new VBox(28);
        content.getStyleClass().add("app-content");
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(32, 36, 36, 36)); 

        // 1. Header di Benvenuto
        HBox welcomeBox = new HBox(20);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        
        StackPane pfp = new StackPane();
        pfp.getStyleClass().add("avatar");
        pfp.setPrefSize(56, 56);
        lblAvatarHome = new Label("UT");
        lblAvatarHome.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: white;");
        pfp.getChildren().add(lblAvatarHome);

        VBox textVBox = new VBox(4);
        Label title = new Label("Dashboard Personale");
        title.getStyleClass().add("page-title");
        
        lblBenvenuto = new Label("Bentornato! Ecco il punto della situazione.");
        lblBenvenuto.getStyleClass().add("page-subtitle");
        textVBox.getChildren().addAll(title, lblBenvenuto);
        welcomeBox.getChildren().addAll(pfp, textVBox);

        // 2. Sezione Quick Actions (Azioni Rapide)
        VBox quickActionsBox = new VBox(12);
        Label lblQuickTitle = new Label("⚡ Azioni Rapide");
        lblQuickTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox actionsRow = new HBox(12);
        btnNuovoProgettoQuick = new Button("+ Nuovo Progetto");
        btnNuovoProgettoQuick.getStyleClass().addAll("button", "btn--primary");
        
        actionsRow.getChildren().add(btnNuovoProgettoQuick);
        quickActionsBox.getChildren().addAll(lblQuickTitle, actionsRow);

        // 3. Sezione Scadenze Imminenti (Prossimi 7 giorni)
        VBox scadenzeCard = new VBox(16);
        scadenzeCard.getStyleClass().add("chart-card");
        scadenzeCard.setPadding(new Insets(24));
        
        Label lblScadenzeTitle = new Label("⏳ Attività in Scadenza (Prossimi 7 giorni)");
        lblScadenzeTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        containerScadenze = new VBox(10);
        
        scadenzeCard.getChildren().addAll(lblScadenzeTitle, new Separator(), containerScadenze);

        content.getChildren().addAll(welcomeBox, new Separator(), quickActionsBox, scadenzeCard);
        return content;
    }

    // Metodo per popolare dinamicamente le attività in scadenza nella Home
    public void mostraScadenzeImminenti(List<Attivita> attivitaScadenza) {
        containerScadenze.getChildren().clear();

        if (attivitaScadenza == null || attivitaScadenza.isEmpty()) {
            Label vuoto = new Label("🎉 Ottimo lavoro! Nessuna attività in scadenza nei prossimi 7 giorni.");
            vuoto.getStyleClass().add("text-muted");
            containerScadenze.getChildren().add(vuoto);
            return;
        }

        for (Attivita a : attivitaScadenza) {
            HBox item = new HBox(12);
            item.setAlignment(Pos.CENTER_LEFT);
            item.setPadding(new Insets(10, 14, 10, 14));
            item.getStyleClass().add("card");

            VBox info = new VBox(3);
            Label desc = new Label(a.getDescrizione());
            desc.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
            
            String nomeProg = (a.getProgetto() != null && a.getProgetto().getNome() != null) 
                ? a.getProgetto().getNome() : "Progetto #" + (a.getProgetto() != null ? a.getProgetto().getId() : "");
            
            Label sub = new Label("Progetto: " + nomeProg);
            sub.getStyleClass().add("text-muted");
            sub.setStyle("-fx-font-size: 11px;");
            info.getChildren().addAll(desc, sub);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            LocalDate scadenza = a.getDataScadenza();
            long giorniRimanenti = ChronoUnit.DAYS.between(LocalDate.now(), scadenza);
            
            Label lblScad = new Label(scadenza.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " (" + (giorniRimanenti == 0 ? "Oggi!" : "tra " + giorniRimanenti + " giorni") + ")");
            lblScad.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: -color-warning-text;");

            item.getChildren().addAll(info, spacer, lblScad);
            containerScadenze.getChildren().add(item);
        }
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
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPrefHeight(56);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox userBox = new HBox(12);
        userBox.setAlignment(Pos.CENTER_RIGHT);

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

        userBox.getChildren().addAll(miniAvatar, lblNomeTopbar, sep, logoutButtonReplacement(btnLogout));
        topbar.getChildren().addAll(spacer, userBox);
        return topbar;
    }

    private Button logoutButtonReplacement(Button btn) { return btn; }

    public void mostraDashboard() { root.setCenter(dashboardContent); }
    public void mostraProgettiView(ProgettiView progettiView) { root.setCenter(progettiView.getRoot()); }
    public void mostraAttivitaView(AttivitaView attivitaView) { root.setCenter(attivitaView.getRoot()); }
    public void mostraCommentiView(CommentoView commentoView) { root.setCenter(commentoView.getRoot()); }

    public void setUtenteLoggato(String matricola) {
        if (lblBenvenuto != null) lblBenvenuto.setText("Bentornato, " + matricola + "! Ecco il riepilogo delle scadenze.");
        if (lblNomeTopbar != null) lblNomeTopbar.setText(matricola);
        
        String initials = (matricola != null && matricola.length() >= 2) 
            ? matricola.substring(0, 2).toUpperCase() 
            : (matricola != null ? matricola.toUpperCase() : "UT");

        if (lblAvatarHome != null) lblAvatarHome.setText(initials);
        if (lblAvatarTopbar != null) lblAvatarTopbar.setText(initials);
    }
    
    public BorderPane getRoot() { return root; }
    public HBox getItemDashboard() { return itemDashboard; }
    public HBox getItemProgetti() { return itemProgetti; }
    public HBox getItemAttivita() { return itemAttivita; }
    public HBox getItemCommenti() { return itemCommenti; }
    public Button getBtnLogout() { return btnLogout; }
    public Button getBtnNuovoProgettoQuick() { return btnNuovoProgettoQuick; }
}