package boundary.persistence.jdbc;

import boundary.persistence.dao.AttivitaDAO;
import entity.Attivita;
import entity.Progetto;
import entity.enums.TipoAttivita;
import entity.enums.StatoAttivita;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttivitaBoundaryJdbc implements AttivitaDAO {

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
            
            // Prende l'ID dall'oggetto Progetto associato all'Attività
            if (attivita.getProgetto() != null) {
                stmt.setInt(5, attivita.getProgetto().getId());
            } else {
                throw new SQLException("Impossibile creare l'attività: nessun progetto associato.");
            }

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            System.err.println("Errore Database (Trigger Attività): " + e.getMessage());
        }
        return idGenerato;
    }

    @Override
    public List<Attivita> leggiAttivitaPerProgetto(int idProgetto) {
        List<Attivita> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_attivita_progetto(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProgetto);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String descrizione = rs.getString("descrizione");
                    
                    String tipoStr = rs.getString("tipo");
                    TipoAttivita tipo = TipoAttivita.valueOf(tipoStr.toUpperCase());
                    
                    LocalDate dataScadenza = null;
                    Timestamp tsScadenza = rs.getTimestamp("data_scadenza");
                    if (tsScadenza != null) {
                        // FIX: Uso toLocalDateTime() prima di toLocalDate()
                        dataScadenza = tsScadenza.toLocalDateTime().toLocalDate();
                    }

                    String statoStr = rs.getString("stato");
                    StatoAttivita stato = StatoAttivita.valueOf(statoStr.toUpperCase());

                    Progetto progettoRiferimento = new Progetto(idProgetto, null, null);

                    // Adesso funzionerà grazie al costruttore vuoto aggiunto in Attivita.java
                    Attivita a = new Attivita();
                    a.setId(id);
                    a.setDescrizione(descrizione);
                    a.setTipo(tipo);
                    a.setDataScadenza(dataScadenza);
                    a.setStato(stato);
                    a.setProgetto(progettoRiferimento);

                    lista.add(a);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void aggiornaAttivita(Attivita attivita) {
        String query = "{ call pr_aggiorna_attivita(?, ?, ?, ?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

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
        String query = "{ call pr_elimina_attivita(?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

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

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.BOOLEAN);
            stmt.setString(2, matricola);
            stmt.setInt(3, idAttivita);

            stmt.execute();
            risultato = stmt.getBoolean(1);

        } catch (SQLException e) {
            System.err.println("Errore Assegnazione (Trigger DB): " + e.getMessage());
        }
        return risultato;
    }

    @Override
    public void rimuoviStudenteAttivita(String matricola, int idAttivita) {
        String query = "{ call pr_rimuovi_studente_attivita(?, ?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

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
}