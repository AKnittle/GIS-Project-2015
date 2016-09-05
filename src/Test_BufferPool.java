import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author AndrewK
 * @version Mar 28, 2015
 */

public class Test_BufferPool
{

    BufferPool<String> addWater;
    BufferPool<String> kiddyPool;
    BufferPool<String> adultSwim;
    BufferPool<String> deepEnd;


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     *
     * @throws java.lang.Exception
     */
    @Before
    public void setUp()
        throws Exception
    {
        addWater = new BufferPool<String>();

        kiddyPool = new BufferPool<String>();
        kiddyPool.update("1");

        adultSwim = new BufferPool<String>();
        adultSwim.update("1");
        adultSwim.update("2");
        adultSwim.update("3");
        adultSwim.update("4");
        adultSwim.update("5");

        deepEnd = new BufferPool<String>();
        for (int i = 0; i < 10; i++)
        {
            String derp = Integer.toString(i);
            deepEnd.update(derp);
        }
    }


    /**
     * Test method for {@link BufferPool#update(java.lang.Object)}.
     */
    @Test
    public void testUpdate()
    {
        // Empty
        assertFalse(addWater.find("NO"));
        // 1 node
        assertTrue(kiddyPool.find("1"));
        // Half full
        assertTrue(adultSwim.find("1"));
        assertTrue(adultSwim.find("2"));
        assertTrue(adultSwim.find("3"));
        assertTrue(adultSwim.find("4"));
        assertTrue(adultSwim.find("5"));
        // Full
        for (int i = 0; i < 10; i++)
        {
            String derp = Integer.toString(i);
            assertTrue(deepEnd.find(derp));
        }
        assertFalse(deepEnd.find("Walrus"));
        deepEnd.update("Don't forget to bring a towel!");
        assertTrue(deepEnd.find("Don't forget to bring a towel!"));
        assertFalse(deepEnd.find("0"));

    }


    /**
     * Test method for
     * {@link BufferPool#getElement(java.lang.Object, BufferPool.BufferNode)}.
     */
    @Test
    public void testGetElement()
    {
        deepEnd.update("Don't forget to bring a towel!");
        assertTrue(deepEnd.get("Don't forget to bring a towel!").equals(
            "Don't forget to bring a towel!"));
        assertNull(deepEnd.get("0"));
        assertNull(deepEnd.get("Walrus"));
        assertTrue(deepEnd.get("1").equals("1"));
        assertEquals(adultSwim.get("5"), "5");
    }

}
