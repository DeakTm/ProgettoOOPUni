package boundary.persistence.dao;

import entity.FileCodice;
import java.util.List;

public interface FileCodiceDAO {
    int creaFile(FileCodice file, int idAttivita);
    List<FileCodice> leggiFilePerAttivita(int idAttivita);
    void aggiornaFile(FileCodice file);
    void eliminaFile(int id);
}