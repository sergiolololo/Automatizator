package com.telefonica.nomodulos.beans;

import java.sql.Timestamp;
import java.util.Date;

public class DisneyCustomer_DTO_IN {

	String subjectId;
	Date lastUpdated;
	public String getSubjectId() {
		return subjectId;
	}
	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
