
-- ==============================================================================
--  DEFINIZIONE TRIGGER E FUNZIONI
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


-- TRIGGER 2: Aggiorna in automatico lo stato del progetto in base alle attività
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


-- TRIGGER 3: Aggiorna lo stato dell'attività in base all'inserimento di un file di codice
CREATE OR REPLACE FUNCTION StartActivityFromUpdate()
RETURNS TRIGGER AS $$
BEGIN 
    UPDATE Attivita
    SET Stato = 'In_Corso' 
    WHERE id = NEW.id_attivita AND Stato = 'Non_Iniziata';
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_StatoAttivita 
AFTER INSERT ON FileCodice
FOR EACH ROW 
EXECUTE FUNCTION StartActivityFromUpdate();


-- TRIGGER 4: Impedisce di aggiungere attività se il progetto è già scaduto
CREATE OR REPLACE FUNCTION ControlloScadenzaProgetto()
RETURNS TRIGGER AS $$
DECLARE
    Scadenza_pro DATE;
BEGIN
    SELECT Scadenza 
    INTO Scadenza_pro
    FROM Progetto
    WHERE id = NEW.id_progetto;
    
    IF (Scadenza_pro < CURRENT_DATE) THEN
        RAISE EXCEPTION 'Il progetto è scaduto!';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_ControlloScadenza
BEFORE INSERT ON Attivita
FOR EACH ROW
EXECUTE FUNCTION ControlloScadenzaProgetto();


-- TRIGGER 5: Autorizza solo chi fa parte del progetto di commentare un'attività
CREATE OR REPLACE FUNCTION ControlloAssegnazioniPerCommentare()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS(
        SELECT 1 
        FROM Studente_Progetto ST 
        JOIN Attivita A ON ST.id_progetto = A.id_progetto 
        WHERE ST.matricola_studente = NEW.matricola_studente 
          AND A.id = NEW.id_attivita
    ) THEN
        RAISE EXCEPTION 'Non fai parte del progetto, NON puoi commentare!';
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_ControlloAssegnazioniPerCommentare
BEFORE INSERT ON Commento
FOR EACH ROW
EXECUTE FUNCTION ControlloAssegnazioniPerCommentare();