package com.katryo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by katryo on 9/30/17.
 */
class DBTest {
    @org.junit.jupiter.api.Test
    void set() {
        DB db = new DB(10);
        assertEquals(db.set("SW Episode", 8), true);
    }

    @org.junit.jupiter.api.Test
    void get() {
        DB db = new DB(1);
        db.set("Jonathan", "Hermit Purple");
        assertEquals(db.get("Jonathan"), "Hermit Purple");
    }

    @org.junit.jupiter.api.Test
    void delete() {
        DB db = new DB(10);
        db.set("a", "abcd");
        db.set("bibibi", 128);
        db.set("citadel", 'D');

        db.delete("citadel");
        assertEquals(db.get("citadel"), null);
    }

    @org.junit.jupiter.api.Test
    void load() {
        DB db = new DB(100);
        db.set("Jotaro", "Star Platinum");
        db.set("Kakyoin", 17);
        assertEquals(db.load(), 0.02);
    }

    @Test
    void testCollision() {
        DB db = new DB(2);
        // If the DB's size is 2, "a" and "bibibi" collides.
        db.set("a", "abcd");
        db.set("bibibi", 128);
        assertEquals(db.load(), 1.0);
        assertEquals(db.get("a"), "abcd");
        assertEquals(db.get("bibibi"), 128);
    }

    @Test
    void testSetMoreThanLimit() {
        DB db = new DB(3);
        db.set("Ora", 1);
        db.set("OraOra", 2);
        db.set("OraOraOra", 3);
        assertEquals(db.set("OraOraOraOra", 4), false);
    }

}