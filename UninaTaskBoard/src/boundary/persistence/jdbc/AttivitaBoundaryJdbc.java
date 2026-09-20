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

    @Override
    public int creaAttivita(Attivita attivita) {
        String query = "{ ? = call fn_crea_attivita(?, ?, ?, ?) }";
        int idGenerato = -1;

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (CallableStatement stmt = conn.prepareCall(query)) {

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
                throw new SQLException("Nessun progetto associato.");
            }

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            System.err.println("Errore creaAttivita: " + e.getMessage());
        }
        return idGenerato;
    }

    @Override
    public List<Attivita> leggiAttivitaPerProgetto(int idProgetto) {
        List<Attivita> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_attivita_progetto(?)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProgetto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attivita a = new Attivita();
                    a.setId(rs.getInt("id"));
                    a.setDescrizione(rs.getString("descrizione"));
                    a.setTipo(TipoAttivita.valueOf(rs.getString("tipo")));
                    a.setStato(StatoAttivita.valueOf(rs.getString("stato")));

                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    if (tsScadenza != null) {
                        a.setDataScadenza(tsScadenza.toLocalDateTime().toLocalDate());
                    }

                    int idProg = idProgetto;
                    String nomeProg = null;
                    try {
                        nomeProg = rs.getString("nome_progetto");
                    } catch (SQLException e) {
                        try {
                            nomeProg = rs.getString("nome");
                        } catch (SQLException ex) {
                            
                        }
                    }

                    a.setProgetto(new Progetto(idProg, null, null, nomeProg));
                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public Attivita leggiAttivitaPerId(int id) {
        Attivita attivita = null;
        String query = "SELECT * FROM fn_leggi_attivita_id(?::bigint)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    attivita = new Attivita();
                    attivita.setId(rs.getInt("id"));
                    attivita.setDescrizione(rs.getString("descrizione"));
                    attivita.setTipo(TipoAttivita.valueOf(rs.getString("tipo")));
                    attivita.setStato(StatoAttivita.valueOf(rs.getString("stato")));

                    Timestamp tsCreazione = rs.getTimestamp("data_creazione");
                    if (tsCreazione != null) {
                        attivita.setDataCreazione(tsCreazione.toLocalDateTime().toLocalDate());
                    }
                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    if (tsScadenza != null) {
                        attivita.setDataScadenza(tsScadenza.toLocalDateTime().toLocalDate());
                    }

                    int idProg = rs.getInt("id_progetto");
                    String nomeProg = null;
                    try {
                        nomeProg = rs.getString("nome_progetto");
                    } catch (SQLException e) {
                        try {
                            nomeProg = rs.getString("nome");
                        } catch (SQLException ex) {
                           
                        }
                    }

                    attivita.setProgetto(new Progetto(idProg, null, null, nomeProg));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attivita;
    }

    @Override
    public void aggiornaAttivita(Attivita attivita) {
        String query = "CALL pr_aggiorna_attivita(?, ?, ?, ?)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

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

    @Override
    public void eliminaAttivita(int id) {
        String query = "CALL pr_elimina_attivita(?)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean assegnaStudenteAttivita(String matricola, int idAttivita) {
        String query = "{ ? = call fn_assegna_studente_attivita(?, ?) }";
        boolean risultato = false;

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (CallableStatement stmt = conn.prepareCall(query)) {

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

    @Override
    public void rimuoviStudenteAttivita(String matricola, int idAttivita) {
        String query = "CALL pr_rimuovi_studente_attivita(?, ?)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            stmt.setInt(2, idAttivita);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean inserisciAttivitaConAssegnazione(Attivita attivita, String matricola) {
        int idAttivita = creaAttivita(attivita);

        if (idAttivita != -1 && matricola != null && !matricola.trim().isEmpty()) {
            return assegnaStudenteAttivita(matricola, idAttivita);
        }
        return idAttivita != -1;
    }

    @Override
    public List<Attivita> getAttivitaStudente(String matricola) {
        List<Attivita> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_attivita_studente(?)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attivita a = new Attivita();
                    a.setId(rs.getInt("id"));
                    a.setDescrizione(rs.getString("descrizione"));
                    a.setTipo(TipoAttivita.valueOf(rs.getString("tipo")));
                    a.setStato(StatoAttivita.valueOf(rs.getString("stato")));

                    Timestamp tsCreazione = rs.getTimestamp("data_creazione");
                    if (tsCreazione != null) {
                        a.setDataCreazione(tsCreazione.toLocalDateTime().toLocalDate());
                    }
                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    if (tsScadenza != null) {
                        a.setDataScadenza(tsScadenza.toLocalDateTime().toLocalDate());
                    }

                    int idProg = rs.getInt("id_progetto");
                    String nomeProg = null;
                    try {
                        nomeProg = rs.getString("nome_progetto");
                    } catch (SQLException e) {
                        try {
                            nomeProg = rs.getString("nome");
                        } catch (SQLException ex) {
                            
                        }
                    }

                    a.setProgetto(new Progetto(idProg, null, null, nomeProg));
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

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

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

    @Override
    public double getMediaRevisioniProgetto(int idProgetto) {
        String query = "SELECT fn_media_revisioni_progetto(?)";
        double media = 0.0;

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProgetto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    media = rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return media;
    }
}