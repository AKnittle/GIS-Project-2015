import java.util.ArrayList;

//
//
// -------------------------------------------------------------------------
/**
 * The interface Compare2D is intended to supply facilities that are useful in
 * supporting the the use of a generic spatial structure with a user-defined
 * data type.
 *
 * @param <T>
 * @author AndrewK
 * @version Feb 23, 2015
 */
public interface Compare2D<T>
{

    //
    // ----------------------------------------------------------
    /**
     * Returns the x-coordinate field of the user data object.
     *
     * @return long
     */
    public long getX();


    //
    // ----------------------------------------------------------
    /**
     * Returns the y-coordinate field of the user data object.
     *
     * @return long
     */
    public long getY();


    // ----------------------------------------------------------
    /**
     * Get the container for the point
     *
     * @return ArrayList
     */
    public ArrayList<Long> getContainer();


    // ----------------------------------------------------------
    /**
     * Grabs the element directly from container based on the given index.
     * Mainly used for testing and for easier to read code.
     *
     * @param index
     * @return long
     */
    public long grab(int index);


    // ----------------------------------------------------------
    /**
     * Inserts an element into the container.
     *
     * @param e
     */
    public void insert(long e);


    // ----------------------------------------------------------
    /**
     * Returns indicator of the direction to the user data object from the
     * location (X, Y) specified by the parameters. The indicators are defined
     * in the enumeration Direction, and are used as follows:
     *
     * @param X
     * @param Y
     * @return Direction
     */
    //
    // NE: vector from (X, Y) to user data object has a direction in the
    // range [0, 90) degrees (relative to the positive horizontal axis
    // NW: same as above, but direction is in the range [90, 180)
    // SW: same as above, but direction is in the range [180, 270)
    // SE: same as above, but direction is in the range [270, 360)
    // NOQUADRANT: location of user object is equal to (X, Y)
    //
    public Direction directionFrom(long X, long Y);


    // Returns indicator of which quadrant of the rectangle specified by the
    // parameters that user data object lies in.
    // The indicators are defined in the enumeration Direction, and are used
    // as follows, relative to the center of the rectangle:
    //
    // NE: user data object lies in NE quadrant, including non-negative
    // x-axis, but not the positive y-axis
    // NW: user data object lies in the NW quadrant, including the positive
    // y-axis, but not the negative x-axis
    // SW: user data object lies in the SW quadrant, including the negative
    // x-axis, but not the negative y-axis
    // SE: user data object lies in the SE quadrant, including the negative
    // y-axis, but not the positive x-axis
    // NOQUADRANT: user data object lies outside the specified rectangle
    //
    // ----------------------------------------------------------
    /**
     * Returns indicator of which quadrant of the rectangle specified by the
     * parameters that user data object lies in.
     *
     * @param xLo
     * @param xHi
     * @param yLo
     * @param yHi
     * @return Direction
     */
    public Direction inQuadrant(double xLo, double xHi, double yLo, double yHi);


    // Returns true iff the user data object lies within or on the boundaries
    // of the rectangle specified by the parameters.
    // ----------------------------------------------------------
    /**
     * Returns true iff the user data object lies within or on the boundaries of
     * the rectangle specified by the parameters.
     *
     * @param xLo
     * @param xHi
     * @param yLo
     * @param yHi
     * @return boolean
     */
    public boolean inBox(double xLo, double xHi, double yLo, double yHi);


    // Overrides the user data object's inherited equals() method with an
    // appropriate definition; it is necessary to place this in the interface
    // that is used as a bound on the type parameter for the generic spatial
    // structure, otherwise the compiler will bind to Object.equals(), which
    // will almost certainly be inappropriate.
    public boolean equals(Object o);
}
