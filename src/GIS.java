import java.util.Vector;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

// -------------------------------------------------------------------------
/**
 * This the "brains" of the operation. It controls everything that goes on in
 * the project. It dictates when things should be parsed, stored in the hash
 * table, and placed in the prQuadTree
 *
 * @author AndrewK
 * @version Mar 27, 2015
 */

public class GIS
{

    // FILES ------------------------------------------------

    /**
     * commandFile = the file with all the commands that will be read from.
     */
    static RandomAccessFile        commandFile;

    /**
     * dataFile = all the records stored in a text file.
     */
    static RandomAccessFile        dataFile;

    /**
     * where everything will be written to.
     */
    static RandomAccessFile        logFile;

    /**
     * Where all the imported records are being stored.
     */
    static RandomAccessFile        myDataBase;

    // DATA TYPES -------------------------------------------
    /**
     * tree is the prQuadTee used to store all the records (LocationGIS records)
     * relating to geographic locations
     */
    static prQuadTree<Point>       tree;
    /**
     * table is the HashTable used to store all records (RecordGIS records)
     * based on their name-state abbreviation.
     */
    static HashTable<Long, String> table;

    /**
     * The buffer pool. It uses RecordGIS which stores all a record's info
     * without having to read from the database file. It can hold up to only 10
     * records at a time.
     */
    static BufferPool<RecordGIS>   pool;
    // VARIOUS FIELDS ----------------------------------------

    /**
     * West boundary represented as a long.
     */
    static long                    westL;
    /**
     * East boundary represented as a long.
     */
    static long                    eastL;
    /**
     * South boundary represented as a long.
     */
    static long                    southL;
    /**
     * North boundary represented as a long.
     */
    static long                    northL;


    // ----------------------------------------------------------
    /**
     * The main method that handles everything for the project Initial parsing
     * of the command file starts here. Most of the parsing done, will be with
     * code from Project 1 that has been reporpsed.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args)
                                          throws IOException
    {
        // NOTE: we DO NOT initialize the tree here. That will be done later in
        // commandProcessor() when "world" is called.

        // make a new buffer pool
        pool = new BufferPool<RecordGIS>();
        // Make a new defualt table.
        table = new HashTable<Long, String>();
        // Set up the command reading file
        commandFile = new RandomAccessFile(args[1], "r");
        // Set up the log writing file
        logFile = new RandomAccessFile(args[2], "rw");
        // Build an empty database file
        myDataBase = new RandomAccessFile(args[0], "rw");
        logFile.setLength(0);
        myDataBase.setLength(0);
        // Printing...
        // I sign my name as part of authenticity.
        logFile.writeBytes("Created by: Andrew Knittle, andrk11@vt.edu");
        logFile.writeBytes("\n");
        logFile.writeBytes("--------------------------------------------------------------");
        logFile.writeBytes("\n");
        logFile.writeBytes("STARTING PROGRAM: \n");
        logFile.writeBytes("\t\t\t\t  GIS PROGRAM \n");
        logFile.writeBytes("\n");
        logFile.writeBytes("Data base file: \t" + args[0] + "\n");
        logFile.writeBytes("Command file: \t" + args[1] + "\n");
        logFile.writeBytes("Log file: \t" + args[2] + "\n");
        logFile.writeBytes("\n");
        logFile.writeBytes("Quadtree children are printed in the order SW  SE  NE  NW \n");
        logFile.writeBytes("--------------------------------------------------------------");
        logFile.writeBytes("\n");
        // Begin processing...
        try
        {
            commandProcessor();
        }
        catch (Exception e)
        {
            // on the off chance something terrible happens
            logFile.writeBytes("FATAL ERROR: \n \t PROGRAM TERMINATED...");
            logFile.close();
            dataFile.close();
            commandFile.close();
            myDataBase.close();
        }
    }


    /**
     * Imports all the records in the Data Base file (dataFile) into the
     * hashtable and tree.
     *
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static void recording()
                                  throws IOException
    {
        long highScore = 0;
        // Start at the beginning of the file.
        dataFile.seek(0);
        String dataLine = dataFile.readLine();
        dataLine = dataFile.readLine();
        // To simplify things we will use a record variable and instantiate it
        // through out the while loop for each line.

        // Variable for the dataline's offset.
        long offSet;
        // while loop similar to the commandProcessor's, but with the data base
        // (dataFile) file
        while (dataLine != null)
        {
            String key;
            // For each line in grab the necessary info for a RecordGIS object
            offSet = 0;
            // Scan the line you are on.
            Scanner splitter = new Scanner(dataLine);
            splitter.useDelimiter("\\|");
            // Get all the related strings needed for the record.
            splitter.next();
            String FEATURE_NAME = splitter.next();
            String FEATURE_CLASS = splitter.next();
            String STATE_ALPHA = splitter.next();
            splitter.next();
            splitter.next();
            splitter.next();

            // COORDINATES NEEDED:
            // For Long:
            // DDDMMSS
            // For Lat:
            // DDMMSS
            // DD(D)*3600 + MM*60 + SS
            String PRIMARY_LAT_DMS = splitter.next();
            String PRIM_LONG_DMS = splitter.next();

            // TODO: ALWAYS BE CAREFUL WITH WITH SWITCHING LAT AND LONG!!!

            long latL;
            long longL;
            // Validation check
            if (validLocation(PRIMARY_LAT_DMS, PRIM_LONG_DMS))
            {
                latL = coordinateLong(PRIMARY_LAT_DMS);
                longL = coordinateLong(PRIM_LONG_DMS);
                // check if in world bounds
                if (inWorld(longL, latL))
                {
                    long newScore = 0;
                    offSet = myDataBase.getFilePointer();
                    myDataBase.writeBytes(dataLine);
                    myDataBase.writeBytes("\n");
                    // insert everything into their corresponding indexes.
                    Point pin = new Point(longL, latL, offSet);
                    // Build the key for the table.
                    key = FEATURE_NAME + ":" + STATE_ALPHA;
                    // The hash table, also get the number of probes.
                    newScore = table.insert(offSet, key);
                    // Determine the max amount of probes
                    highScore = Math.max(newScore, highScore);
                    // The prQuad Tree
                    tree.insert(pin);
                }
            }
            // Initiated record with the strings.
            splitter.close();
            dataLine = dataFile.readLine();
        }
        logFile.writeBytes("Number of Imported Features by name: " + table.uniqueVals + "\n");
        logFile.writeBytes("Number of Max Probes done: " + highScore + "\n");
        logFile.writeBytes("Number of Imported Locations: " + tree.totalElems + "\n");
        return;
    }


    // ----------------------------------------------------------
    /**
     * Goes through the buffer pool looking for a record with the same offset.
     * If nothing is found null is returned otherwise a built string is
     * returned.
     *
     * @param offset
     * @return String
     */
    public static String bufferFindByOffset(long offset)
    {
        // first see if the pool even has anything.
        if (pool.size == 0 || pool.anchor == null)
        {
            return null;
        }
        // getting ready to traverse through the pool
        int index = 0;
        // placeholder node for looking at data.
        BufferPool<RecordGIS>.BufferNode<RecordGIS> current = pool.anchor;
        // build the string.
        while (index < pool.size)
        {
            // See if there's a match by name
            if (current != null && current.element.offset == offset)
            {
                // Add data into the string.
                RecordGIS record = current.element;
                return record.completeLine;
            }
            // update traversal
            index++;
            current = current.next;
        }
        // nothing was found
        return null;

    }


    // ----------------------------------------------------------
    /**
     * Goes through the buffer pool looking for a record with the position as
     * the coordinates passed in. If nothing is found null is returned otherwise
     * a built string is returned.
     *
     * @param longitude
     * @param latitude
     * @return String
     */
    public static String bufferFindByLocation(long longitude, long latitude)
    {
        // first see if the pool even has anything.
        if (pool.size == 0 || pool.anchor == null)
        {
            return null;
        }
        // getting ready to traverse through the pool
        int index = 0;
        // placeholder node for looking at data.
        BufferPool<RecordGIS>.BufferNode<RecordGIS> current = pool.anchor;
        // build the string.
        StringBuilder sb = new StringBuilder();
        sb.append("");
        while (index < pool.size)
        {
            // See if there's a match by location
            if (current != null && current.element.latitude == latitude
                && current.element.longitude == longitude)
            {
                // Add data into the string.
                RecordGIS record = current.element;
                sb.append(record.offset + ": \t");
                sb.append(humanRead(record.completeLine));
                sb.append("\n");
            }
            // update traversal
            index++;
            current = current.next;
        }
        if (sb.toString().equals(""))
        {
            // nothing was found
            return null;
        }
        else
        {
            // a match was found in the pool, and it will be returned.
            return sb.toString();
        }
    }


    // ----------------------------------------------------------
    /**
     * executes the "what_is_at" command. Finds a set of records corresponding
     * to a specific location.
     *
     * @param x
     * @param y
     * @throws IOException
     */
    public static void whatIsAt(String y, String x)
                                                   throws IOException
    {
        // Convert to longs
        // WHAT DID I TELL YOU ABOUT ACCIDENTLY MIXING UP LATITUDE AND
        // LONGITUDE!?!?
        long longitude = coordinateLong(x);
        long latitude = coordinateLong(y);
        // check to see if in the world
        if (!inWorld(longitude, latitude))
        {
            // out of bounds
            logFile.writeBytes("FAILED: \n");
            logFile.writeBytes("\t OUTSIDE OF WORLD BOUNDS \n");
            return;
        }
        String record = "";
        StringBuilder sb = new StringBuilder();
        // make a temporary point to find the node that matches it. offset is
        // a dummy value.
        Point seeker = new Point(longitude, latitude, 0);
        Point found = tree.find(seeker);
        if (found != null)
        {
            // found a matching point!
            logFile.writeBytes("The following were found at:");
            logFile.writeBytes("\t");
            logFile.writeBytes(x);
            logFile.writeBytes("\t");
            logFile.writeBytes(y);
            logFile.writeBytes("\n \n");
            // get every offset corresponding to the found point
            for (int r = 0; r < found.container.size(); r++)
            {
                // get rth offset in the point.
                long offset = found.grab(r);
                sb.append(offset + ": \t");
                // grab the line at the offset, and make it easy to read.
                // Check the buffer for possible matches.
                String bufferFind = bufferFindByOffset(offset);
                if (bufferFind == null)
                {
                    // have to read from the file
                    myDataBase.seek(offset);
                    record = myDataBase.readLine();
                }
                else
                {
                    // bufferFind found a match. no need to read from file.
                    record = bufferFind;
                }
                // INSERT INTO BUFFER POOL
                RecordGIS recordD = new RecordGIS(record, offset);
                pool.update(recordD);
                record = humanRead2(record);
                // add the data gathered to the string builder.
                sb.append(record);
                sb.append("\n");
            }
            // Output has been built and ready to be written.
            logFile.writeBytes(sb.toString());
        }
        else
        {
            // A match could not be found.
            logFile.writeBytes("FAILED: \n");
            logFile.writeBytes("\t NOTHING FOUND \n");
        }

    }


    // ----------------------------------------------------------
    /**
     * The what_is_in helper method call. The 4 long parameters define the
     * bounds of the region being searched. The integer parameter defines what
     * kind of "what_is_in" command is being called. 0 for the basic command, 1
     * for '-l', and 2 for '-c'
     *
     * @param longitude
     * @param latitude
     * @param width
     * @param height
     * @param type
     * @throws IOException
     */
    public static void whatIsIn(String longitude, String latitude, String width, String height,
                                int type)
                                         throws IOException
    {

        long centerY = coordinateLong(latitude);
        long centerX = coordinateLong(longitude);
        // Get range of region
        long yRange = Long.parseLong(height, 10);
        long xRange = Long.parseLong(width, 10);
        // Get the bounds of the region
        long longMax = centerX + xRange;
        long longMin = centerX - xRange;
        long latMax = centerY + yRange;
        long latMin = centerY - yRange;
        // Get the vector for every point within region
        Vector<Point> list = tree.find(longMin, longMax, latMin, latMax);
        StringBuilder sb = new StringBuilder();
        StringBuilder endLine = new StringBuilder();
        // set up counter for how many entries.
        int count = 0;
        // check if basic
        if (type == 0)
        {
            // get every offset corresponding to the found point
            for (int vect = 0; vect < list.size(); vect++)
            {
                Point current = list.get(vect);
                for (int index = 0; index < current.container.size(); index++)
                {
                    count++;
                    // get nth offset in the point.
                    long offset = current.grab(index);
                    sb.append(offset + ": \t");
                    // grab the line at the offset, and make it easy to read.
                    // Check the buffer for possible matches.
                    String line;
                    String bufferFind = bufferFindByOffset(offset);
                    if (bufferFind == null)
                    {
                        // have to read from the file
                        myDataBase.seek(offset);
                        line = myDataBase.readLine();
                    }
                    else
                    {
                        // bufferFind found a match. no need to read from file.
                        line = bufferFind;
                    }
                    sb.append(humanRead3(line));
                    sb.append("\n");
                    RecordGIS record = new RecordGIS(line, offset);
                    pool.update(record);
                }
            }
            // build the final string with parameters given
            endLine.append("The following ");
            endLine.append(count);
            endLine.append(" features were found in (");
            endLine.append(longitude);
            endLine.append(" +/- ");
            endLine.append(xRange);
            endLine.append(", ");
            endLine.append(latitude);
            endLine.append(" +/- ");
            endLine.append(yRange);
            endLine.append(")");
            endLine.append("\n");
            if (count != 0)
            {
                // Put everything together.
                endLine.append(sb.toString());
                logFile.writeBytes(endLine.toString());
                return;
            }
            else
            {
                // Nothing was found
                logFile.writeBytes("\n NOTHING WAS FOUND WITHIN GIVEN REGION \n");
                return;
            }
        }
        // check if -l
        else if (type == 1)
        {
            for (int vect = 0; vect < list.size(); vect++)
            {
                Point current = list.get(vect);
                for (int index = 0; index < current.container.size(); index++)
                {
                    count++;
                    // get nth offset in the point.
                    long offset = current.grab(index);
                    sb.append(offset + ": \t");
                    String line;
                    String bufferFind = bufferFindByOffset(offset);
                    if (bufferFind == null)
                    {
                        // have to read from the file
                        myDataBase.seek(offset);
                        line = myDataBase.readLine();
                    }
                    else
                    {
                        // bufferFind found a match. no need to read from file.
                        line = bufferFind;
                    }
                    sb.append(fullRead(line));
                    sb.append("\n");
                    // putting the record into the pool
                    RecordGIS record = new RecordGIS(line, offset);
                    pool.update(record);
                }
            }
            // build the final string with parameters given
            endLine.append("The following ");
            endLine.append(count);
            endLine.append(" features were found in (");
            endLine.append(longitude);
            endLine.append(" +/- ");
            endLine.append(xRange);
            endLine.append(", ");
            endLine.append(latitude);
            endLine.append(" +/- ");
            endLine.append(yRange);
            endLine.append(")");
            endLine.append("\n");
            if (count != 0)
            {
                // Put everything together.
                endLine.append(sb.toString());
                logFile.writeBytes(endLine.toString());
                return;
            }
            else
            {
                // Nothing was found
                logFile.writeBytes("\n NOTHING WAS FOUND WITHIN GIVEN REGION \n");
                return;
            }
        }
        // must be -c
        else
        {
            // COUNT the number of offsets found
            for (int vect = 0; vect < list.size(); vect++)
            {
                Point current = list.get(vect);
                for (int index = 0; index < current.container.size(); index++)
                {
                    count++;
                    // get nth offset in the point.
                    long offset = current.grab(index);
                    String line;
                    String bufferFind = bufferFindByOffset(offset);
                    if (bufferFind == null)
                    {
                        // have to read from the file
                        myDataBase.seek(offset);
                        line = myDataBase.readLine();
                    }
                    else
                    {
                        // bufferFind found a match. no need to read from file.
                        line = bufferFind;
                    }
                    RecordGIS record = new RecordGIS(line, offset);
                    pool.update(record);
                }
            }
            // build the final string with parameters given
            endLine.append(count);
            endLine.append(" features were found in (");
            endLine.append(longitude);
            endLine.append(" +/- ");
            endLine.append(xRange);
            endLine.append(", ");
            endLine.append(latitude);
            endLine.append(" +/- ");
            endLine.append(yRange);
            endLine.append(")");
            endLine.append("\n");
            if (count == 0)
            {
                logFile.writeBytes("\n NOTHING WAS FOUND WITHIN GIVEN REGION");
                return;
            }
            // Write the situation to the file.
            logFile.writeBytes(endLine.toString());
        }

    }


    // ----------------------------------------------------------
    /**
     * Reads and prints every single non-empty field in the record. After
     * building the string it is returned, formatted and labeled.
     *
     * @param record
     * @return String
     */
    public static String fullRead(String record)
    {
        // bob the string builder will continually be updated with useful data
        StringBuilder bob = new StringBuilder();
        bob.append("");
        Scanner splitter = new Scanner(record);
        splitter.useDelimiter("\\|");
        String FEATURE_ID = splitter.next();
        if (FEATURE_ID != null && !FEATURE_ID.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("FEATURE ID : \t");
            bob.append(FEATURE_ID);
        }
        String FEATURE_NAME = splitter.next();
        if (FEATURE_NAME != null && !FEATURE_NAME.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("FEATURE NAME : \t");
            bob.append(FEATURE_NAME);
        }
        String FEATURE_CLASS = splitter.next();
        if (FEATURE_CLASS != null && !FEATURE_CLASS.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("FEATURE CLASS : \t");
            bob.append(FEATURE_CLASS);
        }
        String STATE_ALPHA = splitter.next();
        if (STATE_ALPHA != null && !STATE_ALPHA.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("STATE ALPHA : \t");
            bob.append(STATE_ALPHA);
        }
        String STATE_NUMERIC = splitter.next();
        if (STATE_NUMERIC != null && !STATE_NUMERIC.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("STATE NUMERIC : \t");
            bob.append(STATE_NUMERIC);
        }
        String COUNTY_NAME = splitter.next();
        if (COUNTY_NAME != null && !COUNTY_NAME.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("STATE NUMERIC : \t");
            bob.append(COUNTY_NAME);
        }
        splitter.next(); // COUNTY_NUMERIC
        String PRIMARY_LAT_DMS = splitter.next();
        if (PRIMARY_LAT_DMS != null && !PRIMARY_LAT_DMS.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("Latitude : \t");
            bob.append(PRIMARY_LAT_DMS);
        }
        String PRIM_LONG_DMS = splitter.next();
        if (PRIM_LONG_DMS != null && !PRIM_LONG_DMS.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("Longitude : \t");
            bob.append(PRIM_LONG_DMS);
        }
        splitter.next(); // PRIM_LAT_DEC
        splitter.next();// PRIM_LONG_DEC
        splitter.next();// SOURCE_LAT_DMS
        splitter.next();// SOURCE_LONG_DMS
        splitter.next();// SOURCE_LAT_DEC
        splitter.next();// SOURCE_LONG_DEC
        String ELEV_IN_M = splitter.next();
        String ELEV_IN_FT = splitter.next();
        if (ELEV_IN_M != null && !ELEV_IN_M.equals("") && ELEV_IN_FT != null
            && !ELEV_IN_FT.equals(""))
        {

            bob.append("\n");
            bob.append("\t");
            bob.append("Elevation in: (feet, meters): \t");
            bob.append(ELEV_IN_FT);
            bob.append(" (ft), \t");
            bob.append(ELEV_IN_M);
            bob.append(" (m)");
            bob.append("\n");
        }
        else if (ELEV_IN_FT != null && !ELEV_IN_FT.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("Elevation in: (feet): \t");
            bob.append(ELEV_IN_FT);
        }
        else if (ELEV_IN_M != null && !ELEV_IN_M.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("Elevation in: (meters): \t");
            bob.append(ELEV_IN_M);
        }
        String MAP_NAME = splitter.next();
        if (MAP_NAME != null && !MAP_NAME.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("USGS Quad: \t");
            bob.append(MAP_NAME);
        }
        String DATE_CREATED = splitter.next();
        if (DATE_CREATED != null && !DATE_CREATED.equals(""))
        {
            bob.append("\n");
            bob.append("\t");
            bob.append("Date Created: \t");
            bob.append(DATE_CREATED);
        }
        try
        {
            String DATE_EDITED = splitter.next();
            if (DATE_EDITED != null && !DATE_EDITED.equals(""))
            {
                bob.append("\n");
                bob.append("\t");
                bob.append("Date Edited: \t");
                bob.append(DATE_EDITED);
            }
        }
        catch (Exception e)
        {
            bob.append("\n");
            splitter.close();
            // Parsing and building our string is complete. Every important
// piece of
            // info is printed on a line describing what it is as well.
            return bob.toString();
        }
        bob.append("\n");
        splitter.close();
        // Parsing and building our string is complete. Every important piece of
        // info is printed on a line describing what it is as well.
        return bob.toString();
    }


    // ----------------------------------------------------------
    /**
     * executes "what_is" command. Search is based on a record's name and state.
     *
     * @param name
     * @param state
     * @return String
     * @throws IOException
     */
    public static String whatIs(String name, String state)
                                                          throws IOException
    {
        String key = name + ":" + state;
        // nothing found in the pool
        ArrayList<Long> list;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        // search for record.
        try
        {
            // A set of record offsets have been found.
            list = table.search(key);
            for (int i = 0; i < list.size(); i++)
            {
                long offset = list.get(i);
                sb.append(offset + ": \t");
                String line;
                // Check the pool
                String bufferFind = bufferFindByOffset(offset);
                if (bufferFind != null)
                {
                    // The pool had a match.
                    line = bufferFind;
                }
                else
                {
                    // grab the line at the offset, and make it easy to read.
                    myDataBase.seek(offset);
                    line = myDataBase.readLine();
                }
                // putting the record into the pool
                RecordGIS record = new RecordGIS(line, offset);
                pool.update(record);
                // build the output with found data.
                sb.append(humanRead(line));
                sb.append("\n");
            }
            // return string built from the search.
            return sb.toString();
        }
        catch (NoSuchElementException e)
        {
            // Search failed to find anything.
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * Checks to make sure the location strings passed in are valid. Returns
     * true if valid otherwise return false.
     *
     * @param y
     * @param x
     * @return boolean
     */
    public static boolean validLocation(String y, String x)
    {
        if (y.equals("") || y.equals("Unknown"))
        {
            return false;
        }
        if (x.equals("") || x.equals("Unknown"))
        {
            return false;
        }
        return true;
    }


    // ----------------------------------------------------------
    /**
     * Method that goes through the Command File and sends that info to the
     * various method calls.
     *
     * @throws IOException
     */
    public static void commandProcessor()
                                         throws IOException
    {
        // Records how many commands have been passed through.
        int commandCount = 0;
        // This will hold the command from the string. Identification of which
        // will take place using this as well.
        String command;
        // Set up the DataReader to process the commands
        String commandLine = commandFile.readLine();
        // go through each line of the command file. If the end of file comes
        // before the quit command the while loop will end there
        while (commandLine != null)
        {
            // if the line has no comments ";" then read the line
            if (commandLine.charAt(0) != ';')
            {
                Scanner splitter = new Scanner(commandLine);
                splitter.useDelimiter("\t");
                command = splitter.next();
                // Check before getting an offset, because quit does not have an
                // offset
                if (command.equals("quit"))
                {
                    // Increase counter.
                    commandCount++;
                    // CLOSE EVERY FILE WHEN QUIT IS CALLED
                    commandFile.close();
                    dataFile.close();
                    logFile.writeBytes("Command " + commandCount + ":");
                    logFile.writeBytes("\t quit \n");
                    logFile.writeBytes("TERMINATING PROGRAM... \n");
                    logFile.close();
                    myDataBase.close();
                    splitter.close();
                    return;
                }
                else
                {
                    // from here we need to see which command is going to be
                    // used.
                    if (command.equals("world"))
                    {
                        // Set the world bounds of the tree and the rest of the
                        // indexes

                        // Longitude:
                        String west = splitter.next();
                        String east = splitter.next();
                        // Latitude:
                        String south = splitter.next();
                        String north = splitter.next();
                        // Converted:
                        westL = coordinateLong(west);
                        eastL = coordinateLong(east);
                        southL = coordinateLong(south);
                        northL = coordinateLong(north);
                        splitter.close();
                        tree = new prQuadTree<Point>(westL, eastL, southL, northL, 4);
                        logFile.writeBytes("Command " + commandCount + ":");
                        logFile.writeBytes("\t world " + west + " " + east + " " + south + " "
                            + north + "\n");
                        logFile.writeBytes("\n");
                        logFile.writeBytes("SETTING WORLD BOUNDS (REPRESENTED IN SECONDS): \n");
                        logFile.writeBytes("\t\t\t");
                        logFile.writeBytes("" + northL + "\n");
                        logFile.writeBytes("");
                        logFile.writeBytes(westL + "\t\t\t\t\t" + eastL + "\n");
                        logFile.writeBytes("\t\t\t");
                        logFile.writeBytes("" + southL + "\n");
                        logFile
                            .writeBytes("--------------------------------------------------------------");
                        logFile.writeBytes("\n");
                    }
                    if (command.equals("import"))
                    {
                        // Increase counter.
                        commandCount++;
                        // Puts everything in the Hash Table and tree Get the
                        // name of the file to work with.Can do multiple
                        // imports.
                        String data = splitter.next();
                        // Set up the data reading file
                        dataFile = new RandomAccessFile(data, "r");
                        splitter.close();
                        logFile.writeBytes("Importing records from: \t" + data + " \n");
                        logFile.writeBytes("Command " + commandCount + ":");
                        logFile.writeBytes("\t import \n");
                        logFile.writeBytes("\n");

                        // executes the command
                        recording();

                        logFile.writeBytes("Import complete");
                        logFile.writeBytes("\n");
                        logFile
                            .writeBytes("--------------------------------------------------------------");
                        logFile.writeBytes("\n");
                    }
                    if (command.equals("what_is_at"))
                    {
                        // Increase counter.
                        commandCount++;
                        // Get a specific location in the tree.
                        // Longitude:
                        String x = splitter.next();
                        // Latitude:
                        String y = splitter.next();
                        splitter.close();

                        logFile.writeBytes("Command " + commandCount + ":");
                        logFile.writeBytes("\t what_is_at \t" + x + "\t" + y + "\n");

                        // executes what is at command
                        whatIsAt(x, y);

                        logFile.writeBytes("\n");
                        logFile
                            .writeBytes("--------------------------------------------------------------");
                        logFile.writeBytes("\n");

                    }
                    if (command.equals("what_is"))
                    {
                        // Increase counter.
                        commandCount++;
                        // Get a specific record from the table, based on name.

                        String name = splitter.next();
                        String state = splitter.next();
                        splitter.close();

                        logFile.writeBytes("Command " + commandCount + ":");
                        logFile.writeBytes("\t what_is " + name + " " + state + " \n");
                        logFile.writeBytes("\n");

                        String result = whatIs(name, state);
                        // Check the success of the search.
                        if (result == null)
                        {
                            // Failed.
                            logFile.writeBytes("SEARCH FAILED: RECORD NOT IN TABLE \n");
                        }
                        else
                        {
                            // Passed.
                            logFile.writeBytes(result);
                        }
                        logFile
                            .writeBytes("--------------------------------------------------------------");
                        logFile.writeBytes("\n");
                    }
                    if (command.equals("what_is_in"))
                    {
                        // Increase counter.
                        commandCount++;
                        String type = splitter.next();
                        logFile.writeBytes("Command " + commandCount + ":");
                        logFile.writeBytes("\t what_is_in \t");
                        if (type.equals("-l"))
                        {
                            // log every important non-empty field, nicely
                            // formatted and labeled. Do not log any empty
                            // fields.

                            // Get the center of region
                            String latitude = splitter.next();
                            String longitude = splitter.next();
                            // Get range of region
                            String height = splitter.next();
                            String width = splitter.next();
                            splitter.close();

                            logFile.writeBytes("-l " + latitude + " " + longitude + " " + height
                                + " " + width + "\n");
                            // Call the corresponding helper function
                            whatIsIn(longitude, latitude, width, height, 1);
                            logFile.writeBytes("\n");
                            logFile
                                .writeBytes("--------------------------------------------------------------");
                            logFile.writeBytes("\n");
                        }
                        else if (type.equals("-c"))
                        {
                            // Log the number of GIS records in the database
                            // file whose coordinates fall within the closed
                            // rectangle with the specified height and width,
                            // centered at the center.Do not log any data from
                            // the records themselves
                            // Get the center of region
                            String latitude = splitter.next();
                            String longitude = splitter.next();
                            // Get range of region
                            String height = splitter.next();
                            String width = splitter.next();
                            splitter.close();

                            logFile.writeBytes("-c " + latitude + " " + longitude + " " + height
                                + " " + width + "\n");
                            // Call the corresponding helper function
                            whatIsIn(longitude, latitude, width, height, 2);
                            logFile.writeBytes("\n");
                            logFile
                                .writeBytes("--------------------------------------------------------------");
                            logFile.writeBytes("\n");
                        }
                        else
                        {
                            // log the offset at which the record was found, and
                            // the feature name, the state name, and the primary
                            // latitude and primary longitude. Do not log any
                            // other data from the matching records.
                            String latitude = type;
                            String longitude = splitter.next();
                            // Get range of region
                            String height = splitter.next();
                            String width = splitter.next();
                            splitter.close();

                            logFile.writeBytes(latitude + " " + longitude + " " + height + " "
                                + width + "\n");
                            // Call the corresponding helper function
                            whatIsIn(longitude, latitude, width, height, 0);
                            logFile.writeBytes("\n");
                            logFile.writeBytes("\n");
                            logFile
                                .writeBytes("--------------------------------------------------------------");
                            logFile.writeBytes("\n");
                        }
                    }
                    if (command.equals("debug"))
                    {
                        // Increase counter.
                        commandCount++;

                        // Log the contents of the specified index structure ina
                        // fashion that makes the internal structure and
                        // contents of the index clear. It is not necessary to
                        // be overly verbose here, but it would be useful to
                        // include information like key values and file offsets
                        // where appropriate.
                        logFile.writeBytes("Command " + commandCount + ":");
                        logFile.writeBytes("\t debug \t");
                        String type = splitter.next();
                        logFile.writeBytes(type);
                        logFile.writeBytes("\n");
                        // find out which structure to print from.
                        if (type.equals("pool"))
                        {
                            // Print the Buffer Pool
                            String diver = printPool();
                            if (diver != null)
                            {
                                logFile.writeBytes(diver);
                            }
                            else
                            {
                                // Just in case something happens...
                                logFile.writeBytes("Oops... That wasn't supposed to happen...\n");
                            }
                        }
                        else if (type.equals("hash"))
                        {
                            // Print the Hash Table
                            String slasher = printHash();
                            if (slasher != null)
                            {
                                logFile.writeBytes(slasher);
                            }
                            else
                            {
                                // Just in case something happens...
                                logFile.writeBytes("Oops... That wasn't supposed to happen...\n");
                            }
                        }
                        else
                        {
                            // Print the prQuad Tree.
                            StringBuilder sb = new StringBuilder();
                            logFile.writeBytes("\n");
                            logFile.writeBytes(tree.printTreeHelper(tree.root, "-", sb));
                            logFile.writeBytes("\n");
                        }
                        logFile.writeBytes("\n");
                        logFile
                            .writeBytes("--------------------------------------------------------------");
                        logFile.writeBytes("\n");

                    }
                }
                splitter.close();
            }
            // moves the pointer along the file
            commandLine = commandFile.readLine();
        }
        // Close all files just in case quit is never called.
        logFile.close();
        dataFile.close();
        myDataBase.close();
        commandFile.close();
    }


    // ----------------------------------------------------------
    /**
     * Grabs everything in the Hash Table and returns it as a string. Prints
     * everything offset corresponding to the same key value (which is also
     * printed)
     *
     * @return String
     */
    public static String printHash()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("Format of display is\n");
        sb.append("Slot:  [Key, [Offset]]\n");
        sb.append("Current Table size: ");
        sb.append(table.capacity);
        sb.append("\n");
        sb.append("Total number of elements: ");
        sb.append(table.uniqueVals);
        sb.append("\n");
        sb.append("\n");
        // For loop that goes through every single slot looking for an offset.
        for (int seeker = 0; seeker < table.capacity; seeker++)
        {
            if (table.table[seeker] != null)
            {
                // get which slot we're at
                sb.append(seeker);
                sb.append(":  ");
                sb.append("[");
                // grab the slot's key
                sb.append(table.table[seeker].key);
                sb.append(", ");
                sb.append("[");
                ArrayList<Long> list = table.table[seeker].contained;
                for (int i = 0; i < list.size(); i++)
                {
                    if (i > 0 && i < list.size() - 1)
                    {
                        // for ease of notation
                        sb.append(", ");
                    }
                    // grab all the offsets for this key
                    long offset = list.get(i);
                    sb.append(offset);
                }
                // close all the brackets
                sb.append("]");
                sb.append("]\n");
            }
        }
        sb.append("\n");
        // return the string that has been built.
        return sb.toString();
    }


    // ----------------------------------------------------------
    /**
     * Grabs everything in the Buffer Pool and returns it as a string
     *
     * @return String
     */
    public static String printPool()
    {
        // first see if the pool even has anything.
        if (pool.size == 0 || pool.anchor == null)
        {
            return null;
        }
        // getting ready to traverse through the pool
        int index = 0;
        // placeholder node for looking at data.
        BufferPool<RecordGIS>.BufferNode<RecordGIS> current = pool.anchor;
        // build the string.
        StringBuilder sb = new StringBuilder();
        sb.append("MRU \n");
        while (index < pool.size)
        {
            // Add data into the string.
            RecordGIS record = current.element;
            sb.append(record.offset + ": \t");
            sb.append(record.completeLine);
            sb.append("\n");
            // update traversal
            index++;
            current = current.next;
        }
        // Print out the entire thing.
        sb.append("LRU \n");
        return sb.toString();

    }


    // ----------------------------------------------------------
    /**
     * checks to see if in the world bounds.
     *
     * @param x
     * @param y
     * @return boolean
     */
    public static boolean inWorld(long x, long y)
    {
        if (x >= westL && x <= eastL)
        {
            if (y >= southL && y <= northL)
            {
                return true;
            }
            return false;
        }
        return false;
    }


    // ----------------------------------------------------------
    /**
     * Builds a string that has all the the useful details about the record.
     * USED FOR what_is
     *
     * @param record
     * @return String
     */
    @SuppressWarnings("resource")
    public static String humanRead(String record)
    {
        Scanner splitter = new Scanner(record);
        splitter.useDelimiter("\\|");
        // Get all the related strings needed for the record.
        splitter.next();
        splitter.next();
        splitter.next();
        String STATE_ALPHA = splitter.next();
        splitter.next();
        String COUNTY_NAME = splitter.next();
        splitter.next(); // COUNTY_NUMERIC
        String PRIMARY_LAT_DMS = splitter.next();
        String PRIMARY_LONG_DMS = splitter.next();
        StringBuilder sb = new StringBuilder();
        sb.append(COUNTY_NAME + "\t");
        sb.append(STATE_ALPHA + "\t\t\t");
        sb.append(PRIMARY_LAT_DMS + "\t\t");
        sb.append(PRIMARY_LONG_DMS);
        return sb.toString();
    }


    // ----------------------------------------------------------
    /**
     * Builds a string that has all the the useful details about the record.
     * USED FOR what_is_at
     *
     * @param record
     * @return String
     */
    @SuppressWarnings("resource")
    public static String humanRead2(String record)
    {
        Scanner splitter = new Scanner(record);
        splitter.useDelimiter("\\|");
        // Get all the related strings needed for the record.
        splitter.next();
        String STATE_NAME = splitter.next();
        splitter.next();
        String STATE_ALPHA = splitter.next();
        splitter.next();
        String COUNTY_NAME = splitter.next();
        StringBuilder sb = new StringBuilder();
        sb.append(STATE_NAME + "\t");
        sb.append(COUNTY_NAME + "\t");
        sb.append(STATE_ALPHA);
        return sb.toString();
    }


    // ----------------------------------------------------------
    /**
     * Builds a string that has all the the useful details about the record.
     * USED FOR what_is_in
     *
     * @param record
     * @return String
     */
    @SuppressWarnings("resource")
    public static String humanRead3(String record)
    {
        Scanner splitter = new Scanner(record);
        splitter.useDelimiter("\\|");
        // Get all the related strings needed for the record.
        splitter.next();
        String STATE_NAME = splitter.next();
        splitter.next();
        String STATE_ALPHA = splitter.next();
        splitter.next();
        String COUNTY_NAME = splitter.next();
        splitter.next(); // COUNTY_NUMERIC
        String PRIMARY_LAT_DMS = splitter.next();
        String PRIMARY_LONG_DMS = splitter.next();
        StringBuilder sb = new StringBuilder();
        sb.append(STATE_NAME + "\t");
        sb.append(COUNTY_NAME + "\t");
        sb.append(STATE_ALPHA + "\t\t\t");
        sb.append(PRIMARY_LAT_DMS + "\t\t");
        sb.append(PRIMARY_LONG_DMS);
        return sb.toString();
    }


    // ----------------------------------------------------------
    /**
     * Takes the string version of a coordinate and gives back its value as a
     * long in seconds format.
     *
     * @param direction
     * @return long
     */
    public static long coordinateLong(String direction)
    {
        // null check just in case
        if (direction == null || direction.length() < 6)
        {
            return 0;
        }
        long seconds = 0;
        // check which direction this is.
        if (direction.length() == 8)
        {
            // If the direction is 8 characters long then this is East or West
            StringBuilder day = new StringBuilder();
            day.append(direction.charAt(0));
            day.append(direction.charAt(1));
            day.append(direction.charAt(2));
            seconds = Long.parseLong(day.toString()) * 3600;

            StringBuilder min = new StringBuilder();
            min.append(direction.charAt(3));
            min.append(direction.charAt(4));
            seconds = (Long.parseLong(min.toString()) * 60) + seconds;

            StringBuilder sec = new StringBuilder();
            sec.append(direction.charAt(5));
            sec.append(direction.charAt(6));
            seconds = Long.parseLong(sec.toString()) + seconds;
            if (direction.charAt(7) == 'W')
            {
                seconds = seconds * -1;
                return seconds;
            }
            return seconds;
        }
        else
        {
            // This is North or South
            StringBuilder day = new StringBuilder();
            day.append(direction.charAt(0));
            day.append(direction.charAt(1));
            seconds = Long.parseLong(day.toString()) * 3600;

            StringBuilder min = new StringBuilder();
            min.append(direction.charAt(2));
            min.append(direction.charAt(3));
            seconds = (Long.parseLong(min.toString()) * 60) + seconds;

            StringBuilder sec = new StringBuilder();
            sec.append(direction.charAt(4));
            sec.append(direction.charAt(5));
            seconds = Long.parseLong(sec.toString()) + seconds;
            if (direction.charAt(6) == 'S')
            {
                seconds = seconds * -1;
                return seconds;
            }
            return seconds;
        }
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
