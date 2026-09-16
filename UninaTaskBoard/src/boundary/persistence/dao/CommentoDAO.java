package boundary.persistence.dao;

import entity.Commento;
import java.util.List;

public interface CommentoDAO {
    
    int creaCommento(Commento commento, int idAttivita);
    List<Commento> leggiCommentiPerAttivita(int idAttivita);
    void eliminaCommento(int id);
}