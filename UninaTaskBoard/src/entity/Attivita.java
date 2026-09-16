package entity;

import java.time.LocalDate;
import java.util.List; 
import entity.enums.StatoAttivita;
import java.util.ArrayList;
import entity.enums.TipoAttivita;
import java.time.LocalDateTime;

public class Attivita {
	private int id;
	private String Descrizione;
	private LocalDateTime DataCreazione;
	private LocalDate DataScadenza;
	private StatoAttivita Stato;
	private TipoAttivita Tipo;
	
	private List<Studente> Studenti = new ArrayList<>();
	private List<FileCodice> List_FileCodice = new ArrayList<>();
	
	private Progetto id_progetto;
	

	public Attivita(int id, String Descrizione,LocalDateTime DataCreazione,LocalDate DataScadenza, StatoAttivita Stato, TipoAttivita Tipo, Progetto id_progetto) {
		this.id = id;
		this.Descrizione = Descrizione;
		this.DataCreazione = DataCreazione;
		this.DataScadenza = DataScadenza;
		this.Stato = Stato;
		this.Tipo = Tipo; 
		this.id_progetto = id_progetto;
	}
 
	public Attivita(String Descrizione, LocalDateTime DataCreazione, LocalDate DataScadenza, StatoAttivita Stato, TipoAttivita Tipo) {
		this.Descrizione = Descrizione;
		this.DataCreazione = DataCreazione;
		this.DataScadenza = DataScadenza;
		this.Stato = Stato;
		this.Tipo = Tipo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescrizione() {
		return Descrizione;
	}

	public void setDescrizione(String descrizione) {
		Descrizione = descrizione;
	}

	public LocalDateTime getDataCreazione() {
		return DataCreazione;
	}

	public void setDataCreazione(LocalDateTime dataCreazione) {
		DataCreazione = dataCreazione;
	}

	public StatoAttivita getStato() {
		return Stato;
	}

	public void setStato(StatoAttivita stato) {
		Stato = stato;
	}

	public List<Studente> getStudenti() {
		return Studenti;
	}

	public void setStudenti(List<Studente> studenti) {
		Studenti = studenti;
	}

	public List<FileCodice> getList_FileCodice() {
		return List_FileCodice;
	}

	public void setList_FileCodice(List<FileCodice> list_FileCodice) {
		List_FileCodice = list_FileCodice;
	}

	public Progetto getId_progetto() {
		return id_progetto;
	}

	public void setId_progetto(Progetto id_progetto) {
		this.id_progetto = id_progetto;
	}

	public TipoAttivita getTipo() {
		return Tipo;
	}

	public void setTipo(TipoAttivita tipo) {
		this.Tipo = tipo;
	}

	public LocalDate getDataScadenza() {
		return DataScadenza;
	}

	public void setDataScadenza(LocalDate dataScadenza) {
		DataScadenza = dataScadenza;
	}
}
