package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class DatabaseManager {
    private static DatabaseManager dbcon = null;
    private Connection conn = null;

    private DatabaseManager() {}

    public static DatabaseManager getDatabaseManager() {
        if (dbcon == null) {
            dbcon = new DatabaseManager();
        }
        return dbcon;
    }    
       
    public Connection getConnection() {
        try {
           
            if (conn == null || conn.isClosed()) {
                Properties props = loadProperties();

                conn = DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
                );
                System.out.println("...connessione ottenuta con successo!");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Errore di connessione !");
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * Overload del metodo getConnection().
     * Consente di collegarsi al database impostando un determinato "schema" di lavoro 
     * (una partizione logica in cui sono raggruppate le tabelle).
     * 
     * N.B: Se su pgAdmin non sono stati definiti schemi personalizzati, PostgreSQL 
     * utilizza in automatico lo schema predefinito chiamato "public". In tal caso, 
     * è sufficiente richiamare il metodo getConnection() senza parametri.
     */    
    
    
    public Connection getConnection(String schema_name) {
        System.out.println("Richiesta connessione...");

        if (Objects.equals(schema_name, "") || schema_name == null) {
            throw new RuntimeException("Schema name is empty");
        }

        try {
            if (conn == null || conn.isClosed()) {
                Properties props = loadProperties();

                String baseUrl = props.getProperty("db.url");
                String s_url = baseUrl + "?currentSchema=" + schema_name;

                conn = DriverManager.getConnection(
                    s_url,
                    props.getProperty("db.user"),
                    props.getProperty("db.password")
                );
                System.out.println("...connessione ottenuta!");
            }
        } catch (SQLException | IOException e) {
            System.err.println("Errore di connessione !");
            e.printStackTrace();
        }

        return conn;
    }

    // Metodo di supporto per il caricamento sicuro di db.properties via ClassLoader
    private Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties"))// ClassLoader.getResourceAsStream
        {
            if (input == null) {
                throw new IOException("File db.properties non trovato in src/main/resources!");
            }
            props.load(input);
        }
        return props;
    }
}