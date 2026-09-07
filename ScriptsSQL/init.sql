-- ==============================================================================
-- Script di Inizializzazione Database
-- ==============================================================================
-- Questo script genera l'intera struttura fisica del database in PostgreSQL.
-- L'architettura è suddivisa in quattro fasi per garantire una compilazione sicura:
-- 1. Definizione dei Tipi (ENUM)
-- 2. Creazione delle Tabelle (Primary Keys e attributi base)
-- 3. Definizione dei Vincoli Relazionali (Foreign Keys in coda)
-- 4. Definizione Trigger e Logica di Business
-- ==============================================================================


-- ==============================================================================
-- 1. DEFINIZIONE ENUM(s)
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
-- 2. DEFINIZIONE TABELLE
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
-- 3. DEFINIZIONE DELLE CHIAVI ESTERNE (FOREIGN KEYS)
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

-- ==============================================================================
-- 4. DEFINIZIONE TRIGGER E FUNZIONI
-- ==============================================================================

-- TRIGGER 1: Sicurezza - Controlla che lo studente sia nel progetto prima di assegnarlo all'attività
CREATE OR REPLACE FUNCTION StudenteExistsOnProject()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM Studente_Progetto ST 
        JOIN Attivita A ON ST.id_progetto = A.id_progetto 
        WHERE ST.matricola_studente = NEW.matricola_studente 
          AND A.id = NEW.id_attivita
    ) THEN 
        RAISE EXCEPTION 'Utente non registrato nel progetto';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER Controllo_Assegnazioni
BEFORE INSERT ON Studente_Attivita
FOR EACH ROW
EXECUTE FUNCTION StudenteExistsOnProject();


-- TRIGGER 2: Business Logic - Aggiorna in automatico lo stato del progetto in base alle attività
CREATE OR REPLACE FUNCTION AutoUpdateProjectStatus()
RETURNS TRIGGER AS $$
DECLARE 
    TotaleAttivita INT;
    AttivitaCompletate INT;
BEGIN 
    -- 1. Controllo chiusura progetto
    IF (NEW.Stato = 'Completata') THEN
        SELECT COUNT(*) INTO AttivitaCompletate FROM Attivita WHERE id_progetto = NEW.id_progetto AND Stato = 'Completata'; 
        SELECT COUNT(*) INTO TotaleAttivita FROM Attivita WHERE id_progetto = NEW.id_progetto;
        
        IF (TotaleAttivita > 0 AND AttivitaCompletate = TotaleAttivita) THEN
            UPDATE Progetto SET Stato = 'Completato' WHERE id = NEW.id_progetto;
        END IF;
    END IF;

    -- 2. Controllo apertura/riapertura progetto
    IF (NEW.Stato = 'In_Corso') THEN
        UPDATE Progetto 
        SET Stato = 'Attivo' 
        WHERE id = NEW.id_progetto 
          AND Stato IN ('Creato', 'Sospeso', 'Completato');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_StatoProgetto
AFTER UPDATE OF Stato ON Attivita
FOR EACH ROW
EXECUTE FUNCTION AutoUpdateProjectStatus();




