import java.util.Scanner;

/**
 * @author Andrew Knittle Data Type that will hold everything needed for a GIS
 *         Record. This is specifically used for the Hash Tables.
 */
public class RecordGIS
{

    /**
     * Feature_Name
     */
    String FEATURE_NAME;

    /**
     * State_Alpha
     */
    String STATE_ALPHA;

    // -----------------------------------------------------------------------

    /**
     * long representation latitude
     */
    long   latitude;
    /**
     * long representation longitude
     */
    long   longitude;

    // -----------------------------------------------------------------------

    /**
     * combination of name and state
     */
    String fullname;

    /**
     * location of record in the database file
     */
    long   offset;

    /**
     * The ENTIRE string of the record is stored in here. Verbatim from the
     * database file
     */
    String completeLine;


    /**
     * Initializes everything related to the fields to empty strings, and offset
     * of the data file to 0. Acts as a default record, empty, record. A blank
     * record
     */
    public RecordGIS()
    {
        FEATURE_NAME = "";
        STATE_ALPHA = "";
        fullname = FEATURE_NAME + ":" + STATE_ALPHA;

        latitude = 0;
        longitude = 0;

        offset = 0;
        completeLine = "";
    }


    // ----------------------------------------------------------
    /**
     * Constructor for the name, state, and file offset for the record.
     * Partially built Record. mainly used for ease of testing, and not for the
     * actual assignment.
     *
     * @param name
     * @param state
     * @param pos
     */
    public RecordGIS(String name, String state, long pos)
    {
        FEATURE_NAME = name;
        STATE_ALPHA = state;
        fullname = FEATURE_NAME + ":" + STATE_ALPHA;

        latitude = 0;
        longitude = 0;

        offset = pos;
        completeLine = "";
    }


    // ----------------------------------------------------------
    /**
     * Create a new RecordGIS object, with everything important filled in at the
     * start. Not much work will be needed from this class if done this way.
     *
     * @param name
     * @param state
     * @param pos
     * @param y
     * @param x
     * @param full
     */
    public RecordGIS(String name, String state, long pos, long y, long x, String full)
    {
        FEATURE_NAME = name;
        STATE_ALPHA = state;
        fullname = FEATURE_NAME + ":" + STATE_ALPHA;

        latitude = y;
        longitude = x;

        offset = pos;
        completeLine = full;
    }


    // ----------------------------------------------------------
    /**
     * Passed in just the entire record line from the database file and it's
     * position in the file. Easier to initialize everything, but this could be
     * doing a lot of extra, unneeded work. However, it makes coding a little
     * more easier.
     *
     * @param line
     * @param pos
     */
    public RecordGIS(String line, long pos)
    {
        offset = pos;
        completeLine = line;
        fillInBlanks(line);
        fullname = FEATURE_NAME + ":" + STATE_ALPHA;
    }


    // ----------------------------------------------------------
    /**
     * Initializes every field, but the the offset and completeLine.
     *
     * @param line
     */
    public void fillInBlanks(String line)
    {
        Scanner splitter = new Scanner(line);
        splitter.useDelimiter("\\|");
        // Get all the related strings needed for the record.splitter.next();
        FEATURE_NAME = splitter.next();
        splitter.next();
        STATE_ALPHA = splitter.next();
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
        splitter.close();
        latitude = coordinateLong(PRIMARY_LAT_DMS);
        longitude = coordinateLong(PRIM_LONG_DMS);

    }


    // ----------------------------------------------------------
    /**
     * Takes the string version of a coordinate and gives back its value as a
     * long in seconds format.
     *
     * @param direction
     * @return long
     */
    long coordinateLong(String direction)
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


    /**
     * Sets the fullname of the record with FEATURE_NAME and STATE_ALPHA
     */
    public void setFullName()
    {
        fullname = FEATURE_NAME + ":" + STATE_ALPHA;
        return;
    }


    /**
     * Setter method for the FEATURE_NAME of the record.
     *
     * @param name
     */
    public void setName(String name)
    {
        FEATURE_NAME = name;
    }


    /**
     * Setter method for the STATE_ALPHA of the record.
     *
     * @param state
     */
    public void setState(String state)
    {
        STATE_ALPHA = state;
    }


    /**
     * Simple setter method for the offset of the file corresponding to the
     * record
     *
     * @param location
     */
    public void setOffset(long location)
    {
        offset = location;
    }


    /**
     * Returns Sate
     *
     * @return STATE_ALPHA
     */
    public String getState()
    {
        return STATE_ALPHA;
    }


    /**
     * Returns Name
     *
     * @return FEATURE_NAME
     */
    public String getName()
    {
        return FEATURE_NAME;
    }


    /**
     * Returns offset
     *
     * @return offset
     */
    public long getOffset()
    {
        return offset;
    }


    /**
     * Returns the combo of the feature name and the state abbreviation
     *
     * @return fullname
     */
    public String getFullName()
    {
        return fullname;
    }


    // ----------------------------------------------------------
    /**
     * returns completeLine
     *
     * @return String
     */
    public String getCompleteLine()
    {
        return completeLine;
    }
}
