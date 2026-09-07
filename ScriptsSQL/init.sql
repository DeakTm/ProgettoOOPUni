-- ==============================================================================
-- Script di Inizializzazione Database
-- ==============================================================================
-- Questo script genera l'intera struttura fisica del database in PostgreSQL.
-- L'architettura è suddivisa in tre fasi per garantire una compilazione sicura:
-- 1. Definizione dei Tipi (ENUM)
-- 2. Creazione delle Tabelle (Primary Keys e attributi base)
-- 3. Definizione dei Vincoli Relazionali (Foreign Keys in coda)
-- ==============================================================================


-- ==============================================================================
-- DEFINIZIONE ENUM(s)
-- ==============================================================================

CREATE TYPE stato_avanzamento AS ENUM (
	'Creato',
	'Attivo',
	'Completato',
	'Sospeso',
	'Annullato'
);

CREATE TYPE tipo_attivita AS ENUM(
	'Sviluppo',
	'Documentazione'
);

CREATE TYPE stato_attivita AS ENUM(
	'Non_Iniziata',
	'In_Corso',
	'Completata'
);

CREATE TYPE tipo_linguaggio AS ENUM(
	'Java',
	'C',
	'C++',
	'Python',
	'HTML',
	'CSS',
	'SQL',
	'Altro'
);

-- ==============================================================================
-- DEFINIZIONE TABELLE
-- ==============================================================================

CREATE TABLE Studente(
Matricola VARCHAR(255) PRIMARY KEY,
Nome VARCHAR(255),
Cognome VARCHAR(255),
hashed_password VARCHAR(255)
);

CREATE TABLE Progetto(
id SERIAL PRIMARY KEY,
Scadenza DATE,
Stato stato_avanzamento
);

CREATE TABLE Studente_Progetto(
matricola_studente VARCHAR(255),
id_progetto INT,
PRIMARY KEY (matricola_studente, id_progetto)
);

CREATE TABLE Attivita(
id SERIAL PRIMARY KEY,
Descrizione TEXT,
Tipo tipo_attivita,
DataCreazione TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
Stato stato_attivita DEFAULT 'Non_Iniziata',
id_progetto INT
);

CREATE TABLE Studente_Attivita(
matricola_studente VARCHAR(255),
id_attivita INT,
PRIMARY KEY(matricola_studente, id_attivita)
);

CREATE TABLE Commento(
id SERIAL PRIMARY KEY,
DataCommento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
Testo TEXT,
matricola_studente VARCHAR(255),
id_attivita INT
);

CREATE TABLE Revisione (
id SERIAL PRIMARY KEY,
Data TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
Nota TEXT,
id_filecodice INT,
matricola_studente VARCHAR(255)
);

CREATE TABLE FileCodice(
id_file SERIAL PRIMARY KEY,
nome_file VARCHAR(255) NOT NULL,
contenuto TEXT,
linguaggio tipo_linguaggio DEFAULT 'Altro',
id_attivita INT
);

-- ==============================================================================
-- DEFINIZIONE DELLE CHIAVI ESTERNE (FOREIGN KEYS)
-- ==============================================================================
-- I vincoli relazionali vengono definiti tutti insieme alla fine dello script.
-- Questo approccio (aggiunta a posteriori tramite ALTER TABLE) evita errori 
-- di dipendenza incrociata e "missing links", permettendo di creare prima 
-- tutte le tabelle in modo indipendente senza preoccuparsi dell'ordine gerarchico.
-- ==============================================================================
+

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
-- ==============================================================================
-- Definizione Trigger
-- ==============================================================================

CREATE FUNCTION StudenteExistsOnProject()
RETURNS TRIGGER AS $$
BEGIN
IF NOT EXISTS (SELECT 1 FROM Studente_Progetto ST JOIN Attivita ON ST.id_Progetto = Attivita.id_progetto WHERE ST.matricola_studente = NEW.matricola_studente AND Attivita.id = NEW.id_Attivita)
THEN 
RAISE EXCEPTION 'Utente non registrato nel progetto';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER Controllo_Assegnazioni
BEFORE 
INSERT ON Studente_Attivita
FOR EACH ROW
EXECUTE FUNCTION StudenteExistsOnProject();