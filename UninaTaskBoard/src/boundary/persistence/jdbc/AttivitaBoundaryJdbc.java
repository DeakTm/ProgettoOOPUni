	package boundary.persistence.jdbc;
	
	import boundary.persistence.dao.AttivitaDAO;
	import entity.Attivita;
	import entity.enums.StatoAttivita;
	import entity.enums.TipoAttivita;
	import util.DatabaseManager;
	
	import java.sql.*;
	import java.time.LocalDate;
	import java.util.ArrayList;
	import java.util.List;
	
	public class AttivitaBoundaryJdbc implements AttivitaDAO {
	
	    @Override
	    public int creaAttivita(Attivita attivita, int idProgetto) {
	        // Aggiunto un '?' per la data di scadenza
	        String query = "{ ? = call fn_crea_attivita(?, ?, ?, ?) }";
	        int idGenerato = -1;
	
	        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
	             CallableStatement stmt = conn.prepareCall(query)) {
	
	            stmt.registerOutParameter(1, Types.INTEGER);
	            stmt.setString(2, attivita.getDescrizione());
	            stmt.setString(3, attivita.getTipo().name()); 
	            stmt.setDate(4, Date.valueOf(attivita.getDataScadenza()));
	            stmt.setInt(5, idProgetto);
	
	            stmt.execute();
	            idGenerato = stmt.getInt(1);
	
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return idGenerato;
	    }
	
	    @Override
	    public List<Attivita> leggiAttivitaPerProgetto(int idProgetto) {
	        String query = "SELECT * FROM fn_leggi_attivita_progetto(?)";
	        List<Attivita> lista = new ArrayList<>();

	        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
	             PreparedStatement stmt = conn.prepareStatement(query)) {

	            stmt.setInt(1, idProgetto);

	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    int id = rs.getInt("id");
	                    
	                    String descrizione = rs.getString("descrizione");
	                    String tipodb = rs.getString("tipo");
	                    TipoAttivita tipoENUM = TipoAttivita.valueOf(tipodb.toUpperCase());
	                    
	                    LocalDate dataCreazione = rs.getDate("data_creazione").toLocalDate();
	                    LocalDate dataScadenza = rs.getDate("data_scadenza").toLocalDate();

	                    String statodb = rs.getString("stato");
	                    StatoAttivita statoENUM = StatoAttivita.valueOf(statodb.toUpperCase());
	                    
	                    Attivita a = new Attivita(id, descrizione, dataCreazione, dataScadenza, statoENUM, tipoENUM);
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
	            stmt.setDate(3, Date.valueOf(attivita.getDataScadenza()));
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
	            e.printStackTrace();
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
	}