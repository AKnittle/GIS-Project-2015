import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 */

/**
 * @author Andrew
 */
public class Test_HashTable
{

    /**
	 *
	 */
    RecordGIS               test1;
    /**
	 *
	 */
    RecordGIS               test2;
    /**
	 *
	 */
    RecordGIS               test3;
    /**
	 *
	 */
    RecordGIS               test4;
    /**
	 *
	 */
    RecordGIS               test5;
    RecordGIS               test6;
    /**
	 *
	 */
    HashTable<Long, String> table1;
    HashTable<Long, String> table2;
    HashTable<Long, Long>   table3;


    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
                       throws Exception
    {
        test1 = new RecordGIS("START", "STOP", 123);
        test2 = new RecordGIS("START", "STOP", 321);
        test3 = new RecordGIS("A", "B", 1);
        test4 = new RecordGIS("B", "A", 2);
        test5 = new RecordGIS("A", "B", 2);
        test6 = new RecordGIS("Solid", "Snake", 5555);
        table1 = new HashTable<Long, String>();
        table1.capacity = 5;
        // --------------------------------------------
        table2 = new HashTable<Long, String>();
        table2.capacity = 20;
        table3 = new HashTable<Long, Long>();
        table3.insert((long)2, (long)2);
    }


    /**
     * Test method for {@link HashTable#search(java.lang.String)}.
     */
    @Test
    public final void testSearch()
    {
        boolean thrown = false;
        table1.insert(test3.offset, test3.fullname);
        table1.insert(test4.offset, test4.fullname);
        table1.insert(test5.offset, test5.fullname);
        assertTrue(table1.search(test3.fullname).get(0) == 1);
        assertTrue(table1.search(test5.fullname).get(1) == 2);
        try
        {
            table1.search("derp"); // Should throw exception
        }
        catch (Exception e)
        {
            thrown = true;
        }
        assertTrue(thrown);
        table1.insert(test1.offset, test1.fullname);
        table1.insert(test2.offset, test2.fullname);
        table1.insert(test6.offset, test6.fullname);
        assertTrue(table1.search(test3.fullname).get(0) == 1);
        assertTrue(table1.search(test5.fullname).get(1) == 2);
        assertEquals(table1.occupied, 4);
        assertEquals(table1.capacity, 2027);

    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public final void testCrash()
    {
        for (int i = 0; i < 12; i++)
        {
            int j = i * i;
            RecordGIS derp = new RecordGIS(Integer.toString(j), Integer.toString(i), j + i);
            table2.insert(derp.offset, derp.fullname);
        }
        boolean thrown = false;
        table2.insert(test3.offset, test3.fullname);
        table2.insert(test4.offset, test4.fullname);
        table2.insert(test5.offset, test5.fullname);
        assertTrue(table2.search(test3.fullname).get(0) == 1);
        assertTrue(table2.search(test5.fullname).get(1) == 2);
        try
        {
            table2.search("derp"); // Should throw exception
        }
        catch (Exception e)
        {
            thrown = true;
        }
        assertTrue(thrown);
        table2.insert(test1.offset, test1.fullname);
        table2.insert(test2.offset, test2.fullname);
        table2.insert(test6.offset, test6.fullname);
        assertTrue(table2.search(test3.fullname).get(0) == 1);
        assertTrue(table2.search(test5.fullname).get(1) == 2);
        assertTrue(table2.search(test2.fullname).get(1) == 321);
        assertEquals(table2.occupied, 16);
        assertEquals(table2.capacity, 2027);
    }
}
