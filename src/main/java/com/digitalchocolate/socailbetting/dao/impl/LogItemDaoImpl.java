package com.digitalchocolate.socailbetting.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.digitalchocolate.socailbetting.dao.AbstractBaseDao;
import com.digitalchocolate.socailbetting.dao.BaseDao;
import com.digitalchocolate.socailbetting.model.LogItem;
import com.digitalchocolate.socailbetting.model.LogTarget;

/**
*
* @author hliang
*/

@Repository("logItemDao")
public class LogItemDaoImpl extends AbstractBaseDao implements BaseDao<LogTarget> {
   private static final Logger log = Logger.getLogger(LogItemDaoImpl.class);
   public LogTarget selectByPk(String id) {
	   log.debug("id is"+id);
	   Query query = new Query(Criteria.where("a").is(id));
       // Execute the query and find one matching entry
       LogTarget person = mongoTemplate.findOne(query, LogTarget.class, "foo");
       return new LogTarget();
      // return (LogItem)mongoTemplate.findById(id, LogItem.class);
   }

   public Long updateEvent() {
	   
	   log.debug("update event");
	  // LogEvent event = new LogEvent();
	   long count = 0;
	   try
	   {
		   LogItem event = mongoTemplate.findOne(new Query(Criteria.where("name").is("test_event").and("updated").is(false)), LogItem.class);
		   /**int size = event.getItems().size();
		   log.debug("#########SIZE is " + size);*/
		   List<LogTarget> targets = event.getItems();
		   LogTarget[]  items = new LogTarget[20];
		  
		   for(int i =0; i < 20; i++)
		   {
			   LogTarget item = new LogTarget();
			   item.setId(String.valueOf(i));
			   item.setMessage("test"+i);
			   item.setTimestamp(new Date());
			   items[i] = item;
			   targets.add(item);
		   }
		   LogTarget item = new LogTarget();
		   
		   //mongoTemplate.save(event);
		   mongoTemplate.updateMulti(
				   new Query(Criteria.where("name").is("test_event").and("updated").is(false)),
					Update.update("updated", true).pushAll("items", items), LogItem.class);
	   
		 // count = mongoTemplate.count(new Query(Criteria.where("name").is("test_event").and("updated").is(true)), LogItem.class);
		  log.debug("count is " + count);
	   }
	   catch(Exception e)
	   {
		   log.error("Exception :", e);
	   }
	   return count;
   }
   public Long insertEvent() {
	   
	   log.debug("insert event");
	   
	   long count = 0;
	   try
	   {
		  
		   LogItem event = new LogItem();
		   Date now = new Date();
		   //event.setId(String.valueOf(now.getTime()));
		   List<LogTarget> items = event.getItems();
		   event.setName("test_event");
		   for(int i =0; i < 20; i++)
		   {
			   LogTarget item = new LogTarget();
			   //item.setId(String.valueOf(i));
			   item.setMessage("test"+i);
			   item.setTimestamp(new Date());
			   items.add(item);
		   }
		   
		   
		   
		   mongoTemplate.insert(event);
	   
		   //count = mongoTemplate.count(new Query(Criteria.where("name").is("test_event")), "logEvent");
		  log.debug("count is " + count);
	   }
	   catch(Exception e)
	   {
		   log.error("Exception :", e);
	   }
	   return count;
   }
   public Long insert(LogTarget log) {
       mongoTemplate.insert(log);
       return Long.valueOf(log.getId());
   }
}