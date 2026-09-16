package boundary.persistence.jdbc;

import boundary.persistence.dao.CommentoDAO;
import entity.Commento;
import entity.Studente;
import util.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentoBoundaryJdbc implements CommentoDAO {

    @Override
    public int creaCommento(Commento commento, int idAttivita) {
        String query = "{ ? = call fn_crea_commento(?, ?, ?, ?) }";
        int idGenerato = -1;

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, commento.getTesto());
            stmt.setTimestamp(3, Timestamp.valueOf(commento.getDataCommento()));
            stmt.setString(4, commento.getMatricola().getMatricola());
            stmt.setInt(5, idAttivita);

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }

    @Override
    public List<Commento> leggiCommentiPerAttivita(int idAttivita) {
        String query = "SELECT * FROM fn_leggi_commenti_attivita(?)";
        List<Commento> lista = new ArrayList<>();

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idAttivita);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String testo = rs.getString("testo");
                    LocalDateTime data = rs.getTimestamp("data_commento").toLocalDateTime();
                    
                    String matricolaDb = rs.getString("matricola_studente"); 
                    Studente autoreFantasma = new Studente(matricolaDb, null, null);
                    Commento c = new Commento(id, data, testo, autoreFantasma, null);
                    
                    lista.add(c);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void eliminaCommento(int id) {
        String query = "{ call pr_elimina_commento(?) }";

        try (Connection conn = DatabaseManager.getDatabaseManager().getConnection();
             CallableStatement stmt = conn.prepareCall(query)) {

            stmt.setInt(1, id);
            stmt.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}