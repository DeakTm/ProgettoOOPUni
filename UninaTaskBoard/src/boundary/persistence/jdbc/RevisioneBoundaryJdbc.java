package boundary.persistence.jdbc;

import boundary.persistence.dao.RevisioneDAO;
import entity.FileCodice;
import entity.Revisione;
import entity.Studente;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RevisioneBoundaryJdbc implements RevisioneDAO {

    @Override
    public int creaRevisione(Revisione revisione, int idFile, String matricolaRevisore) {
        String query = "{ ? = call fn_crea_revisione(?, ?, ?) }";
        int idGenerato = -1;

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (CallableStatement stmt = conn.prepareCall(query)) {

            stmt.registerOutParameter(1, Types.INTEGER);
            stmt.setString(2, revisione.getNota());
            stmt.setInt(3, idFile);
            stmt.setString(4, matricolaRevisore);

            stmt.execute();
            idGenerato = stmt.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerato;
    }

    @Override
    public List<Revisione> leggiRevisioniPerFile(int idFile) {
        return getRevisioniFile(idFile);
    }

    @Override
    public List<Revisione> getRevisioniFile(int idFileCodice) {
        List<Revisione> lista = new ArrayList<>();
        String query = "SELECT * FROM fn_leggi_revisioni_file(?)";

        Connection conn = DatabaseManager.getDatabaseManager().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idFileCodice);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Revisione r = new Revisione();
                    r.setId(rs.getInt("id"));

                    Timestamp ts = rs.getTimestamp("data");
                    if (ts != null) {
                        r.setData(ts.toLocalDateTime().toLocalDate());
                    }

                    r.setNota(rs.getString("nota"));

                    FileCodice fc = new FileCodice();
                    fc.setId(idFileCodice);
                    r.setId_filecodice(fc);

                    String matricolaStr = rs.getString("matricola_studente");
                    if (matricolaStr != null) {
                        Studente s = new Studente();
                        s.setMatricola(matricolaStr);
                        r.setMatricola(s);
                    }

                    lista.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}