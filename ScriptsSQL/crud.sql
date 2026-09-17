-- Studente

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE OR REPLACE FUNCTION fn_crea_studente(
    p_matricola VARCHAR(9),
    p_nome VARCHAR(255),
    p_cognome VARCHAR(255),
    p_hpassword VARCHAR(255)
) RETURNS VARCHAR AS $$
BEGIN
    INSERT INTO Studente (Matricola, Nome, Cognome, hashed_password)
    VALUES (p_matricola, p_nome, p_cognome, encode(digest(p_hpassword, 'sha256'), 'hex'));
-- Scelta architetturale: Abbiamo delegato l'hashing SHA-256 al database 
-- tramite 'pgcrypto'. Nonostante l'hashing lato client sia preferibile per la 
-- sicurezza in transito, questa soluzione centralizza tutta la logica di 
-- sicurezza direttamente all'interno di PostgreSQL.
    RETURN p_matricola;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_leggi_studenti()
RETURNS TABLE (
    Matricola VARCHAR(9),
    Nome VARCHAR(255),
    Cognome VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT s.Matricola, s.Nome, s.Cognome
    FROM Studente s;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_leggi_studente_matricola(
    p_matricola VARCHAR(9)
) RETURNS TABLE (
    Matricola VARCHAR(9),
    Nome VARCHAR(255),
    Cognome VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT s.Matricola, s.Nome, s.Cognome
    FROM Studente s
    WHERE s.Matricola = p_matricola;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_aggiorna_studente(
    IN p_matricola VARCHAR(9),
    IN p_nome VARCHAR(255),
    IN p_cognome VARCHAR(255),
    IN p_hpassword VARCHAR(255)
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Studente
    SET Nome = p_nome,
        Cognome = p_cognome,
        hashed_password = p_hpassword
    WHERE Matricola = p_matricola;
END;
$$;


CREATE OR REPLACE PROCEDURE pr_elimina_studente(
    IN p_matricola VARCHAR(9)
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Studente
    WHERE Matricola = p_matricola;
END;
$$;


-- Progetto

CREATE OR REPLACE FUNCTION fn_crea_progetto(
    p_scadenza DATE
) RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    INSERT INTO Progetto (Scadenza)
    VALUES (p_scadenza)
    RETURNING id INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_leggi_progetto_id(
    p_id INT
) RETURNS TABLE (
    id INT,
    scadenza DATE,
    stato VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.Scadenza, p.Stato::VARCHAR
    FROM Progetto p
    WHERE p.id = p_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_aggiorna_progetto(
    IN p_id INT,
    IN p_scadenza DATE,
    IN p_stato VARCHAR
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Progetto
    SET Scadenza = p_scadenza,
        Stato = p_stato::StatoAttivita
    WHERE id = p_id;
END;
$$;


CREATE OR REPLACE PROCEDURE pr_elimina_progetto(
    IN p_id INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Progetto
    WHERE id = p_id;
END;
$$;


-- Attivita

CREATE OR REPLACE FUNCTION fn_crea_attivita(
    p_descrizione TEXT,
    p_tipo VARCHAR,
    p_data_scadenza TIMESTAMP,
    p_id_progetto INT
) RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    INSERT INTO Attivita (Descrizione, Tipo, DataScadenza, id_progetto) 
    VALUES (p_descrizione::valid_text, p_tipo::TipoAttivita, p_data_scadenza, p_id_progetto)
    RETURNING id INTO v_id;
    
    RETURN v_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_leggi_attivita_progetto(
    p_id_progetto INT
) RETURNS TABLE (
    id INT,
    descrizione TEXT,
    tipo VARCHAR,
    data_creazione TIMESTAMP,
    data_scadenza TIMESTAMP,
    stato VARCHAR
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.id,
        a.Descrizione::TEXT,
        a.Tipo::VARCHAR,
        a.DataCreazione,
        a.DataScadenza,
        a.Stato::VARCHAR
    FROM Attivita a
    WHERE a.id_progetto = p_id_progetto;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE PROCEDURE pr_aggiorna_attivita(
    IN p_id INT,
    IN p_descrizione TEXT,
    IN p_data_scadenza TIMESTAMP,
    IN p_stato VARCHAR
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE Attivita
    SET Descrizione = p_descrizione::valid_text,
        DataScadenza = p_data_scadenza,
        Stato = p_stato::StatoAttivita
    WHERE id = p_id;
END;
$$;

CREATE OR REPLACE PROCEDURE pr_elimina_attivita(
    IN p_id INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Attivita
    WHERE id = p_id;
END;
$$;


-- File di codice
CREATE OR REPLACE FUNCTION fn_crea_filecodice(
    p_nome_file VARCHAR(255),
    p_contenuto TEXT,
    p_linguaggio VARCHAR,
    p_id_attivita INT
) RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    INSERT INTO FileCodice (
        nome_file,
        contenuto,
        linguaggio,
        id_attivita
    )
    VALUES (
        p_nome_file,
        p_contenuto::valid_text,
        p_linguaggio::TipoLinguaggio,
        p_id_attivita
    )
    RETURNING id_file INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_leggi_file_attivita(
    p_id_attivita INT
) RETURNS TABLE (
    id_file INT,
    nome_file VARCHAR(255),
    contenuto TEXT,
    linguaggio VARCHAR,
    data_modifica TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        f.id_file,
        f.nome_file,
        f.contenuto::TEXT,
        f.linguaggio::VARCHAR,
        f.DataUltimaModifica
    FROM FileCodice f
    WHERE f.id_attivita = p_id_attivita;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_aggiorna_filecodice(
    IN p_id_file INT,
    IN p_contenuto TEXT
) LANGUAGE plpgsql AS $$
BEGIN
    UPDATE FileCodice
    SET contenuto = p_contenuto::valid_text,
        DataUltimaModifica = CURRENT_TIMESTAMP
    WHERE id_file = p_id_file;
END;
$$;


CREATE OR REPLACE PROCEDURE pr_elimina_filecodice(
    IN p_id_file INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM FileCodice
    WHERE id_file = p_id_file;
END;
$$;


-- Commento

CREATE OR REPLACE FUNCTION fn_crea_commento(
    p_testo TEXT,
    p_matricola VARCHAR(9),
    p_id_attivita INT
) RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    INSERT INTO Commento (
        Testo,
        matricola_studente,
        id_attivita
    )
    VALUES (
        p_testo::valid_text,
        p_matricola,
        p_id_attivita
    )
    RETURNING id INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_leggi_commenti_attivita(
    p_id_attivita INT
) RETURNS TABLE (
    id INT,
    data_commento TIMESTAMP,
    testo TEXT,
    matricola_studente VARCHAR(9)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        c.id,
        c.DataCommento,
        c.Testo::TEXT,
        c.matricola_studente
    FROM Commento c
    WHERE c.id_attivita = p_id_attivita
    ORDER BY c.DataCommento ASC;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_elimina_commento(
    IN p_id INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Commento
    WHERE id = p_id;
END;
$$;


-- Revisione

CREATE OR REPLACE FUNCTION fn_crea_revisione(
    p_nota TEXT,
    p_id_filecodice INT,
    p_matricola VARCHAR(9)
) RETURNS INT AS $$
DECLARE
    v_id INT;
BEGIN
    INSERT INTO Revisione (
        Nota,
        id_filecodice,
        matricola_studente
    )
    VALUES (
        p_nota::valid_text,
        p_id_filecodice,
        p_matricola
    )
    RETURNING id INTO v_id;

    RETURN v_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fn_leggi_revisioni_file(
    p_id_filecodice INT
) RETURNS TABLE (
    id INT,
    data TIMESTAMP,
    nota TEXT,
    matricola_studente VARCHAR(9)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        r.id,
        r.Data,
        r.Nota::TEXT,
        r.matricola_studente
    FROM Revisione r
    WHERE r.id_filecodice = p_id_filecodice
    ORDER BY r.Data DESC;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_elimina_revisione(
    IN p_id INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Revisione
    WHERE id = p_id;
END;
$$;


-- Studente_Progetto

CREATE OR REPLACE FUNCTION fn_assegna_studente_progetto(
    p_matricola VARCHAR(9),
    p_id_progetto INT
) RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO Studente_Progetto (
        matricola_studente,
        id_progetto
    )
    VALUES (
        p_matricola,
        p_id_progetto
    );

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_rimuovi_studente_progetto(
    IN p_matricola VARCHAR(9),
    IN p_id_progetto INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Studente_Progetto
    WHERE matricola_studente = p_matricola
      AND id_progetto = p_id_progetto;
END;
$$;


-- Studente_Attivita

CREATE OR REPLACE FUNCTION fn_assegna_studente_attivita(
    p_matricola VARCHAR(9),
    p_id_attivita INT
) RETURNS BOOLEAN AS $$
BEGIN
    INSERT INTO Studente_Attivita (
        matricola_studente,
        id_attivita
    )
    VALUES (
        p_matricola,
        p_id_attivita
    );

    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE PROCEDURE pr_rimuovi_studente_attivita(
    IN p_matricola VARCHAR(9),
    IN p_id_attivita INT
) LANGUAGE plpgsql AS $$
BEGIN
    DELETE FROM Studente_Attivita
    WHERE matricola_studente = p_matricola
      AND id_attivita = p_id_attivita;
END;
$$;
CREATE OR REPLACE FUNCTION fn_leggi_studenti_progetto(
    p_id_progetto INT
) RETURNS TABLE (
    Matricola VARCHAR(9),
    Nome VARCHAR(255),
    Cognome VARCHAR(255)
) AS $$
BEGIN
    RETURN QUERY
    SELECT s.Matricola, s.Nome, s.Cognome
    FROM Studente s
    JOIN Studente_Progetto sp ON s.Matricola = sp.matricola_studente
    WHERE sp.id_progetto = p_id_progetto;
END;
$$ LANGUAGE plpgsql;