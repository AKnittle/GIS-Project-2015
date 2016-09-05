// -------------------------------------------------------------------------
/**
 * The buffer pool. Holds recently used info from previous operations. Cuts cost
 * of most search operations if using a recently used record.
 *
 * @author AndrewK
 * @version Mar 28, 2015
 * @param <T>
 */

public class BufferPool<T>
{

    // BUFFER NODE ------------------------------------------------------------
    /**
     * The Nodes that buffer pool is composed of
     *
     * @author AndrewK
     * @version Mar 28, 2015
     * @param <T>
     */
    @SuppressWarnings("hiding")
    class BufferNode<T>
    {
        /**
         * next is what creates the "chain" of nodes in the pool. the LRU.next
         * will always be null since that is the end of the chain
         */
        BufferNode<T> next;
        /**
         * The element that is being held inside the node
         */
        T             element;


        // ----------------------------------------------------------
        /**
         * Create a new BufferNode object with no defined element
         */
        public BufferNode()
        {
            element = null;
            next = null;
        }


        // ----------------------------------------------------------
        /**
         * Create a new BufferNode object with defined elements. data will be
         * what defines the element.
         *
         * @param data
         */
        public BufferNode(T data)
        {
            element = data;
            next = null;
        }
    }

    // BUFFER POOL IMPLEMENTATION ----------------------------------------------

    /**
     * current size of the pool
     */
    int           size;
    /**
     * maximum size the pool can hold. When at max level LRU starts to get
     * replaced
     */
    int           capacity;

    /**
     * anchor will be the MRU buffer pool node. named the anchor for it is what
     * grounds the chain of nodes... also I like maritime vocabulary so bite me.
     */
    BufferNode<T> anchor;


    // ----------------------------------------------------------
    /**
     * Create a new BufferPool object. Starts with no anchor
     */
    public BufferPool()
    {
        size = 0;
        capacity = 10;
        anchor = null;
    }


    // ----------------------------------------------------------
    /**
     * Updates the pool with a new node being added onto the chain
     * -------------------------------------------------------------------------
     * + If the anchor is null make this new node be the anchor
     * -------------------------------------------------------------------------
     * + Moves other nodes up the chain to LRU. LRU is the end of the chain
     * -------------------------------------------------------------------------
     * + IF size == capacity knock off the current LRU, and replace it.
     *
     * @param elem
     */
    public void update(T elem)
    {
        // make a new BufferNode with an inserted element
        BufferNode<T> newKid = new BufferNode<T>(elem);
        if(find(elem) == true)
        {
            return;
        }
        if (anchor == null)
        {
            anchor = newKid;
            size++;
            return;
        }
        // First make the old anchor equal to newKid's next.
        newKid.next = anchor;
        // Then set anchor equal to newKid. This will preserve the old anchor,
        // and its pointers, while also updating the new anchor.
        anchor = newKid;
        if (size == capacity)
        {
            // There are now 11 nodes in the pool, BUT the pool can only hold 10
            BufferNode<T> tempNode = anchor;
            for (int index = 0; index < 9; index++)
            {
                // Get to the "extra" node (LRU)
                tempNode = tempNode.next;
            }
            // Set the old LRU to null
            tempNode.next = null;
            return;
        }
        else
        {
            // Increase the size to represent the amount of current nodes in the
            // pool.
            size++;
        }
    }


    // Most of the code below exists mainly for testing.
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // ----------------------------------------------------------
    /**
     * Getter method that returns the element that is being searched. returns
     * the element if it is in the pool otherwise it returns null
     *
     * @param elem
     * @return T
     */
    public T get(T elem)
    {
        if (elem == null || anchor == null)
        {
            return null;
        }
        else
        {
            return getElement(elem, anchor);
        }
    }


    // ----------------------------------------------------------
    /**
     * Helper Method ----------------------------------------------------------
     * Recursively start at the anchor and move up the chain until either:
     * ------------------------------------------------------------------------
     * 1: Found our missing element, in which case return the element
     * ------------------------------------------------------------------------
     * 2: Reached the end of our chain.
     *
     * @param elem
     * @param traveler
     *            (TRAVELER SHOULD ALWAYS START AT THE ANCHOR)
     * @return T
     */
    public T getElement(T elem, BufferNode<T> traveler)
    {
        if (traveler == null)
        {
            return null;
        }
        else if (traveler.element.equals(elem))
        {
            return traveler.element;
        }
        else
        {
            if (traveler.next == null)
            {
                return null;
            }
            return getElement(elem, traveler.next);
        }
    }


    // ----------------------------------------------------------
    /**
     * Getter method
     *
     * @param elem
     * @return T
     */
    public boolean find(T elem)
    {
        if (elem == null)
        {
            return false;
        }
        else
        {
            return findElement(elem, anchor);
        }
    }


    // ----------------------------------------------------------
    /**
     * Recursively start at the anchor and move up the chain until either:
     * ------------------------------------------------------------------------
     * 1: Found our missing element, in which case return true
     * ------------------------------------------------------------------------
     * 2: Reached the end of our chain, in which case return false
     *
     * @param elem
     * @param traveler
     *            (TRAVELER SHOULD ALWAYS START AT THE ANCHOR)
     * @return T
     */
    public boolean findElement(T elem, BufferNode<T> traveler)
    {
        if (traveler == null)
        {
            return false;
        }
        else if (traveler.element.equals(elem))
        {
            return true;
        }
        else
        {
            return findElement(elem, traveler.next);
        }
    }

}
