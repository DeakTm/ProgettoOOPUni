package entity;

import java.time.LocalDate;
import java.util.List; 
import entity.enums.StatoAttivita;
import java.util.ArrayList;
import entity.enums.TipoAttivita;

public class Attivita {
	private int id;
	private String Descrizione;
	private LocalDate DataCreazione;
	private LocalDate DataScadenza;
	private StatoAttivita Stato;
	private TipoAttivita Tipo;
	
	private List<Studente> Studenti = new ArrayList<>();
	private List<FileCodice> List_FileCodice = new ArrayList<>();
	
	private Progetto progetto;
	

	public Attivita(int id, String Descrizione,LocalDate DataCreazione,LocalDate DataScadenza, StatoAttivita Stato, TipoAttivita Tipo, Progetto progetto) {
		this.id = id;
		this.Descrizione = Descrizione;
		this.DataCreazione = DataCreazione;
		this.DataScadenza = DataScadenza;
		this.Stato = Stato;
		this.Tipo = Tipo; 
		this.progetto = progetto;
	}
 
	public Attivita(String Descrizione, LocalDate DataCreazione, LocalDate DataScadenza, StatoAttivita Stato, TipoAttivita Tipo) {
		this.Descrizione = Descrizione;
		this.DataCreazione = DataCreazione;
		this.DataScadenza = DataScadenza;
		this.Stato = Stato;
		this.Tipo = Tipo;
	}
	public Attivita(int id,String Descrizione, LocalDate DataCreazione, LocalDate DataScadenza, StatoAttivita Stato, TipoAttivita Tipo) {
		this.id = id;
		this.Descrizione = Descrizione;
		this.DataCreazione = DataCreazione;
		this.DataScadenza = DataScadenza;
		this.Stato = Stato;
		this.Tipo = Tipo;
	}
	public Attivita() {
		
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

	public LocalDate getDataCreazione() {
		return DataCreazione;
	}

	public void setDataCreazione(LocalDate dataCreazione) {
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

	public Progetto getProgetto() {
		return progetto;
	}

	public void setProgetto(Progetto progetto) {
		this.progetto = progetto;
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
