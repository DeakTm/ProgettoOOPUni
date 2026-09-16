package entity;

import java.time.LocalDateTime;

public class Commento {
	private int id;
	private LocalDateTime DataCommento;
	private String Testo;
	
	private Studente matricola;
	private Attivita id_attivita;
	
	public Commento(int id, LocalDateTime DataCommento, String Testo, Studente matricola, Attivita id_attivita) {
		this.id = id;
		this.DataCommento = DataCommento;
		this.Testo = Testo;
		this.matricola = matricola;
		this.id_attivita = id_attivita;
	}
	
	public Commento(LocalDateTime DataCommento, String Testo, Studente matricola, Attivita id_attivita) {
		this.DataCommento = DataCommento;
		this.Testo = Testo;
		this.matricola = matricola;
		this.id_attivita = id_attivita;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getDataCommento() {
		return DataCommento;
	}

	public void setDataCommento(LocalDateTime dataCommento) {
		DataCommento = dataCommento;
	}

	public String getTesto() {
		return Testo;
	}

	public void setTesto(String testo) {
		Testo = testo;
	}

	public Studente getMatricola() {
		return matricola;
	}

	public void setStudente(Studente matricola) {
		matricola = matricola;
	}

	public Attivita getId_attivita() {
		return id_attivita;
	}

	public void setId_attivita(Attivita id_attivita) {
		this.id_attivita = id_attivita;
	}
}

