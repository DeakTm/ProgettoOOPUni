package boundary.persistence.jdbc;

import boundary.persistence.dao.ProgettoDAO;
import entity.Progetto;
import entity.enums.StatoAvanzamento;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProgettoBoundaryJdbc implements ProgettoDAO {

    // ============================================
    // fn_crea_progetto(p_scadenza DATE) → INT
    // ============================================
    @Override
    public int creaProgetto(Progetto progetto) {
        String query = "{ ? = call fn_crea_progetto(?) }";
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setDate(2, Date.valueOf(progetto.getScadenze()));

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }

    // ============================================
    // fn_leggi_progetto_id(p_id) → TABLE(id, scadenza, stato)
    // ============================================
    @Override
    public Progetto leggiProgettoPerId(int id) {
        String query = "SELECT * FROM fn_leggi_progetto_id(?)";
        Progetto progetto = null;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    progetto = new Progetto(
                        rs.getInt("id"),
                        StatoAvanzamento.valueOf(rs.getString("stato")),  // ✅ NIENTE toUpperCase
                        rs.getDate("scadenza").toLocalDate()
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progetto;
    }

    // ============================================
    // pr_aggiorna_progetto(p_id, p_scadenza, p_stato) → PROCEDURE
    // ============================================
    @Override
    public void aggiornaProgetto(Progetto progetto) {
        String query = "{ call pr_aggiorna_progetto(?, ?, ?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, progetto.getId());
            stmt.setDate(2, Date.valueOf(progetto.getScadenze()));
            stmt.setString(3, progetto.getStato().name());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // pr_elimina_progetto(p_id) → PROCEDURE
    // ============================================
    @Override
    public void eliminaProgetto(int id) {
        String query = "CALL pr_elimina_progetto(?)";
        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // fn_assegna_studente_progetto(p_matricola, p_id) → BOOLEAN
    // ============================================
    @Override
    public boolean assegnaStudenteProgetto(String matricola, int idProgetto) {
        String query = "{ ? = call fn_assegna_studente_progetto(?, ?) }";
        boolean risultato = false;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setString(2, matricola);
            stmt.setInt(3, idProgetto);

            stmt.execute();
            risultato = stmt.getBoolean(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return risultato;
    }

    // ============================================
    // pr_rimuovi_studente_progetto(p_matricola, p_id) → PROCEDURE
    // ============================================
    @Override
    public void rimuoviStudenteProgetto(String matricola, int idProgetto) {
        String query = "{ call pr_rimuovi_studente_progetto(?, ?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setString(1, matricola);
            stmt.setInt(2, idProgetto);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // fn_leggi_studenti_progetto(p_id) → TABLE(Matricola, Nome, Cognome)
    // ============================================
    @Override
    public List<String> getStudentiByProgetto(int idProgetto) {
        List<String> matricole = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_studenti_progetto(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProgetto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    matricole.add(rs.getString("Matricola"));  // nome colonna con M maiuscola!
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura studenti: " + e.getMessage());
        }
        return matricole;
    }

    // ============================================
    // fn_progetti_studente(p_matricola) → TABLE(id, scadenza, stato)
    // ============================================
    @Override
    public List<Progetto> getProgettiStudente(String matricola) {
        List<Progetto> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_progetti_studente(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Progetto(
                        rs.getInt("id"),
                        StatoAvanzamento.valueOf(rs.getString("stato")),  // ✅ NIENTE toUpperCase
                        rs.getDate("scadenza") != null 
                            ? rs.getDate("scadenza").toLocalDate() 
                            : null
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura progetti studente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}