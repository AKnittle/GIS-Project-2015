import java.util.ArrayList;

// -------------------------------------------------------------------------
/**
 * Represents a Point on a 2D grid
 *
 * @author AndrewK
 * @version Mar 3, 2015
 */
public class Point
    implements Compare2D<Point>
{

    private long xcoord;
    private long ycoord;
    /**
     * Container for offsets
     */
    ArrayList<Long> container;


    // ----------------------------------------------------------
    /**
     * Create a new Point object.
     */
    public Point()
    {
        container = new ArrayList<Long>();
        xcoord = 0;
        ycoord = 0;
    }


    // ----------------------------------------------------------
    /**
     * Create a new Point object. defined coordinates
     *
     * @param x
     * @param y
     * @param e
     */
    public Point(long x, long y, long e)
    {
        container = new ArrayList<Long>();
        xcoord = x;
        ycoord = y;
        container.add(e);
    }


    // ----------------------------------------------------------
    /**
     * Get the container for the point
     *
     * @return container
     */
    public ArrayList<Long> getContainer()
    {
        return container;
    }


    /**
     * Returns the x-coordinate field of the user data object. get x coordinate
     */
    public long getX()
    {
        return xcoord;
    }


    /**
     * Returns the y-coordinate field of the user data object. get y coordinate
     */
    public long getY()
    {
        return ycoord;
    }


    // ----------------------------------------------------------
    /**
     * Inserts an element into the container.
     *
     * @param e
     */
    public void insert(long e)
    {
        container.add(e);
    }


    // ----------------------------------------------------------
    /**
     * Grabs the element directly from container based on the given index.
     * Mainly used for testing and for easier to read code.
     *
     * @param index
     * @return T
     */
    public long grab(int index)
    {
        return container.get(index);
    }


    /**
     * Returns indicator of the direction to the user data object from the
     * location (X, Y) specified by the parameters. The indicators are defined
     * in the enumeration Direction, and are used as follows: NE: vector from
     * (X, Y) to user data object has a direction in the range [0, 90) degrees
     * (relative to the positive horizontal axis NW: same as above, but
     * direction is in the range [90, 180) SW: same as above, but direction is
     * in the range [180, 270) SE: same as above, but direction is in the range
     * [270, 360)
     */
    public Direction directionFrom(long X, long Y)
    {

        return Direction.NOQUADRANT;
    }


    /**
     * Returns indicator of which quadrant of the rectangle specified by the
     * parameters that user data object lies in. The indicators are defined in
     * the enumeration Direction, and are used as follows, relative to the
     * center of the rectangle: NE: user data object lies in NE quadrant,
     * including non-negative x-axis, but not the positive y-axis NW: user data
     * object lies in the NW quadrant, including the positive y-axis, but not
     * the negative x-axis SW: user data object lies in the SW quadrant,
     * including the negative x-axis, but not the negative y-axis SE: user data
     * object lies in the SE quadrant, including the determines where a new node
     * will go in an internal node. negative y-axis, but not the positive x-axis
     * NOQUADRANT: user data object lies outside the specified rectangle
     */
    public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi)
    {

        double centerX = (xHi + xLo) / 2;
        double centerY = (yHi + yLo) / 2;
        // checks to see if the point is in the center.
        if (xcoord == centerX && ycoord == centerY)
        {
            // goes to NE
            return Direction.NE;
        }
        // x is right of center, y is at the center or higher. NE
        else if (xcoord > centerX && ycoord >= centerY)
        {
            return Direction.NE;
        }
        // x is at or left of center, y is higher. NW
        else if (xcoord <= centerX && ycoord > centerY)
        {
            return Direction.NW;
        }
        // x is left of center, y is at the center or lower. SW
        else if (xcoord < centerX && ycoord <= centerY)
        {
            return Direction.SW;
        }
        // x is at or right of center, y is lower. SE
        else if (xcoord >= centerX && ycoord < centerY)
        {
            return Direction.SE;
        }
        return Direction.NOQUADRANT;
    }


    /**
     * Returns true iff the user data object lies within or on the boundaries of
     * the rectangle specified by the parameters.
     */
    public boolean inBox(double xLo, double xHi, double yLo, double yHi)
    {
        // check to see if it lies in the bounds
        return (xcoord >= xLo && xcoord <= xHi && ycoord >= yLo && ycoord <= yHi);
    }


    /**
     *
     */
    public String toString()
    {
        String something = "X:" + xcoord + "Y:" + ycoord;
        return something;
    }


    /**
     * Overrides the user data object's inherited equals() method with an
     * appropriate definition; it is necessary to place this in the interface
     * that is used as a bound on the type parameter for the generic spatial
     * structure, otherwise the compiler will bind to Object.equals(), which
     * will almost certainly be inappropriate.
     */
    public boolean equals(Object o)
    {
        Point check = (Point)o;
        return (check.xcoord == getX() && check.ycoord == getY());
    }
}
