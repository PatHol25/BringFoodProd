package com.example.demo;

import static matchingAlgo.Database.DatabaseHandler.*;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseHandlerTests {

    public static void main(String[] args) throws Exception {

        final String TABLE = "buyers";
        System.out.println("---- TESTLAUF ----");

        // ­Schema säubern
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS " + TABLE);
        }

        // 1) Tabelle + Indexe anlegen
        createUserTable(TABLE);
        System.out.println("Tabelle erstellt.");

        /*
        // 2) Zwei Test-User einfügen
        addUser(TABLE, "u1", "g1_1", 1,2,  3,4, 5,6, 1.0);
        addUser(TABLE, "u2", "g1_1", 7,8,  9,1, 2,3, 1.0);
        System.out.println("2 User eingefügt. Gesamt: " + getTotalUserCount(TABLE));

        // 3) getUsersByGrid
        List<User> list = getUsersByGrid(TABLE, "g1_1");
        System.out.println("getUsersByGrid('g1_1') liefert " + list.size() + " Einträge.");

        // 4) Einzelsuche
        User u = getUserInGrid(TABLE, "g1_1", "u1");
        System.out.println("getUserInGrid => " + (u != null ? u.getId() : "null"));

        // 5) Reservierung
        boolean ok = reserveBuyer(TABLE, "g1_1", "u1", "shopper42");
        System.out.println("reserveBuyer => " + ok);

        // 6) Löschen von u2
        deleteUser(TABLE, "g1_1", "u2");
        System.out.println("Nach deleteUser Gesamt: " + getTotalUserCount(TABLE));

        */

        // 7) Aufräumen & Pool schließen
        try (Connection c = getConnection(); Statement s = c.createStatement()) {
            s.execute("DROP TABLE IF EXISTS " + TABLE);
        }

        shutdownPool();
        System.out.println("---- ENDE ----");
    }
}
