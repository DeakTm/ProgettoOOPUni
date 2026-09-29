# UninaTaskboard 

UninaTaskboard è un'applicazione desktop in Java per la gestione di progetti, assegnazione di task e version control base per i file di codice.

## Struttura e Architettura
Il codice è stato organizzato seguendo l'euristica **EBC (Entity-Boundary-Control)**:
- **Entity**: classi del dominio applicativo (Studente, Progetto, Attività, ecc.).
- **Boundary**: l'interfaccia utente (UI) sviluppata in JavaFX.
- **Control**: i Controller per la logica applicativa e le classi JDBC/DAO per l'interazione con il database.

## Tecnologie usate
- Java
- JavaFX 
- JDBC (Pattern DAO)
- Database: PostgreSQL

## Setup del database
Per avviare correttamente l'applicazione in locale:
1. Crea un database vuoto.
2. Esegui lo script SQL `schema.sql` per generare le tabelle, successivamente i rimanenti script (`crud.sql`, `triggers.sql` ed anche `seeding.sql` se si vogliono dei dati fittizi).
3. Inserisci le tue credenziali (URL, utente e password) nel file `db.properties.example` e rinominarlo `db.properties`.

## Componenti del gruppo
- Lorenzo Avellino - N86005675
- Fabio De Luca - DE1000296