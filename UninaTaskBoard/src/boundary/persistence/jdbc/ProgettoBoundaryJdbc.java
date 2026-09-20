package boundary.persistence.jdbc;

import boundary.persistence.dao.ProgettoDAO;
import entity.Progetto;
import entity.Studente;
import entity.enums.StatoAvanzamento;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProgettoBoundaryJdbc implements ProgettoDAO {

    @Override
    public int creaProgetto(Progetto progetto) {
        String query = "{ ? = call fn_crea_progetto(?, ?) }";
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, progetto.getNome());
            stmt.setDate(3, Date.valueOf(progetto.getScadenza()));

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }

    @Override
    public Progetto leggiProgettoPerId(int id) {
        // CORRETTO: La funzione accetta solo l'id
        String query = "SELECT * FROM fn_leggi_progetto_id(?)";
        Progetto progetto = null;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    progetto = new Progetto(
                    	    rs.getInt("id"),
                    	    StatoAvanzamento.valueOf(rs.getString("stato")),
                    	    rs.getDate("scadenza") != null ? rs.getDate("scadenza").toLocalDate() : null,
                    	    rs.getString("nome")
                    	);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return progetto;
    }

    @Override
    public void aggiornaProgetto(Progetto progetto) {
        String query = "CALL pr_aggiorna_progetto(?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, progetto.getId());
            stmt.setString(2, progetto.getNome());
            stmt.setDate(3, Date.valueOf(progetto.getScadenza()));
            stmt.setString(4, progetto.getStato().name());

            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
        String query = "CALL pr_rimuovi_studente_progetto(?, ?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            stmt.setInt(2, idProgetto);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Studente> leggiStudentiPerProgetto(int idProgetto) {
        List<Studente> membri = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_studenti_progetto(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idProgetto);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    membri.add(new Studente(
                        rs.getString("Matricola"),
                        rs.getString("Nome"),
                        rs.getString("Cognome")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura studenti: " + e.getMessage());
        }
        return membri;
    }

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
                    	    StatoAvanzamento.valueOf(rs.getString("stato")),
                    	    rs.getDate("scadenza") != null ? rs.getDate("scadenza").toLocalDate() : null,
                    	    rs.getString("nome")
                    	));
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore lettura progetti studente: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

	@Override
	public List<String> getStudentiByProgetto(int idProgetto) {
	    List<String> matricole = new ArrayList<>();
	    String query = "SELECT * FROM fn_leggi_studenti_progetto(?)";

	    try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setInt(1, idProgetto);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                matricole.add(rs.getString("Matricola"));   
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("ERRORE getStudentiByProgetto: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return matricole;
	}
}