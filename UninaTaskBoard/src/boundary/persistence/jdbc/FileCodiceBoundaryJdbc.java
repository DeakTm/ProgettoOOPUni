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
        String query = "{ ? = call fn_crea_file(?, ?, ?, ?, ?) }";
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, file.getNome_file());
            stmt.setString(3, file.getLinguaggio().name()); 
            stmt.setString(4, file.getContenuto());
            stmt.setTimestamp(5, Timestamp.valueOf(file.getDataUltimaModifica()));
            stmt.setInt(6, idAttivita);

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }

    @Override
    public List<FileCodice> leggiFilePerAttivita(int idAttivita) {
        String query = "SELECT * FROM fn_leggi_file_attivita(?)";
        List<FileCodice> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAttivita);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome_file");
                    String contenuto = rs.getString("contenuto");
                    String langDb = rs.getString("linguaggio");
                    TipoLinguaggio linguaggioEnum = TipoLinguaggio.valueOf(langDb.toUpperCase());

                    LocalDateTime dataModifica = rs.getTimestamp("data_ultima_modifica").toLocalDateTime();
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
        String query = "{ call pr_aggiorna_file(?, ?, ?, ?, ?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, file.getId());
            stmt.setString(2, file.getNome_file());
            stmt.setString(3, file.getLinguaggio().name());
            stmt.setString(4, file.getContenuto());
            stmt.setTimestamp(5, Timestamp.valueOf(file.getDataUltimaModifica()));

            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void eliminaFile(int id) {
        String query = "{ call pr_elimina_file(?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}