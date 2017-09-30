package com.katryo;


/*

http://kpcbfellows.com/engineering/apply

deadline is 9/30

Answer the challenge question (optional)

Make your application stand out.

Problem
Using only primitive types, implement a fixed-size hash map that associates string keys with arbitrary data object references
 (you don't need to copy the object).
 Your data structure should be optimized for algorithmic runtime and memory usage.
 You should not import any external libraries, and may not use primitive hash map or dictionary types in languages like Python or Ruby.

The solution should be delivered in one class (or your language's equivalent) that provides the following functions:

constructor(size): return an instance of the class with pre-allocated space for the given number of objects.
boolean set(key, value): stores the given key/value pair in the hash map. Returns a boolean value indicating success / failure of the operation.
get(key): return the value associated with the given key, or null if no value is set.
delete(key): delete the value associated with the given key, returning the value on success or null if the key has no value.
float load(): return a float value representing the load factor (`(items in hash map)/(size of hash map)`) of the data structure.
Since the size of the dat structure is fixed, this should never be greater than 1.
If your language provides a built-in hashing function for strings (ex. `hashCode` in Java or `__hash__` in Python) you are welcome to use that.
If not, you are welcome to do something naive, or use something you find online with proper attribution.

Instructions
Please provide the source, tests, runnable command-line function and all the resources required to compile (if necessary) and run the following program.
You are free to use any coding language that compiles/runs on *nix operating systems and requires no licensed software.
 */

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DB {
    private int maxSize;
    private int occupiedKeyCount;
    private Node[] entryChains;

    /*
      constructor(size): return an instance of the class with pre-allocated space for the given number of objects.
     */
    public DB(int size) {
        maxSize = size;
        entryChains = new Node[size];
        occupiedKeyCount = 0;
    }

    /*
      boolean set(key, value): stores the given key/value pair in the hash map.
      Returns a boolean value indicating success / failure of the operation.
     */
    public boolean set(String key, Object value) {
        if (occupiedKeyCount == maxSize) return false;
        int ind = keyToIndex(key);
        try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
            try (ObjectOutputStream o = new ObjectOutputStream(b)) {
                o.writeObject(value);
                o.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            byte[] data = b.toByteArray();

            if (entryChains[ind] == null) {
                entryChains[ind] = new Node(key, data);
                occupiedKeyCount++;
            } else {
                if (entryChains[ind].get(key) == null) {
                    occupiedKeyCount++;
                };
                entryChains[ind].addNext(new Node(key, data));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
      get(key): return the value associated with the given key, or null if no value is set.
     */
    public Object get(String key) {
        int ind = keyToIndex(key);
        byte[] data = entryChains[ind].get(key);
        if (data == null) return null;
        try {
            return deserialize(data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
      delete(key): delete the value associated with the given key, returning the value on success or null if the key has no value.
     */
    public Object delete(String key) {
        int ind = keyToIndex(key);
        byte[] data = entryChains[ind].get(key);
        if (data == null) return null;
        try {
            Object target = deserialize(data);
            entryChains[ind].delete(key);
            occupiedKeyCount--;
            return target;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
      float load(): return a float value representing the load factor (`(items in hash map)/(size of hash map)`) of the data structure.
      Since the size of the dat structure is fixed, this should never be greater than 1.
     */
    public double load() {
        return (double) occupiedKeyCount / (double) maxSize;
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return ois.readObject();
            }
        }
    }

    /*
    Index might conflict.
     */
    private int keyToIndex(String key) {
        int hashedKey = key.hashCode();
        return Math.abs(hashedKey % maxSize);
    }

    private byte[] getBytes(String key) {
        return new byte[1];
    }

    public static void main(String[] args) {

        DB db = new DB(2);
        db.set("a", "abcd");
        db.set("bibibi", 128);

        System.out.println(db.get("a"));
    }

    private class Node {
        private Node next;
        private Node previous;
        private byte[] value;
        private String key;
        private Node(String key, byte[] data) {
            this.key = key;
            value = data;
            next = null;
            previous = null;
        }

        private void addNext(Node next) {
            this.next = next;
            next.previous = this;
        }

        private byte[] get(String key) {
            if (this.key.equals(key)) return value;
            if (next != null) {
                return next.get(key);
            }
            return null; // If there is no node that has the key with the same given key
        }

        private void delete(String key) {
            if (this.key.equals(key)) {
                if (previous != null) {
                    previous.next = next;
                }
                if (next != null) {
                    next.previous = previous;
                }
                value = null;
                return;
            }
            if (next != null) {
                next.delete(key);
                return;
            }
            throw new RuntimeException("There is no such key");
        }
    }
}