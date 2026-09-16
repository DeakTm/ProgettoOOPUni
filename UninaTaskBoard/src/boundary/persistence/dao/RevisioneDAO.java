package boundary.persistence.dao;

import entity.Revisione;
import java.util.List;

public interface RevisioneDAO {
    
    int creaRevisione(Revisione revisione, int idFile, String matricolaRevisore);
    List<Revisione> leggiRevisioniPerFile(int idFile);
}