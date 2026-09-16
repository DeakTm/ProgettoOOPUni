package boundary.persistence.jdbc;

import boundary.persistence.dao.ProgettoDAO;
import entity.Progetto;
import util.DatabaseManager;
import entity.enums.StatoAvanzamento;
import java.sql.*;
import java.time.LocalDate;

public class ProgettoBoundaryJdbc implements ProgettoDAO {

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

    @Override
    public Progetto leggiProgettoPerId(int id) {
        String query = "SELECT * FROM fn_leggi_progetto_id(?)";
        Progetto progetto = null;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int projId = rs.getInt("id");
                    LocalDate scadenza = rs.getDate("scadenza").toLocalDate();
                    String statoDb = rs.getString("stato");
                    StatoAvanzamento statoEnum = StatoAvanzamento.valueOf(statoDb.toUpperCase());
                    progetto = new Progetto(projId, statoEnum, scadenza);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progetto;
    }

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

    @Override
    public void eliminaProgetto(int id) {
        String query = "{ call pr_elimina_progetto(?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
}