import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;

/**
 * This is the Hash Table that will hold records from the GIS.
 *
 * @author Andrew Knittle andrk11
 * @param <T>
 * @param <S>
 */
public class HashTable<T, S>
{

    // -------------------------------------------------------------------------
    /**
     * Inner class that represents a "slot" within the table
     *
     * @param <T>
     * @author AndrewK
     * @version Apr 7, 2015
     * @param <S>
     */
    @SuppressWarnings("hiding")
    public class HashSlot<T, S>
    {
        /**
         * This is name-state abbreviation used to determine where things go in
         * the hash table.
         */
        S            key;

        /**
         * The ArrayList that contains all the offsets, but is generic.
         */
        ArrayList<T> contained;


        // We store duplicates in a list
        // ----------------------------------------------------------
        /**
         * Create a new HashSlot object. The key starts out null which makes
         * finding empty slots easier.
         */
        public HashSlot()
        {
            key = null;
            contained = new ArrayList<T>();
        }


        // ----------------------------------------------------------
        /**
         * Create a new HashSlot object.
         *
         * @param name
         * @param record
         */
        public HashSlot(S name, T record)
        {
            key = name;
            contained = new ArrayList<T>();
            contained.add(record);
        }


        // ----------------------------------------------------------
        /**
         * Inserts an element (offset) into the contained array list
         *
         * @param e
         */
        public void add(T e)
        {
            contained.add(e);
        }
    }

    /**
     * max size of the current table.
     */
    long             capacity;

    /**
     * number of slots taken
     */
    long             occupied;

    /**
     * Represents the number of unique values in the HashTable
     */
    long             uniqueVals;

    /**
     * The table that holds HashSlots. Each slot holds an ArrayList of offsets
     * and a key (name-state abbreviation)
     */
    HashSlot<T, S>[] table;


    /**
     * Initializes the Hash Table with a size and first level of capacity
     */
    @SuppressWarnings("unchecked")
    public HashTable()
    {
        capacity = 1019;
        occupied = 0;
        uniqueVals = 0;
        // All slots will be null
        table = (HashSlot<T, S>[])Array.newInstance(HashSlot.class, (int)capacity);
    }


    // ----------------------------------------------------------
    /**
     * Finds an array of offsets based on the "name-state", or key, given.
     *
     * @param name
     * @return T
     * @throws NoSuchElementException
     */
    public ArrayList<T> search(S name)
                                      throws NoSuchElementException
    {
        // Get the home slot of the record
        long home = 0;
        try
        {
            home = elfHash((String)name);
        }
        catch (ClassCastException e)
        {
            // just in case something goes wrong.
            home = elfHash(String.valueOf(name));

        }
        home = home % capacity;

        if (table[(int)home] != null)
        {

            int counter = 0;
            int slot = 0;
            // Found a record, but not the one we wanted so start probing.
            while (counter != capacity)
            {
                slot = (int)(home + (((counter * counter) + counter) / 2));
                // Check if wrapping around
                slot = (int)(slot % capacity);

                // Check if the slot is null
                if (table[slot] == null)
                {
                    // Record is not in here
                    throw new NoSuchElementException();
                }
                else
                {
                    if (table[slot].key.equals(name))
                    {
                        // We found the record we wanted
                        return table[slot].contained;
                    }
                    // Update counter
                    counter++;
                }
            }
        }
        // Record is not in here
        throw new NoSuchElementException();
    }


    /**
     * Inserts a new element into the Hash Table. 1: Check to make sure if a
     * spot is currently taken. 1.a: If the is spot taken look for another with
     * quadratic probing. Returns the number of probes taken to insert.
     *
     * @param record
     * @param key
     * @return long
     */
    public long insert(T record, S key)
    {
        // The amount of probes done.
        long highScore = 0;
        // Get the name of the record.
        S name = key;
        long location = 0;
        // The name is now used to find a valid location in the HashTable
        try
        {
            // Hopefully "name" is actually a String. If not we rely on the
            // "try-catch" to convert it to a string properly. This process is
            // done in several places throughout this class
            location = elfHash((String)name);
        }
        catch (ClassCastException e)
        {
            // just in case something goes wrong.
            // Since this is generic "name" must be an object, and every object
            // must be able to be represented as a string. I could either use
            // String's built in "valueof" method which takes an object or
            // Object's built in toString method which returns a string. Either
            // does the same job, while still keeping things generic
            location = elfHash(String.valueOf(name));
        }
        // MOD BY THE SIZE
        location = location % capacity;
        if (table[(int)location] != null)
        {
            int counter = 0;
            int slot = 0;
            boolean found = false;
            // Try to find a vacant slot to store the record. Stop the loop if a
            // slot has been found or if counter is at the same size as
            // capacity. It should never increment capacity number of times, but
            // it's a safety against an unending loop
            while (found == false && counter != capacity)
            {
                slot = (int)(location + (((counter * counter) + counter) / 2));
                // Check if wrapping around
                slot = (int)(slot % capacity);
                // -----------------------------------------------------
                if (table[slot] == null)
                {
                    // Empty slot found. we can insert here
                    table[slot] = new HashSlot<T, S>(key, record);
                    occupied++;
                    uniqueVals++;
                    found = true;
                    highScore = counter;
                }
                // check if the records are the same.
                else if (table[slot].key.equals(name))
                {
                    table[slot].add(record);
                    uniqueVals++;
                    found = true;
                    highScore = counter;
                }
                else
                {
                    // Update counter
                    counter++;
                }
            }
        }
        else
        {
            // This slot in the table is empty so we can insert the record
            // directly.
            table[(int)location] = new HashSlot<T, S>(key, record);
            occupied++;
            uniqueVals++;
        }
        // if the number of records is high enough rehashing will occur.
        reSize();
        return highScore;
    }


    /**
     * Finds the placement of the Record, given the Record's name, in the Hash
     * Table.
     *
     * @param toHash
     * @return long
     */
    public static long elfHash(String toHash)
    {
        long hashValue = 0;
        for (int Pos = 0; Pos < toHash.length(); Pos++)
        { // use all elements
            hashValue = (hashValue << 4) + toHash.charAt(Pos); // shift/mix
            long hiBits = hashValue & 0xF000000000000000L; // get high nybble
            if (hiBits != 0)
                hashValue ^= hiBits >> 56; // xor high nybble with second nybble
            hashValue &= ~hiBits; // clear high nybble
        }
        return hashValue;
    }


    /**
     * Method that actually hashes records into the new sized table.
     *
     * @param tempTable
     * @param newSize
     */
    public void reHash(HashSlot<T, S>[] tempTable, long newSize)
    {
        int index = 0;
        while (index != capacity)
        {
            if (table[index] != null)
            {
                long newHome = 0;
                try
                {
                    newHome = elfHash((String)table[index].key);
                }
                catch (ClassCastException e)
                {
                    // just in case something goes wrong.
                    newHome = elfHash(String.valueOf(table[index].key));
                }

                // get the key for the slot
                HashSlot<T, S> oldSlot = table[index];
                // Moving the oldSlot to its new slot.
                reHashInsert(tempTable, oldSlot, newHome, newSize);
            }
            index++;
        }
    }


    /**
     * Insert specifically built for rehashing. You pass in the new table, the
     * record going into the new table, the home-slot of the record being passed
     * in, and the size of the new table.
     *
     * @param tempTable
     * @param oldRecord
     * @param newSlot
     * @param newSize
     */
    public void reHashInsert(HashSlot<T, S>[] tempTable, HashSlot<T, S> oldRecord, long newSlot,
                             long newSize)
    {
        // location is the start; home slot.
        long location = newSlot;
        // Mod by the size of the table
        location = location % newSize;
        if (tempTable[(int)location] != null)
        {
            long counter = 0;
            long slot = 0;
            boolean found = false;
            // Try to find a vacant slot to store the record. Stop the loop if a
            // slot has been found or if counter is at the same size as
            // newSize. It should never increment capacity number of times, but
            // it's a safety against an unending loop
            while (found == false || counter != newSize)
            {
                // Probing
                slot = (location + (((counter * counter) + counter) / 2));
                // Adjust the slot number based on the newSize of the new hash
                // table
                slot = (slot % newSize);
                if (tempTable[(int)slot] == null)
                {
                    // Empty slot found in the new Hash table
                    // Can put the oldRecord, arraylist and key, back into the
                    // new slot
                    tempTable[(int)location] = oldRecord;
                    found = true;
                }
                counter++;
            }
        }
        else
        {
            // This slot in the table is empty so we can insert the record
            // directly.
            tempTable[(int)location] = oldRecord;
        }
    }


    /**
     * Updates the size of the hash table, while also preserving all elements
     * within the table.
     */
    @SuppressWarnings("unchecked")
    public void reSize()
    {
        // If the number of occupied slots is %70 of capacity you need make the
        // hash table.
        if (occupied >= (capacity * 0.7))
        {
            // Size will be increased
            if (capacity == 16646323)
            {
                // Unless at max size
                return;
            }
            if (capacity <= 1019)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 2027);
                reHash(newTable, 2027);
                table = newTable;
                // Change capacity last, since the old capacity is required for
                // rehashing.
                capacity = 2027;
            }
            else if (capacity <= 2027)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 4079);
                reHash(newTable, 4079);
                table = newTable;
                capacity = 4079;
            }
            else if (capacity <= 4079)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 8123);
                reHash(newTable, 8123);
                table = newTable;
                capacity = 8123;
            }
            else if (capacity <= 8123)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 16267);
                reHash(newTable, 16267);
                table = newTable;
                capacity = 16267;
            }
            else if (capacity <= 16267)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 32503);
                reHash(newTable, 32503);
                table = newTable;
                capacity = 32503;
            }
            else if (capacity <= 32503)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 65011);
                reHash(newTable, 65011);
                table = newTable;
                capacity = 65011;
            }
            else if (capacity <= 65011)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 130027);
                reHash(newTable, 30027);
                table = newTable;
                capacity = 130027;
            }
            else if (capacity <= 130027)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 260111);
                reHash(newTable, 260111);
                table = newTable;
                capacity = 260111;
            }
            else if (capacity <= 260111)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 520279);
                reHash(newTable, 520279);
                table = newTable;
                capacity = 520279;
            }
            else if (capacity <= 520279)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 1040387);
                reHash(newTable, 1040387);
                table = newTable;
                capacity = 1040387;
            }
            else if (capacity <= 1040387)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 2080763);
                reHash(newTable, 2080763);
                table = newTable;
                capacity = 2080763;
            }
            else if (capacity <= 2080763)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 4161539);
                reHash(newTable, 4161539);
                table = newTable;
                capacity = 4161539;
            }
            else if (capacity <= 4161539)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 8323151);
                reHash(newTable, 8323151);
                table = newTable;
                capacity = 8323151;
            }
            else if (capacity <= 8323151)
            {
                // Make new table to rehash records into.
                HashSlot<T, S>[] newTable =
                    (HashSlot<T, S>[])Array.newInstance(HashSlot.class, 16646323);
                reHash(newTable, 16646323);
                table = newTable;
                capacity = 16646323;
            }
            return;
        }
        // No rehashing needed
        return;
    }
}
// On my honor:
//
// - I have not discussed the Java language code in my program with
// anyone other than my instructor or the teaching assistants
// assigned to this course.
//
// - I have not used Java language code obtained from another student,
// or any other unauthorized source, either modified or unmodified.
//
// - If any Java language code or documentation used in my program
// was obtained from another source, such as a text book or course
// notes, that has been clearly noted with a proper citation in
// the comments of my program.
//
// - I have not designed this program in such a way as to defeat or
// interfere with the normal operation of the Curator System.
//
// Andrew Knittle
