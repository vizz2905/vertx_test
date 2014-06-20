package com.sungard.marketmap.common.vertx.verticles;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

public class MarketMapClientBusOnly_DJ extends Verticle {

	private Integer numMessages = 0;
	private Integer numSymbols = 0;
	private Integer numSymbolsDelta = 0;
	private Long totalTransferedData = 0L;
	private Integer transferedData = 0;
	
	private Integer numSecondsDump = 1;
	Integer randomNumber = 1;
	private Integer currentID = 0;
	
	private HashMap<String,String> symbList = new HashMap<String,String>();
	
	private boolean isConflated = true;
	private String conflated = (isConflated)?".conflated":"";
		
	public void start() {
		currentID = vertx.currentContext().hashCode();

		// Here we create a list of dummy symbols to register to on the vertx bus
		for(Integer i=0;i<=100;i++){
			registerSymbol("USD-"+i+"-"+vertx.currentContext().hashCode()+"=");	
		}
		
		// We create a timer to send information to the verticle dump info to be consumed for statistics
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				EventBus eb = vertx.eventBus();
				eb.send("data.verticle.info",currentID+","+numMessages+","+(numSymbols-numSymbolsDelta)+","+totalTransferedData+","+transferedData);
				transferedData = 0;
				numMessages = 0;
				numSymbolsDelta = numSymbols;
			}}, 0L, numSecondsDump*1000L);
	}
	
	public void registerSymbol(String symbol)
	{
		if(symbList.get(symbol)!=null)return;
		symbList.put(symbol,"");
		numSymbols++;
		EventBus eb = vertx.eventBus();
		// Here we send the request on the data bus request
		eb.send("data.bus.request"+conflated,"price:"+symbol);
		// We register to receive data for the specific symbol we requested data for
		eb.registerHandler("data.bus.price"+conflated+"."+symbol, new Handler<Message>()
				{
					@Override
					public void handle(Message arg0) {
						numMessages++;
						transferedData += arg0.body().toString().length();
						totalTransferedData += arg0.body().toString().length();
					}
				});
	}
}
