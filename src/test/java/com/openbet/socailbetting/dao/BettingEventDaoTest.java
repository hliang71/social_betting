 package com.openbet.socailbetting.dao;

import junit.framework.Assert;


import org.junit.Before;

import org.junit.Test;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.openbet.socailbetting.dao.impl.LogItemDaoImpl;
import com.openbet.socailbetting.model.LogItem;



public class BettingEventDaoTest extends BaseDaoTestCase{ 
    private LogItemDaoImpl dao;
    
    @Before
    public void setUp() throws Exception {
        
    	dao = new LogItemDaoImpl();
        dao.setMongoTemplate(template);      
    }
    
    @Test
    
    public void testInsertUpdateEvent() {
       /** Sample sample = new Sample("TEST", "2");
        repoImpl.save(sample);
        
        int samplesInCollection = template.findAll(Sample.class).size();
        
        assertEquals("Only 1 Sample should exist in collection, but there are "
                + samplesInCollection, 1, samplesInCollection);
                */
    	   
		dao.insertEvent();  
		MongoTemplate template = dao.getMongoTemplate();
		LogItem event = template.findOne(new Query(Criteria.where("name").is("test_event").and("updated").is(false)), LogItem.class);
        Assert.assertNotNull("event should not be null", event);
        dao.updateEvent();
        event = template.findOne(new Query(Criteria.where("name").is("test_event").and("updated").is(false)), LogItem.class);
        Assert.assertNull("after update it should be null", event);
        event = template.findOne(new Query(Criteria.where("name").is("test_event").and("updated").is(true)), LogItem.class);
        Assert.assertNotNull("should be update to true", event);
        
    }

}
