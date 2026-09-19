package boundary.persistence.dao;

import entity.Attivita;
import java.util.List;

public interface AttivitaDAO {
    int creaAttivita(Attivita attivita);
    List<Attivita> leggiAttivitaPerProgetto(int idProgetto);
    void aggiornaAttivita(Attivita attivita);
    void eliminaAttivita(int id);
    boolean assegnaStudenteAttivita(String matricola, int idAttivita);
    void rimuoviStudenteAttivita(String matricola, int idAttivita);
    boolean inserisciAttivitaConAssegnazione(Attivita attivita, String matricola);
    List<Attivita> getAttivitaStudente(String matricola);
    List<String> getAssegnatariAttivita(int idAttivita);
}