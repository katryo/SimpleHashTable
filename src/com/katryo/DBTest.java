package com.katryo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;

class DBTest {
    @org.junit.jupiter.api.Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new DB(-1));
        assertThrows(IllegalArgumentException.class, () -> new DB(0));
        new DB(1);
    }

    @org.junit.jupiter.api.Test
    void set() {
        DB db = new DB(2);
        assertEquals(true, db.set("SW Episode", 8));
    }

    @org.junit.jupiter.api.Test
    void get() {
        DB db = new DB(1);
        assertEquals(null, db.get("Jonathan"));

        db.set("Joseph", "Hermit Purple");
        assertEquals("Hermit Purple", db.get("Joseph"));

        assertEquals(null, db.get("George"));
    }

    @org.junit.jupiter.api.Test
    void delete() {
        DB db = new DB(10);
        assertEquals(null, db.delete("Nothing"));

        db.set("citadel", 'D');

        assertEquals('D', db.delete("citadel"));
        assertEquals(null, db.get("citadel"));
        assertEquals(null, db.delete( "citadel"));
    }

    @org.junit.jupiter.api.Test
    void load() {
        DB db = new DB(100);
        assertEquals(0.0, db.load());
        db.set("Jotaro", "Star Platinum");
        db.set("Kakyoin", 17);
        assertEquals(0.02, db.load());

        db.delete("Kakyoin");
        assertEquals(0.01, db.load());
    }

    @Test
    void testCollision() {
        DB db = new DB(2);
        // If the DB's size is 2, "a" and "bibibi" collides.
        db.set("a", "abcd");
        db.set("bibibi", 128);
        assertEquals(1.0, db.load());
        assertEquals("abcd", db.get("a"));
        assertEquals(128, db.get("bibibi"));
    }

    @Test
    void testSetMoreThanLimit() {
        DB db = new DB(3);
        db.set("Ora", 1);
        db.set("OraOra", 2);
        db.set("OraOraOra", 3);
        assertEquals(false, db.set("OraOraOraOra", 4));
        assertEquals(false, db.set("OraOraOraOraOra", 5));
    }

    @Test
    void testSetSomeMixedObjects() {
        DB db = new DB(4);
        db.set("Johnny", "Tusk");
        db.set("Johnny's age", 19);
        db.set("Chapter", '7');

        ArrayList<Integer> items = new ArrayList<>();
        items.add(1);
        items.add(4);
        items.add(27);
        db.set("Items", items);

        assertEquals("Tusk", db.get("Johnny"));
        assertEquals(19, db.get("Johnny's age"));
        assertEquals('7', db.get("Chapter"));

        ArrayList<Integer> obtainedItems = (ArrayList<Integer>) db.get("Items");
        assertEquals(1, (int) obtainedItems.get(0));
        assertEquals(4, (int) obtainedItems.get(1));
        assertEquals(27, (int) obtainedItems.get(2));
    }
}