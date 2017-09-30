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
    private static final int BYTE_ARRAY_SIZE = 4096;
    private byte[][] dataArray;
    private int[] isOccupiedArray; // I can use primitive data type only
    private int dataLength;
    private int occupiedKeyCount;

    /*
      constructor(size): return an instance of the class with pre-allocated space for the given number of objects.
     */
    public DB(int size) {
        dataLength = size;
        dataArray = new byte[size][BYTE_ARRAY_SIZE];
        isOccupiedArray = new int[size];
        occupiedKeyCount = 0;
    }

    /*
      boolean set(key, value): stores the given key/value pair in the hash map.
      Returns a boolean value indicating success / failure of the operation.
     */
    public boolean set(String key, Object value) {
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

            if (isOccupiedArray[ind] == 0) {
                isOccupiedArray[ind] = 1;
                occupiedKeyCount++;
            }

            dataArray[ind] = data;
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
        if (isOccupiedArray[ind] == 0) return null;
        byte[] data = dataArray[ind];
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
        if (isOccupiedArray[ind] == 0) return null;
        byte[] data = dataArray[ind];
        try {
            Object target = deserialize(data);
            isOccupiedArray[ind] = 0;
            dataArray[ind] = new byte[BYTE_ARRAY_SIZE];
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
        return (double) occupiedKeyCount / (double) dataLength;
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                return ois.readObject();
            }
        }
    }

    private int keyToIndex(String key) {
        int hashedKey = key.hashCode();
        int ind = Math.abs(hashedKey % dataLength);

        //TODO: Implement separate chaining
        // If you can't go out of while, there are full items.
        assert dataLength != occupiedKeyCount;
        while (isOccupiedArray[ind] == 1) {
            ind++;
            if (ind == dataLength) {
                ind = 0;
            }
        }
        return ind;
    }

    private byte[] getBytes(String key) {
        return new byte[1];
    }

    public static void main(String[] args) {

        DB db = new DB(10);
        db.set("a", "abcd");
        db.set("bibibi", 128);
        db.set("citadel", 'D');
        System.out.println(db.get("bibibi"));
        System.out.println(db.load());

        db.delete("bibibi");
        System.out.println(db.get("bibibi"));
        System.out.println(db.load());
    }
}