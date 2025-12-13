package com.example.demo;

import matchingAlgo.Database.DatabaseHandler;
import matchingAlgo.GS.*;
import matchingAlgo.matching.ShopperDatabaseSearcher;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class StressTest {

    // ---------------- Konfiguration ----------------
    private static final int THREADS = 128;
    private static final int REQUESTS_PER_THREAD = 200;

    // Gitter­systeme wie im Haupt­programm
    private static final Random RAND = new Random();
    private static final GridSystem GS_LON = new GridSystem(-180., 180., 0.0125);
    private static final GridSystem GS_LAT = new GridSystem(-90., 90., 0.0125);

    public static void main(String[] args) throws InterruptedException, SQLException {

        // Vorab: schnelle Plausi-Prüfung
        long total = DatabaseHandler.getTotalUserCount("buyers");
        if (total < THREADS * REQUESTS_PER_THREAD) {
            System.err.println("Für den Stresstest liegen zu wenig Käufer in der Tabelle „buyers“.");
            System.err.println("Gefunden: " + total + " Datensätze.");
            return;
        }

        ExecutorService pool = Executors.newFixedThreadPool(THREADS);
        CountDownLatch latch = new CountDownLatch(THREADS);

        long t0 = System.nanoTime();

        for (int i = 0; i < THREADS; i++) {
            pool.submit(() -> {
                try {
                    runRequests();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();          // auf alle warten
        pool.shutdown();
        DatabaseHandler.shutdownPool();

        long t1 = System.nanoTime();
        System.out.println("Alle Threads fertig. Gesamtdauer: " + ((t1 - t0) / 1_000_000) + " ms");
    }

    // ---------------- Ein Task (= mehrfacher Einkaufs-Request) ----------------
    private static void runRequests() throws SQLException {

        UserDatabase buyerDB = new UserDatabase("buyers");
        ShopperDatabaseSearcher searcher = new ShopperDatabaseSearcher(buyerDB, GS_LON, GS_LAT);

        for (int r = 0; r < REQUESTS_PER_THREAD; r++) {
            double fxHomeX = 180;
            double fxHomeY = 90;

            double fxShopX = fxHomeX + RAND.nextDouble() * 0.2 - 0.1;
            double fxShopY = fxHomeY + RAND.nextDouble() * 0.2 - 0.1;

            double fxEndX = fxShopX + RAND.nextDouble() * 0.2 - 0.1;
            double fxEndY = fxShopY + RAND.nextDouble() * 0.2 - 0.1;

            // 1) Fiktiven Shopper erzeugen (Koordinaten irgendwo in der Welt)
            Coordinate home = new Coordinate( fxHomeX, fxHomeY );
            home.setLongtitudeId(GS_LON.numberToGrid(home.getLongtitude()));
            home.setLatitudeId(GS_LAT.numberToGrid(home.getLatitude()));

            Coordinate shop = new Coordinate( fxShopX, fxShopY );
            shop.setLongtitudeId(GS_LON.numberToGrid(shop.getLongtitude()));
            shop.setLatitudeId(GS_LAT.numberToGrid(shop.getLatitude()));

            Coordinate end = new Coordinate( fxEndX, fxEndY );
            end.setLongtitudeId(GS_LON.numberToGrid(shop.getLongtitude()));
            end.setLatitudeId(GS_LAT.numberToGrid(shop.getLatitude()));

            User shopper = new User("S-" + Thread.currentThread().getId() + "-" + r,
                                    home, shop, end);

            // 2) mög­liche Grid-Zellen bestimmen
            int[][] c1 = User.getPossibleGrids(home, shop, 1, 1, GS_LON, GS_LAT, true);
            int[][] c2 = User.getPossibleGrids(home, end,  1, 1, GS_LON, GS_LAT, true);
            int[][] cells = User.mergeGrids(c1, c2);

            // 3) Matching + Reservieren
            User reservedBuyer = null;
            for (User cand : searcher.minDistanceUsers(10, shopper, cells, true)) {
                if (cand == null) break;
                if (DatabaseHandler.reserveBuyer("buyers", cand.getGrid_id(), cand.getId(), shopper.getId())) {
                    reservedBuyer = cand;
                    break;
                }
            }

            // 4) Aufräumen
            if (reservedBuyer != null) {
                DatabaseHandler.deleteUser("buyers", reservedBuyer.getGrid_id(), reservedBuyer.getId());
            }
        }
    }

}
