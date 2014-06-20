package com.sungard.marketmap.common.vertx.data;

import java.util.ArrayList;
import java.util.Map;

import org.vertx.java.core.eventbus.EventBus;

public interface IMarketMapData {

	public void init(Map<String,Object> cfg, EventBus eb);
	public void getHistorical(Map<String,String> req);
	public void monitorQuote(Map<String,String> req);
	public void monitorTimeAndSales(Map<String,String> req);
	public void monitorNews(Map<String,String> req);
	public void setTypeFields(int type, ArrayList<String> fields);
	public void eventUpdate(int type, Map<String,String> data);
}
