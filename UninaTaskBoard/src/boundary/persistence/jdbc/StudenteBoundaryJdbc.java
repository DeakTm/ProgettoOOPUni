package boundary.persistence.jdbc;

import entity.Studente;
import util.DatabaseManager;
import boundary.persistence.dao.StudenteDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudenteBoundaryJdbc implements StudenteDAO {

    @Override
    public String creaStudente(Studente studente, String password) {
        String query = "{ ? = call fn_crea_studente(?, ?, ?, ?) }";
        String matricolaGenerata = null;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.VARCHAR);
            
            stmt.setString(2, studente.getMatricola());
            stmt.setString(3, studente.getNome());
            stmt.setString(4, studente.getCognome());
            stmt.setString(5, password);

            stmt.execute();
            matricolaGenerata = stmt.getString(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return matricolaGenerata;
    }

    @Override
    public List<Studente> leggiTuttiStudenti() {
        String query = "SELECT * FROM fn_leggi_studenti()";
        List<Studente> studenti = new ArrayList<>();

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String matricola = rs.getString("Matricola");
                String nome = rs.getString("Nome");
                String cognome = rs.getString("Cognome");
                
                studenti.add(new Studente(matricola, nome, cognome));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studenti;
    }

    @Override
    public Studente leggiStudentePerMatricola(String matricola) {
        String query = "SELECT * FROM fn_leggi_studente_matricola(?)";
        Studente studente = null;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String mat = rs.getString("Matricola");
                    String nome = rs.getString("Nome");
                    String cognome = rs.getString("Cognome");
                    
                    studente = new Studente(mat, nome, cognome);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studente;
    }

    @Override
    public void aggiornaStudente(Studente studente, String nuovaPassword) {
        String query = "{ call pr_aggiorna_studente(?, ?, ?, ?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setString(1, studente.getMatricola());
            stmt.setString(2, studente.getNome());
            stmt.setString(3, studente.getCognome());
            stmt.setString(4, nuovaPassword);

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaStudente(String matricola) {
        String query = "{ call pr_elimina_studente(?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setString(1, matricola);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean verificaLogin(String matricola, String password) {
        String query = "SELECT fn_verifica_login(?, ?)";
        boolean credenzialiValide = false;

        System.out.println(">>> DEBUG LOGIN: Matricola = [" + matricola + "]");

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, matricola);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    credenzialiValide = rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            System.err.println(">>> ERRORE SQL LOGIN: " + e.getMessage());
            e.printStackTrace();
        }
        
        return credenzialiValide;
    }
}