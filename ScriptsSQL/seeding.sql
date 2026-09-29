
-- POPOLAMENTO DATI DI PROVA 


-- Studenti (L'hashing SHA-256 della password viene gestito direttamente dalla funzione PL/pgSQL)
SELECT fn_crea_studente('S00000001', 'Mario', 'Rossi', 'password123');
SELECT fn_crea_studente('S00000002', 'Luigi', 'Verdi', 'password123');
SELECT fn_crea_studente('S00000003', 'Giulia', 'Bianchi', 'password123');
SELECT fn_crea_studente('S00000004', 'Francesca', 'Neri', 'password123');
SELECT fn_crea_studente('S00000005', 'Alessandro', 'Gialli', 'password123');
SELECT fn_crea_studente('S00000006', 'Chiara', 'Romano', 'password123');
SELECT fn_crea_studente('S00000007', 'Davide', 'Colombo', 'password123');
SELECT fn_crea_studente('S00000008', 'Elena', 'Ferrari', 'password123');
SELECT fn_crea_studente('adminUTB', 'Admin', 'Sistema', 'root');

-- Progetti (Con scadenze nel futuro per rispettare i vincoli dei trigger)
SELECT fn_crea_progetto('Progetto Alpha', '2026-12-31'); -- ID 1
SELECT fn_crea_progetto('Progetto Beta', '2027-06-30'); -- ID 2
SELECT fn_crea_progetto('Progetto Gamma', '2027-09-15'); -- ID 3
SELECT fn_crea_progetto('Progetto Delta', '2028-01-20'); -- ID 4

-- Associazioni Studenti - Progetti
-- Team Progetto 1
SELECT fn_assegna_studente_progetto('S00000001', 1);
SELECT fn_assegna_studente_progetto('S00000002', 1);
SELECT fn_assegna_studente_progetto('S00000003', 1);
SELECT fn_assegna_studente_progetto('S00000004', 1);

-- Team Progetto 2
SELECT fn_assegna_studente_progetto('S00000005', 2);
SELECT fn_assegna_studente_progetto('S00000006', 2);
SELECT fn_assegna_studente_progetto('S00000001', 2);

-- Team Progetto 3
SELECT fn_assegna_studente_progetto('S00000007', 3);
SELECT fn_assegna_studente_progetto('S00000008', 3);
SELECT fn_assegna_studente_progetto('S00000002', 3);

-- Team Progetto 4
SELECT fn_assegna_studente_progetto('S00000003', 4);
SELECT fn_assegna_studente_progetto('S00000005', 4);
SELECT fn_assegna_studente_progetto('S00000008', 4);

-- Attività per i Progetti
-- Attività Progetto 1
SELECT fn_crea_attivita('Setup iniziale architettura EBC e database', 'Sviluppo', '2026-10-10 18:00:00', 1); -- ID 1
SELECT fn_crea_attivita('Progettazione interfaccia grafica JavaFX', 'Sviluppo', '2026-10-25 18:00:00', 1); -- ID 2
SELECT fn_crea_attivita('Stesura documentazione e diagrammi UML', 'Documentazione', '2026-11-15 12:00:00', 1); -- ID 3

-- Attività Progetto 2
SELECT fn_crea_attivita('Analisi dei requisiti funzionali', 'Documentazione', '2027-02-28 12:00:00', 2); -- ID 4
SELECT fn_crea_attivita('Implementazione layer di persistenza JDBC', 'Sviluppo', '2027-04-10 18:00:00', 2); -- ID 5

-- Attività Progetto 3
SELECT fn_crea_attivita('Configurazione ambiente di testing', 'Sviluppo', '2027-07-01 18:00:00', 3); -- ID 6

-- Attività Progetto 4
SELECT fn_crea_attivita('Sviluppo modulo commenti e file', 'Sviluppo', '2027-11-20 18:00:00', 4); -- ID 7

-- Assegnazioni Studenti alle Attività
SELECT fn_assegna_studente_attivita('S00000001', 1);
SELECT fn_assegna_studente_attivita('S00000002', 2);
SELECT fn_assegna_studente_attivita('S00000003', 3);
SELECT fn_assegna_studente_attivita('S00000005', 4);
SELECT fn_assegna_studente_attivita('S00000006', 5);
SELECT fn_assegna_studente_attivita('S00000007', 6);
SELECT fn_assegna_studente_attivita('S00000008', 7);

-- File di Codice (Innescano il trigger che sposta l'attività in 'In_Corso')
SELECT fn_crea_filecodice('Main.java', 'public class Main { public static void main(String[] args) {} }', 'Java', 1);
SELECT fn_crea_filecodice('DatabaseConnection.java', 'public class DatabaseConnection {}', 'Java', 5);
SELECT fn_crea_filecodice('index.html', '<HTML><BODY>Test Interfaccia</BODY></HTML>', 'HTML', 2);
SELECT fn_crea_filecodice('script.py', 'print("Hello EBC")', 'Python', 6);

-- Commenti di prova (Verificati dai trigger di appartenenza al progetto)
SELECT fn_crea_commento('Ottimo lavoro con il setup iniziale!', 'S00000002', 1);
SELECT fn_crea_commento('Ricordatevi di controllare i vincoli dei trigger.', 'S00000004', 1);
SELECT fn_crea_commento('Analisi dei requisiti quasi completata.', 'S00000005', 4);
SELECT fn_crea_commento('Connessione JDBC testata con successo.', 'S00000006', 5);

-- Revisioni dei file di codice
SELECT fn_crea_revisione('Revisionato il metodo main per la gestione delle eccezioni.', 1, 'S00000001');
SELECT fn_crea_revisione('Ottimizzata la connessione JDBC.', 2, 'S00000006');
SELECT fn_crea_revisione('Aggiunti tag HTML mancanti.', 3, 'S00000002');