package entity;

import java.time.LocalDateTime;

public class Revisione {
	private int id;
	private LocalDateTime Data;
	private String Nota;
	
	private FileCodice id_filecodice;
	private Studente matricola;
	
	public Revisione(int id, LocalDateTime Data, String Nota, FileCodice id_filecodice , Studente matricola) {
		this.id = id;
		this.Data = Data;
		this.Nota = Nota;
		this.id_filecodice = id_filecodice;
		this.matricola = matricola;
	}
	public Revisione(LocalDateTime Data, String Nota, FileCodice id_filecodice , Studente matricola) {
		this.Data = Data;
		this.Nota = Nota;
		this.id_filecodice = id_filecodice;
		this.matricola = matricola;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getData() {
		return Data;
	}

	public void setData(LocalDateTime data) {
		Data = data;
	}

	public String getNota() {
		return Nota;
	}

	public void setNota(String nota) {
		Nota = nota;
	}

	public FileCodice getId_filecodice() {
		return id_filecodice;
	}

	public void setId_filecodice(FileCodice id_filecodice) {
		this.id_filecodice = id_filecodice;
	}

	public Studente getMatricola() {
		return matricola;
	}

	public void setMatricola(Studente matricola) {
		this.matricola = matricola;
	}
	
}
