package boundary.persistence.jdbc;

import boundary.persistence.dao.FileCodiceDAO;
import entity.FileCodice;
import entity.enums.TipoLinguaggio;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileCodiceBoundaryJdbc implements FileCodiceDAO {


    @Override
    public int creaFile(FileCodice file, int idAttivita) {
        String query = "{ ? = call fn_crea_filecodice(?, ?, ?, ?) }"; 
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, file.getNome_file());
            stmt.setString(3, file.getContenuto());
            stmt.setString(4, file.getLinguaggio().name());
            stmt.setInt(5, idAttivita);

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }


    @Override
    public List<FileCodice> leggiFilePerAttivita(int idAttivita) {
        List<FileCodice> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_file_attivita(?)";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAttivita);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_file");                   
                    String nome = rs.getString("nome_file");
                    String contenuto = rs.getString("contenuto");
                    String langDb = rs.getString("linguaggio");
                    TipoLinguaggio linguaggioEnum = TipoLinguaggio.valueOf(langDb);  

                    Timestamp ts = rs.getTimestamp("data_modifica");  
                    LocalDateTime dataModifica = (ts != null) ? ts.toLocalDateTime() : null;

                    FileCodice f = new FileCodice(id, nome, linguaggioEnum, contenuto, dataModifica, null);
                    lista.add(f);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void aggiornaFile(FileCodice file) {
        String query = "CALL pr_aggiorna_filecodice(?, ?)";  

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, file.getId());
            stmt.setString(2, file.getContenuto());

            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void eliminaFile(int id) {
        String query = "CALL pr_elimina_filecodice(?)";  

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}