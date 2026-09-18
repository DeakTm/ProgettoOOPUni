package entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;        
import entity.enums.StatoAvanzamento;

public class Progetto {
	private int id;
	private String nome;
	private StatoAvanzamento Stato;
	private LocalDate Scadenza;
	private List<Attivita> List_attivita = new ArrayList<>();
	
	public Progetto(int id, StatoAvanzamento Stato, LocalDate Scadenza, String nome) {
		this.id = id;
		this.nome= nome;
		this.Stato = Stato;
		this.Scadenza = Scadenza;
	}
	
	public Progetto( StatoAvanzamento Stato, LocalDate Scadenza, String nome) {
		this.Stato = Stato;
		this.Scadenza = Scadenza;
		this.nome = nome;
	}

	public int getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public LocalDate getScadenza() {
		return Scadenza;
	}

	public void setScadenza(LocalDate scadenza) {
		Scadenza = scadenza;
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
	public List<Attivita> getList_attivita() {
		return List_attivita;
	}

	public void setList_attivita(List<Attivita> list_attivita) {
		List_attivita = list_attivita;
	}
	
}
