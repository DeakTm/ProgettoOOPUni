package boundary.persistence.dao;

import entity.Attivita;
import java.util.List;

public interface AttivitaDAO {
    int creaAttivita(Attivita attivita, int idProgetto);
    List<Attivita> leggiAttivitaPerProgetto(int idProgetto);
    void aggiornaAttivita(Attivita attivita);
    void eliminaAttivita(int id);
    boolean assegnaStudenteAttivita(String matricola, int idAttivita);
    void rimuoviStudenteAttivita(String matricola, int idAttivita);
}