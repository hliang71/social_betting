package com.digitalchocolate.socailbetting.dao;
import org.bson.types.ObjectId;

import com.digitalchocolate.socailbetting.model.rule.BaseRule;
import com.digitalchocolate.socailbetting.utils.BetTypeEnum;
import com.digitalchocolate.socailbetting.utils.RuleNamesEnum;

/**
 * Base DAO for Hibernate Entities
 * @author hliang
 */

public interface BaseDao<T> {
	 T selectByPk(String id);
	 Long insert(T entity);
	 public Long updateEvent();
	 public Long insertEvent();
	 
	 
}
