package com.aug3.sys;


/**
 * This exception indicates that after the indended membership update, there
 * won't be any member left in the group.  This is not allowed and the update
 * won't be committed.  Having an empty group causes various problems in the
 * product so we want to prevent that to happen.  If user really don't want
 * any members in the group, they can delete it from the database.
 * 
 * @author xial
 */
public class ServiceLocatorException extends CommonException
{
    //==========================================================================
    // constructors
    //==========================================================================
    /**
     * the default constructor
     */
    public ServiceLocatorException()
    {
        super();
    }

    /**
     * constructor with the reason
     */
    public ServiceLocatorException(String why)
    {
        super(why);
    }

    /**
     * constructor with reason and linked exception
     */
    public ServiceLocatorException(String why, Throwable t)
    {
        super(why, t);
    }
}