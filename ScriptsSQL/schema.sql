



-- ==============================================================================
-- Script di Inizializzazione Database
-- ==============================================================================
-- Questo script genera l'intera struttura fisica del database in PostgreSQL.
-- L'architettura è suddivisa in quattro fasi per garantire una compilazione sicura:
--  Definizione dei Tipi (ENUM)
--  Creazione delle Tabelle (Primary Keys e attributi base)
--. Definizione dei Vincoli Relazionali (Foreign Keys in coda)
-- ==============================================================================


-- ==============================================================================
-- DEFINIZIONE ENUM
-- ==============================================================================

CREATE TYPE StatoAvanzamento AS ENUM (
	'Creato',
	'Attivo',
	'Completato',
	'Sospeso',
	'Annullato'
);

CREATE TYPE TipoAttivita AS ENUM(
	'Sviluppo',
	'Documentazione'
);

CREATE TYPE StatoAttivita AS ENUM(
	'Non_Iniziata',
	'In_Corso',
	'Completata'
);

CREATE TYPE TipoLinguaggio AS ENUM(
	'Java',
	'C',
	'Cpp',
	'Python',
	'HTML',
	'CSS',
	'SQL',
	'Altro'
);

-- =======================
--  DOMAIN
-- =======================

--Controlla che il testo non sia vuoto per cercare di bypassare il vincolo di NOT NULL
CREATE DOMAIN valid_text AS TEXT
CHECK (
    length(trim(VALUE)) > 0
);


-- ==============================================================================
-- DEFINIZIONE TABELLE
-- ==============================================================================

CREATE TABLE Studente(
    Matricola VARCHAR(9) PRIMARY KEY,
    Nome VARCHAR(255) NOT NULL,
    Cognome VARCHAR(255)NOT NULL,
    hashed_password VARCHAR(255) DEFAULT NULL
);

CREATE TABLE Progetto(
    id SERIAL PRIMARY KEY,
    Scadenza DATE CHECK (Scadenza > CURRENT_DATE),
    Stato StatoAttivita DEFAULT 'Creato'
);

CREATE TABLE Studente_Progetto(
    matricola_studente VARCHAR(9),
    id_progetto INT,
    PRIMARY KEY (matricola_studente, id_progetto)
);

CREATE TABLE Attivita(
    id SERIAL PRIMARY KEY,
    Descrizione valid_text NOT NULL,
    Tipo TipoAttivita NOT NULL,
    DataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Stato StatoAttivita DEFAULT 'Non_Iniziata',
    id_progetto INT
);

CREATE TABLE Studente_Attivita(
    matricola_studente VARCHAR(9),
    id_attivita INT,
    PRIMARY KEY(matricola_studente, id_attivita)
);

CREATE TABLE Commento(
    id SERIAL PRIMARY KEY,
    DataCommento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Testo valid_text NOT NULL,
    matricola_studente VARCHAR(9),
    id_attivita INT
);

CREATE TABLE Revisione (
    id SERIAL PRIMARY KEY,
    Data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Nota valid_text,
    id_filecodice INT,
    matricola_studente VARCHAR(9)
);

CREATE TABLE FileCodice(
    id_file SERIAL PRIMARY KEY,
    nome_file VARCHAR(255) NOT NULL,
    contenuto valid_text,
    linguaggio TipoLinguaggio DEFAULT 'Altro',
    DataUltimaModifica TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_attivita INT
);

-- ==============================================================================
-- DEFINIZIONE DELLE CHIAVI ESTERNE (FOREIGN KEYS)
-- ==============================================================================

-- Studente_Progetto
ALTER TABLE Studente_Progetto
    ADD CONSTRAINT fk_studente FOREIGN KEY(matricola_studente) REFERENCES Studente(Matricola) ON DELETE CASCADE;

ALTER TABLE Studente_Progetto
    ADD CONSTRAINT fk_progetto FOREIGN KEY (id_progetto) REFERENCES Progetto(id) ON DELETE CASCADE;

-- Attività
ALTER TABLE Attivita
    ADD CONSTRAINT fk_progetto FOREIGN KEY(id_progetto) REFERENCES Progetto(id) ON DELETE CASCADE;
  
-- Studente_Attivita
ALTER TABLE Studente_Attivita
    ADD CONSTRAINT fk_studente FOREIGN KEY(matricola_studente) REFERENCES Studente(Matricola) ON DELETE CASCADE;

ALTER TABLE Studente_Attivita
    ADD CONSTRAINT fk_attivita FOREIGN KEY(id_attivita) REFERENCES Attivita(id) ON DELETE CASCADE;

-- Commento
ALTER TABLE Commento
    ADD CONSTRAINT fk_studente FOREIGN KEY(matricola_studente) REFERENCES Studente(Matricola) ON DELETE CASCADE;

ALTER TABLE Commento
    ADD CONSTRAINT fk_attivita FOREIGN KEY(id_attivita) REFERENCES Attivita(id) ON DELETE CASCADE;

-- Revisione
ALTER TABLE Revisione
    ADD CONSTRAINT fk_studente FOREIGN KEY(matricola_studente) REFERENCES Studente(Matricola) ON DELETE CASCADE;

ALTER TABLE Revisione
    ADD CONSTRAINT fk_filecodice FOREIGN KEY(id_filecodice) REFERENCES FileCodice(id_file) ON DELETE CASCADE;

-- FileCodice
ALTER TABLE FileCodice
    ADD CONSTRAINT fk_attivita FOREIGN KEY(id_attivita) REFERENCES Attivita(id) ON DELETE CASCADE;