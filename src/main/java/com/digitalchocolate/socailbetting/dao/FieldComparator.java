package com.digitalchocolate.socailbetting.dao;

import org.bson.BSONObject;

import java.util.Comparator;

/**
 * <tt>FieldComparator</tt>
 *
 * @author 
 */
public class FieldComparator implements Comparator
  {
  private String field;
  private boolean descending;

  public FieldComparator(String field)
    {
    this( field, true ); // default to descending
    }

  public FieldComparator(String field, boolean descending)
    {
    this.field = field;
    this.descending = descending;
    }

  public int compare(Object o, Object o1)
    {
    BSONObject object1 = (BSONObject)o;
    BSONObject object2 = (BSONObject)o1;
    Double value1 = (Double)object1.get( field );
    Double value2 = (Double)object2.get( field );

    if( descending )
      return descendingCompare( value1, value2 );
    else
      return ascendingCompare( value1, value2 );
    }

  private int descendingCompare(Double value1, Double value2)
    {
    if( value1 > value2 )
      return -1;
    else if( value1 < value2 )
      return 1;
    else
      return 0;
    }

  private int ascendingCompare(Double value1, Double value2)
    {
    if( value1 < value2 )
      return -1;
    else if( value1 > value2 )
      return 1;
    else
      return 0;
    }
  }
