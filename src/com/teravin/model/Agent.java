package com.teravin.model;

import java.io.Serializable;

public class Agent implements Serializable{
	
	public String id;
	public String cardnoagent ;
	public String membertocollect;
	public String available;
	public String outstanding;
	public String limit;
	
	
	public String getId (){
		return id;
	}
	
	public void setId(String _id){
		this.id=_id;
	}
	
	public String getMembertoCollect (){
		return membertocollect;
	}
	
	public void setMembertoCollect(String _membertocollect){
		this.membertocollect=_membertocollect;
	}
	
	public String getCardNoAgent (){
		return cardnoagent;
	}
	
	public void setCardNoAgent(String _cardnoagent){
		this.cardnoagent=_cardnoagent;
	}
	
	public String getAvailable (){
		return available;
	}
	
	public void setAvailable(String _available){
		this.available=_available;
	}
	
	public String getOutstanding (){
		return outstanding;
	}
	
	public void setOutstanding(String _outstanding){
		this.outstanding=_outstanding;
	}
	
	public String getLimit (){
		return limit;
	}
	
	public void setLimit(String _limit){
		this.limit=_limit;
	}
	
	
}
