package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;        
import entity.enums.StatoAvanzamento;

public class Progetto {
	private int id;
	private StatoAvanzamento Stato;
	private LocalDate Scadenza;
	private List<Attivita> List_attivita = new ArrayList<>();
	
	public Progetto(int id, StatoAvanzamento Stato, LocalDate Scadenza ) {
		this.id = id;
		this.Stato = Stato;
		this.Scadenza = Scadenza;
	}
	
	public Progetto( StatoAvanzamento Stato, LocalDate Scadenza ) {
		this.Stato = Stato;
		this.Scadenza = Scadenza;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StatoAvanzamento getStato() {
		return Stato;
	}

	public void setStato(StatoAvanzamento stato) {
		Stato = stato;
	}

	public LocalDate getScadenze() {
		return Scadenza;
	}

	public void setScadenze(LocalDate scadenza) {
		Scadenza = scadenza;
	}

	public List<Attivita> getList_attivita() {
		return List_attivita;
	}

	public void setList_attivita(List<Attivita> list_attivita) {
		List_attivita = list_attivita;
	}
}
