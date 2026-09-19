package boundary.persistence.jdbc;

import boundary.persistence.dao.AttivitaDAO;
import entity.Attivita;
import entity.Progetto;
import entity.enums.TipoAttivita;
import entity.enums.StatoAttivita;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttivitaBoundaryJdbc implements AttivitaDAO {

    // ============================================
    // CREA ATTIVITÀ
    // ============================================
    @Override
    public int creaAttivita(Attivita attivita) {
        String query = "{ ? = call fn_crea_attivita(?, ?, ?, ?) }";
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, attivita.getDescrizione());
            stmt.setString(3, attivita.getTipo().name());

            if (attivita.getDataScadenza() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(attivita.getDataScadenza().atStartOfDay()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            if (attivita.getProgetto() != null) {
                stmt.setInt(5, attivita.getProgetto().getId());
            } else {
                throw new SQLException("Impossibile creare l'attività: nessun progetto associato.");
            }

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            System.err.println("Errore Database (creaAttivita): " + e.getMessage());
        }
        return idGenerato;
    }

    // ============================================
    // LEGGI ATTIVITÀ DI UN PROGETTO
    // ============================================
    @Override
    public List<Attivita> leggiAttivitaPerProgetto(int idProgetto) {
        List<Attivita> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_attivita_progetto(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProgetto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attivita a = new Attivita();
                    a.setId(rs.getInt("id"));
                    a.setDescrizione(rs.getString("descrizione"));

                    // ✅ NIENTE toUpperCase!
                    a.setTipo(TipoAttivita.valueOf(rs.getString("tipo")));
                    a.setStato(StatoAttivita.valueOf(rs.getString("stato")));

                    // Data scadenza (può essere null)
                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    if (tsScadenza != null) {
                        a.setDataScadenza(tsScadenza.toLocalDateTime().toLocalDate());
                    }

                    a.setProgetto(new Progetto(idProgetto, null, null,null));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ============================================
    // AGGIORNA ATTIVITÀ (PROCEDURA → CALL)
    // ============================================
    @Override
    public void aggiornaAttivita(Attivita attivita) {
        String query = "CALL pr_aggiorna_attivita(?, ?, ?, ?)";  // ✅ CALL

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {  // ✅ PreparedStatement

            stmt.setInt(1, attivita.getId());
            stmt.setString(2, attivita.getDescrizione());

            if (attivita.getDataScadenza() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(attivita.getDataScadenza().atStartOfDay()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setString(4, attivita.getStato().name());

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // ELIMINA ATTIVITÀ (PROCEDURA → CALL)
    // ============================================
    @Override
    public void eliminaAttivita(int id) {
        String query = "CALL pr_elimina_attivita(?)";  // ✅ CALL

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {  // ✅ PreparedStatement

            stmt.setInt(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // ASSEGNA STUDENTE AD ATTIVITÀ
    // ============================================
    @Override
    public boolean assegnaStudenteAttivita(String matricola, int idAttivita) {
        String query = "{ ? = call fn_assegna_studente_attivita(?, ?) }";
        boolean risultato = false;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setString(2, matricola);
            stmt.setInt(3, idAttivita);

            stmt.execute();
            risultato = stmt.getBoolean(1);

        } catch (SQLException e) {
            System.err.println("Errore Assegnazione: " + e.getMessage());
        }
        return risultato;
    }

    // ============================================
    // RIMUOVI STUDENTE DA ATTIVITÀ (PROCEDURA → CALL)
    // ============================================
    @Override
    public void rimuoviStudenteAttivita(String matricola, int idAttivita) {
        String query = "CALL pr_rimuovi_studente_attivita(?, ?)";  // ✅ CALL

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            stmt.setInt(2, idAttivita);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // INSERISCI + ASSEGNA (helper)
    // ============================================
    @Override
    public boolean inserisciAttivitaConAssegnazione(Attivita attivita, String matricola) {
        int idAttivita = creaAttivita(attivita);

        if (idAttivita != -1 && matricola != null && !matricola.trim().isEmpty()) {
            return assegnaStudenteAttivita(matricola, idAttivita);
        }
        return idAttivita != -1;
    }

    // ============================================
    // GET ATTIVITÀ DI UNO STUDENTE (tutti i progetti)
    // ============================================
    @Override
    public List<Attivita> getAttivitaStudente(String matricola) {
        List<Attivita> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_attivita_studente(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attivita a = new Attivita();
                    a.setId(rs.getInt("id"));
                    a.setDescrizione(rs.getString("descrizione"));
                    a.setTipo(TipoAttivita.valueOf(rs.getString("tipo")));
                    a.setStato(StatoAttivita.valueOf(rs.getString("stato")));

                    // ✅ DataCreazione → LocalDate
                    Timestamp tsCreazione = rs.getTimestamp("data_creazione");
                    if (tsCreazione != null) {
                        a.setDataCreazione(tsCreazione.toLocalDateTime().toLocalDate());
                    }

                    // ✅ DataScadenza → LocalDate
                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    if (tsScadenza != null) {
                        a.setDataScadenza(tsScadenza.toLocalDateTime().toLocalDate());
                    }

                    // ✅ Progetto
                    int idProg = rs.getInt("id_progetto");
                    a.setProgetto(new Progetto(idProg, null, null,null));

                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    @Override
    public List<String> getAssegnatariAttivita(int idAttivita) {
        List<String> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_studenti_attivita(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAttivita);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getString("matricola"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}