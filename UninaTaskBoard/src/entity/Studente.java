package entity;

import java.util.ArrayList;
import java.util.List; 

public class Studente {
	private String Matricola;
	private String Nome;
	private String Cognome;
	private String hashed_password;
	
	private List<Progetto> Progetti = new ArrayList<>();
	
	public Studente (String Matricola, String Nome, String Cognome, String hashed_password) {
		this.Matricola = Matricola;
		this.Nome = Nome;
		this.Cognome = Cognome;
		this.hashed_password = hashed_password;
	}
	
	public Studente (String Matricola, String Nome, String Cognome) {
		this.Matricola = Matricola;
		this.Nome = Nome;
		this.Cognome = Cognome;
	}
	

	public String getMatricola() {
		return Matricola;
	}

	public void setMatricola(String matricola) {	
		Matricola = matricola;
	}

	public String getNome() {
		return Nome;
	}

	public void setNome(String nome) {
		Nome = nome;
	}

	public String getCognome() {
		return Cognome;
	}

	public void setCognome(String cognome) {
		Cognome = cognome;
	}

	public String getHashed_password() {
		return hashed_password;
	}

	public void setHashed_password(String hashed_password) {
		this.hashed_password = hashed_password;
	}

	public List<Progetto> getProgetti() {
		return Progetti;
	}

	public void setProgetti(List<Progetto> progetti) {
		Progetti = progetti;
	}
}

