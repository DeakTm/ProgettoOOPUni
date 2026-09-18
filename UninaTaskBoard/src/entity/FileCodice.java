package entity;

import entity.enums.TipoLinguaggio;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;

public class FileCodice {
	private int id;
	private String nome_file;
	private TipoLinguaggio Linguaggio;
	private String Contenuto;
	private LocalDateTime DataUltimaModifica;
	
	private List<Revisione> Revisioni = new ArrayList<>();
	private Attivita id_attivita;
	
	public FileCodice(int id, String nome_file, TipoLinguaggio Linguaggio, String Contenuto, LocalDateTime DataUltimaModifica, Attivita id_attivita) {
		this.id = id;
		this.nome_file = nome_file;
		this.Linguaggio = Linguaggio;
		this.Contenuto = Contenuto;
		this.DataUltimaModifica = DataUltimaModifica;
		this.id_attivita = id_attivita;
	}
	
	public FileCodice( String nome_file, TipoLinguaggio Linguaggio, String Contenuto, LocalDateTime DataUltimaModifica, Attivita id_attivita) {
		this.nome_file = nome_file;
		this.Linguaggio = Linguaggio;
		this.Contenuto = Contenuto;
		this.DataUltimaModifica = DataUltimaModifica;
		this.id_attivita = id_attivita;
	}
	
	public FileCodice() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome_file() {
		return nome_file;
	}

	public void setNome_file(String nome_file) {
		this.nome_file = nome_file;
	}

	public TipoLinguaggio getLinguaggio() {
		return Linguaggio;
	}

	public void setLinguaggio(TipoLinguaggio linguaggio) {
		Linguaggio = linguaggio;
	}

	public String getContenuto() {
		return Contenuto;
	}

	public void setContenuto(String contenuto) {
		Contenuto = contenuto;
	}

	public List<Revisione> getRevisioni() {
		return Revisioni;
	}

	public void setRevisioni(List<Revisione> revisioni) {
		Revisioni = revisioni;
	}

	public Attivita getId_attivita() {
		return id_attivita;
	}

	public void setId_attivita(Attivita id_attivita) {
		this.id_attivita = id_attivita;
	}

	public LocalDateTime getDataUltimaModifica() {
		return DataUltimaModifica;
	}

	public void setDataUltimaModifica(LocalDateTime dataUltimaModifica) {
		DataUltimaModifica = dataUltimaModifica;
	}
	
}
