package com.digitalchocolate.socailbetting.dao;
import com.mongodb.*;


import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <tt>FootballExample</tt>
 *
 * @author 
 */
public class FootballExample
  {
	private static MongoTemplate template;
  public static void main(String[] args)
    throws Exception
    {
    
    DB db = template.getDb();
    DBCollection footballCollection = db.getCollection("ProFootball2007Season");
    long rowCount = footballCollection.count();

    if( rowCount == 0 )
      {
      System.out.println("Please run LoadFootballData before running this example");
      return;
      }

    System.out.println("ProFootball2007Season row count = " + rowCount);

    DBObject exampleDocument = footballCollection.findOne();
    System.out.println("document example\n" + exampleDocument);

    boolean sortDescending = true;
    MongoAggregator aggregator = new MongoAggregator(footballCollection);

    System.out.println("NFL Rushing Stats\n");

    DBObject rbCondition = new BasicDBObject("position", "rb");
    DBObject columnFields = new BasicDBObject("last name", true);
    columnFields.put("first name", true);
    List rushLeaderResults = aggregator.sum(columnFields, "rushYD", rbCondition, sortDescending, 10);

    System.out.println("Top 10 Rushing leaders for 2007" );
    System.out.println("name\tyards");
    System.out.println("---------------------");

    for (int i = 0; i < rushLeaderResults.size(); i++)
      {
      DBObject document = (DBObject)rushLeaderResults.get(i);
      String output = document.get("first name").toString() + "\t" + document.get("last name").toString() + "\t" + document.get("sum").toString();
      System.out.println(output);
      }

    System.out.println("");

    List teamRushResults = aggregator.sum("team", "rushYD", rbCondition, sortDescending, 10);
    System.out.println("Top 10 Rushing teams for 2007" );
    System.out.println("team\tyards");
    System.out.println("---------------------");

    for (int i = 0; i < teamRushResults.size(); i++)
      {
      DBObject rushingLeaderDocument = (DBObject)teamRushResults.get(i);
      String output = rushingLeaderDocument.get("team").toString() + "\t" + rushingLeaderDocument.get("sum").toString();
      System.out.println(output);
      }

    System.out.println("");

    List teamYdsPerCarryResults = aggregator.divideTwoSums( "team", "rushYD", "rush", rbCondition, "yardsPerCarry", sortDescending, 10 );
    System.out.println("Top 10 Teams by Yard per Carry for 2007" );
    System.out.println("team\tyards/carry");
    System.out.println("---------------------");

    for (int i = 0; i < teamYdsPerCarryResults.size(); i++)
      {
      DBObject rushingLeaderDocument = (DBObject)teamYdsPerCarryResults.get(i);
      String output = rushingLeaderDocument.get("team").toString() + "\t" + rushingLeaderDocument.get("yardsPerCarry").toString();
      System.out.println(output);
      }
    }
  }
