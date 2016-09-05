import java.util.Vector;

// -------------------------------------------------------------------------
/**
 * A prQuadTree is a kind of Quad-Tree. A Quad-Tree has prQuadNodes of different
 * types: internal (which define regions of space it contains) and leaf (which
 * contain elements). The Root Node is the base of the tree, which defines max
 * size of the tree in spatial coordinates; from there smaller quadrants are
 * divided from the max space for other internal nodes which contain other nodes
 * and this cycle repeats itself with more nodes. The Tree does not contain
 * duplicate values.
 *
 * @param <T>
 * @author AndrewK
 * @version Feb 23, 2015
 */
public class prQuadTree<T extends Compare2D<? super T>>
{

    // You must use a hierarchy of node types with an abstract base
    // class. You may use different names for the node types if
    // you like (change displayHelper() accordingly).
    // -------------------------------------------------------------------------
    /**
     * Defines the basic properties of a prQuadNode that will be extended to
     * leaf and internal nodes.
     *
     * @author AndrewK
     * @version Feb 23, 2015
     */
    abstract class prQuadNode
    {
        // Nothing right now...
    }


    // -------------------------------------------------------------------------
    /**
     * A prQuadNode that is a leaf node. The leaf node contains a Vector<T>
     * called Elements
     *
     * @author AndrewK
     * @version Feb 23, 2015
     */
    class prQuadLeaf extends prQuadNode
    {
        /**
         * Vector that holds the elements
         */
        Vector<T> Elements;


        // ----------------------------------------------------------
        /**
         * Initialize the leaf node
         */
        public prQuadLeaf()
        {
            Elements = new Vector<T>();
        }


        // ----------------------------------------------------------
        /**
         * initialize the leaf node
         *
         * @param loc
         */
        public prQuadLeaf(T loc)
        {
            Elements = new Vector<T>();
            Elements.add(loc);
        }


        // ----------------------------------------------------------
        /**
         * Easy getter method.
         *
         * @param x
         * @return T
         */
        public T getIndex(int x)
        {
            return Elements.get(x);
        }

    }


    // -------------------------------------------------------------------------
    /**
     * A prQuadNode that is an internal node. The internal node defines regions,
     * North West, North East, South West, and South East which smaller nodes
     * will occupy.
     *
     * @author AndrewK
     * @version Feb 23, 2015
     */
    class prQuadInternal extends prQuadNode
    {
        /**
         * all the 4 possible child children of an internal node
         */
        @SuppressWarnings("javadoc")
        prQuadNode NW, NE, SE, SW;


        // ----------------------------------------------------------
        /**
         * Create a new prQuadInternal object.
         */
        public prQuadInternal()
        {
            NW = null;
            NE = null;
            SE = null;
            SW = null;
        }
    }

    /**
     * Root Node of the prQuadTree
     */
    prQuadNode root;
    /**
     * xMin is the lower x bound, xMax is the upper x bound, yMin is the lower y
     * bound, yMax is the upper y bound
     */
    @SuppressWarnings("javadoc")
    long       xMin, xMax, yMin, yMax;

    /**
     * The bucket size for all the vectors
     */
    int        bucketSize;

    /**
     * number of individual elements in table
     */
    int        totalElems;


    // TODO: CHECK FOR SIZE ON THE VECTORS INSIDE THE LEAVES!!!

    // Initialize quadtree to empty state, representing the specified region.
    // ----------------------------------------------------------
    /**
     * Create a new prQuadTree object.
     *
     * @param xMin
     * @param xMax
     * @param yMin
     * @param yMax
     * @param bucket
     */
    public prQuadTree(long xMin, long xMax, long yMin, long yMax, int bucket)
    {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        totalElems = 0;
        bucketSize = bucket;
        root = null;
    }


    // ----------------------------------------------------------
    /**
     * Checks an internal node to determine if a "constriction", or "contract",
     * is necessary. It will return true if there's only one child or none.
     * HOWEVER, an internal with no nodes should not even occur, but since I'm
     * paranoid I have a check.
     *
     * @param parent
     * @return boolean
     */
    public boolean constrictCheck(prQuadInternal parent)
    {
        int childcount = 0;
        // check how many children are in the node that are NOT NULL

        // NE---------------------------
        if (parent.NE != null && parent.NE.getClass().equals(prQuadLeaf.class))
        {
            childcount++;
        }
        // If any internal is found the method must return false.
        else if (parent.NE != null && parent.NE.getClass().equals(prQuadInternal.class))
        {
            return false;
        }

        // NW--------------------------
        if (parent.NW != null && parent.NW.getClass().equals(prQuadLeaf.class))
        {
            childcount++;
        }
        else if (parent.NW != null && parent.NW.getClass().equals(prQuadInternal.class))
        {
            return false;
        }

        // SE------------------------
        if (parent.SE != null && parent.SE.getClass().equals(prQuadLeaf.class))
        {
            childcount++;
        }
        else if (parent.SE != null && parent.SE.getClass().equals(prQuadInternal.class))
        {
            return false;
        }

        // SW--------------------
        if (parent.SW != null && parent.SW.getClass().equals(prQuadLeaf.class))
        {
            childcount++;
        }
        else if (parent.SW != null && parent.SW.getClass().equals(prQuadInternal.class))
        {
            return false;
        }

        // check to see if there are enough children to keep the internal node
        if (childcount != 1)
        {
            // Nothing to do
            return false;
        }
        else
        {
            // time to remove the node
            return true;
        }

    }


    // ----------------------------------------------------------
    /**
     * This method gets the 1 remaining child node for constriction. Terminal is
     * the node that will be replaced by a child.
     *
     * @param terminal
     * @return prQuadNode
     */
    @SuppressWarnings("unchecked")
    public prQuadNode internalReplace(prQuadNode terminal)
    {

        prQuadInternal internal = (prQuadInternal)terminal;
        // find which child node will be the replacement. make sure the child is
        // also a leaf as well
        if (internal.NW != null)
        {
            return internal.NW;
        }
        else if (internal.NE != null)
        {
            return internal.NE;
        }
        else if (internal.SE != null)
        {
            return internal.SE;
        }
        else if (internal.SW != null)
        {
            return internal.SW;
        }
        return null;
    }


    // Pre: elem != null
    // Post: If elem lies within the tree's region, and elem is not already
    // present in the tree, elem has been inserted into the tree.
    // Return true iff elem is inserted into the tree.
    // ----------------------------------------------------------
    /**
     * Inserts element into the tree.
     *
     * @param elem
     * @return boolean
     */
    public boolean insert(T elem)
    {

        // check to see if null... just to be sure
        if (elem == null)
        {
            return false;
        }
        // obviously it needs to lie in the bounds
        else if (elem.inBox(xMin, xMax, yMin, yMax))
        {
            totalElems++;
            root = insertH(elem, root, xMax, xMin, yMax, yMin);
            return true;
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Goes through the tree to find a place to insert a node recursively. start
     * with original coordinates then work your way down as needed.
     *
     * @param elem
     * @param node
     * @param xHi
     * @param xLo
     * @param yHi
     * @param yLo
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public prQuadNode insertH(T elem, prQuadNode node, long xHi, long xLo, long yHi, long yLo)
    {
        // At the end of the tree, BUT with NO LEAF. There's room for insertion
        if (node == null)
        {
            // Insertion can be done here now!
            // make "node" a leaf node and give the vector a value. In this case
            // a Point
            prQuadLeaf leaf = (prQuadLeaf)node;
            leaf = new prQuadLeaf();
            leaf.Elements.add(elem);
            return leaf;

        }
        // Determine what KIND of node is being used
        else if (node.getClass().equals(prQuadInternal.class))
        {

            // Find the direction of which way to move within the internal node
            Direction tempDir = elem.inQuadrant(xLo, xHi, yLo, yHi);
            // Casting
            prQuadInternal internal = (prQuadInternal)node;
            if (tempDir == Direction.NE)
            {
                // adjusting coordinates based on quadrant
                xLo = (xHi + xLo) / 2;
                yLo = (yHi + yLo) / 2;
                internal.NE = insertH(elem, internal.NE, xHi, xLo, yHi, yLo);
            }
            else if (tempDir == Direction.NW)
            {
                xHi = (xHi + xLo) / 2;
                yLo = (yHi + yLo) / 2;
                internal.NW = insertH(elem, internal.NW, xHi, xLo, yHi, yLo);
            }
            else if (tempDir == Direction.SW)
            {
                xHi = (xHi + xLo) / 2;
                yHi = (yHi + yLo) / 2;
                internal.SW = insertH(elem, internal.SW, xHi, xLo, yHi, yLo);
            }
            else
            {
                xLo = (xHi + xLo) / 2;
                yHi = (yHi + yLo) / 2;
                internal.SE = insertH(elem, internal.SE, xHi, xLo, yHi, yLo);
            }
        }
        // Must be a leaf. If this was a BST we would stop here normally, but we
        // need to check if the node is a duplicate or not

        // GIS UPDATE BELOW:
        else
        {
            prQuadLeaf leaf = (prQuadLeaf)node;
            // check to see if the leaf contains the same value

            for (int x = 0; x < leaf.Elements.size(); x++)
            {
                // Checks if the coordinates are the same
                if (leaf.Elements.get(x).equals(elem))
                {
                    /**
                     * add into the original Point's bucket (container). The
                     * "point" that is being inserted is just its OFFSET which
                     * will be placed inside the Original Point's container.
                     * This will hold all offsets for the same location
                     */
                    leaf.getIndex(x).getContainer().add(elem.grab(0));
                    return node;
                }
            }

            // Capacity check for the bucket size.
            if (leaf.Elements.size() < bucketSize)
            {
                // There's enough room for Points to be added into the vector of
                // the current leaf.
                leaf.Elements.add(elem);
                return node;
            }
            else
            {
                /**
                 * Both leafs occupy the same spot in the internal, BUT the
                 * capacity of the vector is too big. Time to make a new
                 * internal node and move some leaf nodes turn the current leaf
                 * node into an internal
                 */
                prQuadInternal newInternal = new prQuadInternal();
                // // node = newInternal;
                //
                // // This will only ever happen once if the tree only has a
                // // leaf (1 element) in the tree
                // if (!root.getClass().equals(prQuadInternal.class))
                // {
                // root = node;
                // }
                // put the old leaf back in the tree, this time in a new
                // node
                for (int i = 0; i < bucketSize; i++)
                {
                    insertH(leaf.Elements.get(i), newInternal, xHi, xLo, yHi, yLo);
                }
                // Insert the intended element
                return insertH(elem, newInternal, xHi, xLo, yHi, yLo);

            }
        }
        return node;
    }


    // Pre: elem != null
    // Returns reference to an element x within the tree such that
    // elem.equals(x)is true, provided such a matching element occurs within
    // the tree; returns null otherwise.
    // ----------------------------------------------------------
    /**
     * Locates element in the tree, and returns it.
     *
     * @param Elem
     * @return T
     */
    public T find(T Elem)
    {
        // check to see if value is null
        if (Elem != null && root != null)
        {
            // helper call
            return findHelp(Elem, root, xMin, xMax, yMin, yMax);
        }
        // couldn't be found
        return null;
    }


    // ----------------------------------------------------------
    /**
     * Traverses the prQuadTree by going down specific to more specific ranges.
     *
     * @param elem
     * @param node
     * @param xLo
     * @param xHi
     * @param yLo
     * @param yHi
     * @return T
     */
    @SuppressWarnings("unchecked")
    public T findHelp(T elem, prQuadNode node, long xLo, long xHi, long yLo, long yHi)
    {
        // At the end of the tree, BUT with NO LEAF. NOTHING COULD BE FOUND
        if (node == null)
        {
            return null;
        }
        // Determine what KIND of node is being used
        else if (node.getClass().equals(prQuadInternal.class))
        {
            // Find the direction of which way to move within the internal node
            Direction tempDir = elem.inQuadrant(xLo, xHi, yLo, yHi);
            // Casting
            prQuadInternal internal = (prQuadInternal)node;
            if (tempDir == Direction.NE)
            {
                // adjusting coordinates based on quadrant
                long xSmall = (xHi + xLo) / 2;
                long ySmall = (yHi + yLo) / 2;
                return findHelp(elem, internal.NE, xSmall, xHi, ySmall, yHi);
            }
            else if (tempDir == Direction.NW)
            {
                long xBig = (xHi + xLo) / 2;
                long ySmall = (yHi + yLo) / 2;
                return findHelp(elem, internal.NW, xLo, xBig, ySmall, yHi);
            }
            else if (tempDir == Direction.SW)
            {
                long xBig = (xHi + xLo) / 2;
                long yBig = (yHi + yLo) / 2;
                return findHelp(elem, internal.SW, xLo, xBig, yLo, yBig);
            }
            else if (tempDir == Direction.SE)
            {
                long xSmall = (xHi + xLo) / 2;
                long yBig = (yHi + yLo) / 2;
                return findHelp(elem, internal.SE, xSmall, xHi, yLo, yBig);
            }
            else
            {
                // no Quadrant
                return null;
            }
        }
        // Must be a leaf
        else
        {
            prQuadLeaf leaf = (prQuadLeaf)node;
            // check if this is the correct leaf
            if (leaf.Elements.contains(elem))
            {
                // check which one is the one we're looking for
                for (int index = 0; index < leaf.Elements.size(); index++)
                {
                    if (leaf.getIndex(index).equals(elem))
                    {
                        return leaf.getIndex(index);
                    }
                }
                return null;
            }
            // failed
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Check to see if values are in range. S=small, B=big ----Values being
     * tested:
     *
     * @param xSTest
     * @param xBTest
     * @param ySTest
     * @param yBTest
     *            ----Boundary Range (BR) values:
     * @param xSR
     * @param xBR
     * @param ySR
     * @param yBR
     * @return boolean
     */
    public boolean inRange(long xSTest, long xBTest, long ySTest, long yBTest, long xSR, long xBR,
                           long ySR, long yBR)
    {
        if (xSTest >= xSR && xBTest <= xBR)
        {
            if (ySTest >= ySR && yBTest <= yBR)
            {
                return true;
            }
        }
        // out of bounds
        return false;

    }


    // Pre: xLo, xHi, yLo and yHi define a rectangular region
    // Returns a collection of (references to) all elements x such that x is
    // in the tree and x lies at coordinates within the defined rectangular
    // region, including the boundary of the region.
    // ----------------------------------------------------------
    /**
     * Finds element within a specific region
     *
     * @param xLo
     * @param xHi
     * @param yLo
     * @param yHi
     * @return Vector<T>
     */
    public Vector<T> find(long xLo, long xHi, long yLo, long yHi)
    {
        // make sure we're dealing with an actual tree
        if (root == null)
        {
            return null;
        }
        // call the helper method
        // catalog stores all elements that are found
        Vector<T> catalog = new Vector<T>();
        rangeFinder(root, catalog, xMin, xMax, yMin, yMax, xLo, xHi, yLo, yHi);
        return catalog;
    }


    // ----------------------------------------------------------
    /**
     * Helper method. This method will only end if and only if: 1.) the
     * coordinates are outside the bounds 2.) the traversal is at the end of the
     * tree
     *
     * @param node
     * @param list
     * @param xLo
     * @param xHi
     * @param yLo
     * @param yHi
     *            -------------Range Bounds: L=Little B=Big
     * @param xLRange
     * @param xBRange
     * @param yLRange
     * @param yBRange
     */
    @SuppressWarnings("unchecked")
    public void rangeFinder(prQuadNode node, Vector<T> list, long xLo, long xHi, long yLo,
                            long yHi, long xLRange, long xBRange, long yLRange, long yBRange)
    {
        // At the end of the tree, BUT with NO LEAF. NOTHING COULD BE FOUND
        if (node == null)
        {
            return;
        }
        // Determine what KIND of node is being used
        else if (node.getClass().equals(prQuadInternal.class))
        {
            // Casting
            prQuadInternal internal = (prQuadInternal)node;

            // get coordinates to work with
            long xSmall = (xHi + xLo) / 2;
            long ySmall = (yHi + yLo) / 2;
            long xBig = (xHi + xLo) / 2;
            long yBig = (yHi + yLo) / 2;

            // North East

            // recursive call
            rangeFinder(internal.NE, list, xSmall, xHi, ySmall, yHi, xLRange, xBRange, yLRange,
                yBRange);

            // North West
            rangeFinder(internal.NW, list, xLo, xBig, ySmall, yHi, xLRange, xBRange, yLRange,
                yBRange);

            // South West
            rangeFinder(internal.SW, list, xLo, xBig, yLo, yBig, xLRange, xBRange, yLRange, yBRange);

            // South East
            rangeFinder(internal.SE, list, xSmall, xHi, yLo, yBig, xLRange, xBRange, yLRange,
                yBRange);

        }
        // Must be a leaf
        else
        {
            prQuadLeaf leaf = (prQuadLeaf)node;
            // adds elements into the list vector
            for (int index = 0; index < leaf.Elements.size(); index++)
            {
                // check to see if bounds are still ok.
                if (leaf.Elements.get(index).inBox(xLRange, xBRange, yLRange, yBRange))
                {
                    list.add(leaf.Elements.get(index));
                }
            }
            // ends process

            return;
        }
    }


    // ----------------------------------------------------------
    /**
     * Prints the structure of the tree. Provided by notes.
     *
     * @param sRoot
     * @param Padding
     * @param bobBuilder
     * @return String
     */
    @SuppressWarnings("unchecked")
    public String printTreeHelper(prQuadNode sRoot, String Padding, StringBuilder bobBuilder)
    {
        // Check for empty leaf
        if (sRoot == null)
        {
            bobBuilder.append(Padding + "*\n");
            return bobBuilder.toString();
        }
        // Check for and process SW and SE subtrees
        if (sRoot.getClass().equals(prQuadInternal.class))
        {
            prQuadInternal p = (prQuadInternal)sRoot;
            printTreeHelper(p.SW, Padding + "   ", bobBuilder);
            printTreeHelper(p.SE, Padding + "   ", bobBuilder);
        }
        // Display indentation padding for current node
        // Determine if at leaf or internal and display accordingly
        if (sRoot.getClass().equals(prQuadLeaf.class))
        {
            prQuadLeaf p = (prQuadLeaf)sRoot;
            bobBuilder.append(Padding + p.Elements.toString());
        }
        else
            bobBuilder.append(Padding + "@\n");
        // Check for and process NE and NW subtrees
        if (sRoot.getClass().equals(prQuadInternal.class))
        {
            prQuadInternal p = (prQuadInternal)sRoot;
            printTreeHelper(p.NE, Padding + " ", bobBuilder);
            printTreeHelper(p.NW, Padding + " ", bobBuilder);
        }
        return bobBuilder.toString();
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

// DELETE IS NO LONGER NEEDED:
