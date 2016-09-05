import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author AndrewK
 * @version Apr 12, 2015
 */

public class Test_Project4
{

    private static final String[][] String = null;


    /**
     * Test method for {@link GIS#main(java.lang.String[])}.
     */
    @Test
    public void testMain()
    {
        String[] args1 =
            { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data1.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script01.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result.txt" };

        String[] args2 =
            { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data2.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script02.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result2.txt" };

        String[] args3 =
            { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data3.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script03.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result3.txt" };

        String[] args4 =
            { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data4.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script04.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result4.txt" };

        String[] args9 =
            { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data9.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script09.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result9.txt" };

        String[] args6 =
            { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data6.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script06.txt",
                "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result6.txt" };
        String[] args7 =
        { "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Data7.txt",
            "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Script07.txt",
            "C:\\Users\\AndrewK\\workspace\\Project4\\src\\Result7.txt" };
        try
        {
            GIS.main(args1);
            GIS.main(args2);
            GIS.main(args3);
            GIS.main(args4);
            GIS.main(args6);
            GIS.main(args7);
            GIS.main(args9);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        assertTrue(GIS.tree.root != null);
        assertTrue(GIS.tree.bucketSize == 4);
        // East
        assertTrue(GIS.tree.xMax == GIS.eastL);
        // West
        assertTrue(GIS.tree.xMin == GIS.westL);
        // North
        assertTrue(GIS.tree.yMax == GIS.northL);
        // South
        assertTrue(GIS.tree.yMin == GIS.southL);
    }

}
