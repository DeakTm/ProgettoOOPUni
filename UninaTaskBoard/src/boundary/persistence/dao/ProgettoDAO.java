package boundary.persistence.dao;

import entity.Progetto;
import entity.Studente;

import java.util.List;

public interface ProgettoDAO {
    int creaProgetto(Progetto progetto);
    Progetto leggiProgettoPerId(int id);
    void aggiornaProgetto(Progetto progetto);
    void eliminaProgetto(int id);
    boolean assegnaStudenteProgetto(String matricola, int idProgetto);
    void rimuoviStudenteProgetto(String matricola, int idProgetto);
    List<Progetto> getProgettiStudente(String matricola);
    List<Studente> leggiStudentiPerProgetto(int idProgetto);
    List<String> getStudentiByProgetto(int idProgetto);
}