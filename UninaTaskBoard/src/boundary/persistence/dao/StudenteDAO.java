package boundary.persistence.dao;

import entity.Studente;
import java.util.List;

public interface StudenteDAO {
    String creaStudente(Studente studente, String passwordClairOUnHash);
    List<Studente> leggiTuttiStudenti();
    Studente leggiStudentePerMatricola(String matricola);
    void aggiornaStudente(Studente studente, String nuovaPassword);
    void eliminaStudente(String matricola);
}