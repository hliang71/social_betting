package com.digitalchocolate.socailbetting.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;




	/**
	 * Model object
	 * @author hliang
	 */
	
	public class LogTarget implements Serializable {
		private static final long serialVersionUID = 9191972741750869972L;
	    
		@Id
	    private String id;
	    private String message;
	    private Date timestamp;
		
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(final String message) {
			this.message = message;
		}
		 
		public Date getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Date timestamp) {
			this.timestamp = timestamp;
		}
		@Override
		public String toString() {
			return "LogItem [id=" + id + ", message=" + message + ", timestamp=" + timestamp + "]";
		}

	}

