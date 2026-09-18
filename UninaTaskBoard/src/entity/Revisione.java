package entity;

import java.time.LocalDate;

public class Revisione {
	private int id;
	private LocalDate Data;
	private String Nota;
	private FileCodice id_filecodice;
	private Studente matricola;
	
	public Revisione(int id, LocalDate Data, String Nota, FileCodice id_filecodice , Studente matricola) {
		this.id = id;
		this.Data = Data;
		this.Nota = Nota;
		this.id_filecodice = id_filecodice;
		this.matricola = matricola;
	}
	public Revisione(LocalDate Data, String Nota, FileCodice id_filecodice , Studente matricola) {
		this.Data = Data;
		this.Nota = Nota;
		this.id_filecodice = id_filecodice;
		this.matricola = matricola;
	}
	
	public Revisione() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDate getData() {
		return Data;
	}

	public void setData(LocalDate data) {
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
