package boundary.persistence.jdbc;

import boundary.persistence.dao.RevisioneDAO;
import entity.Revisione;
import entity.Studente;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevisioneBoundaryJdbc implements RevisioneDAO {

    @Override
    public int creaRevisione(Revisione revisione, int idFile, String matricolaRevisore) {
        String query = "{ ? = call fn_crea_revisione(?, ?, ?, ?) }";
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, revisione.getNota());
            stmt.setTimestamp(3, Timestamp.valueOf(revisione.getData()));
            stmt.setInt(4, idFile);
            stmt.setString(5, matricolaRevisore);

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }

    @Override
    public List<Revisione> leggiRevisioniPerFile(int idFile) {
        String query = "SELECT * FROM fn_leggi_revisioni_file(?)";
        List<Revisione> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idFile);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nota = rs.getString("nota");
                    LocalDateTime data = rs.getTimestamp("data_revisione").toLocalDateTime();          
                    String matricolaDb = rs.getString("matricola_studente");
                    Studente revisoreFantasma = new Studente(matricolaDb, null, null);

                    Revisione r = new Revisione(id, data, nota, null, revisoreFantasma);
                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}