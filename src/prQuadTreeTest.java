import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

// -------------------------------------------------------------------------
/**
 * Write a one-sentence summary of your class here. Follow it with additional
 * details about its purpose, what abstraction it represents, and how to use it.
 *
 * @author AndrewK
 * @version Apr 9, 2015
 */

public class prQuadTreeTest
{

    /**
     * Testing the tree
     */
    prQuadTree<Point> treeTest;
    /**
     * first point to test
     */
    Point             pin1;


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
        pin1 = new Point(10, 10, 255);
        treeTest = new prQuadTree<Point>(-50, 50, -50, 50, 4);
    }


    // ----------------------------------------------------------
    /**
     * Place a description of your method here.
     */
    @Test
    public final void testPoint()
    {
        assertTrue(pin1.container.get(0) == 255);
        pin1.insert(123);
        assertTrue(pin1.grab(1) == 123);
        pin1.insert(2);
        assertTrue(pin1.grab(2) == 2);
        assertTrue(pin1.getContainer().get(0) == 255);
    }


    /**
     * Test method for {@link prQuadTree#insert(Point)}.
     */
    @Test
    public final void testInsert()
    {
        pin1.insert(123);
        Point pin2 = new Point(10, 10, 333);
        Point pin3 = new Point(10, 10, 444);
        Point pinA = new Point(11, 10, 999);
        Point pinB = new Point(-10, 10, 1010);
        Point pinC = new Point(10, -10, 13);
        Point pinD = new Point(-1, -1, 23);
        treeTest.insert(pin1);
        prQuadTree.prQuadLeaf leafRoot = (prQuadTree.prQuadLeaf)treeTest.root;
        assertTrue(leafRoot.getIndex(0).grab(0) == 255);
        assertTrue(leafRoot.getIndex(0).grab(1) == 123);
        assertTrue(treeTest.insert(pin2));
        assertTrue(leafRoot.getIndex(0).grab(0) == 255);
        assertTrue(leafRoot.getIndex(0).grab(1) == 123);
        assertTrue(leafRoot.getIndex(0).grab(2) == 333);
        assertTrue(treeTest.insert(pin3));
        assertTrue(leafRoot.getIndex(0).grab(3) == 444);

        assertTrue(treeTest.insert(pinA));
        assertTrue(treeTest.insert(pinB));
        assertTrue(treeTest.insert(pinC));
        assertTrue(leafRoot.getIndex(1).grab(0) == 999);
        assertTrue(leafRoot.getIndex(2).grab(0) == 1010);
        assertTrue(leafRoot.getIndex(3).grab(0) == 13);

        assertTrue(treeTest.insert(pinD));
        assertTrue(treeTest.root.getClass().equals(
            prQuadTree.prQuadInternal.class));
        prQuadTree.prQuadInternal root =
            (prQuadTree.prQuadInternal)treeTest.root;

        prQuadTree.prQuadLeaf NE = (prQuadTree.prQuadLeaf)root.NE;
        prQuadTree.prQuadLeaf NW = (prQuadTree.prQuadLeaf)root.NW;
        assertTrue(NE.getIndex(0).grab(0) == 255);
        assertTrue(NW.getIndex(0).grab(0) == 1010);

    }


    /**
     * Test method for {@link prQuadTree#find(Point)}.
     */
    @Test
    public final void testFindT()
    {
        Point pin2 = new Point(10, 10, 333);
        Point pin3 = new Point(10, 10, 444);
        Point pinA = new Point(11, 10, 999);
        Point pinB = new Point(-10, 10, 1010);
        Point pinC = new Point(10, -10, 13);
        Point pinD = new Point(-1, -1, 23);
        treeTest.insert(pin1);
        Point result = treeTest.find(pin1);
        assertTrue(result.grab(0) == 255);
        assertTrue(treeTest.insert(pin2));
        result = treeTest.find(pin2);
        assertTrue(result.grab(1) == 333);
        assertTrue(treeTest.insert(pin3));
        result = treeTest.find(pin3);
        assertTrue(result.grab(2) == 444);

        assertTrue(treeTest.insert(pinA));
        result = treeTest.find(pinA);
        assertTrue(result.grab(0) == 999);
        assertTrue(treeTest.insert(pinB));
        result = treeTest.find(pinB);
        assertTrue(result.grab(0) == 1010);
        assertTrue(treeTest.insert(pinC));
        result = treeTest.find(pinC);
        assertTrue(result.grab(0) == 13);
        assertTrue(treeTest.insert(pinD));
        result = treeTest.find(pinD);
        assertTrue(result.grab(0) == 23);
    }


    /**
     * Test method for {@link prQuadTree#find(long, long, long, long)}.
     */
    @Test
    public final void testFindVector()
    {
        fail("Not yet implemented"); // TODO
    }

}
