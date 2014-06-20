package com.sungard.marketmap.common.vertx.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;

public class MarketLinkDataTest implements IMarketMapData {
	
	private String busReq = null;
	private String busBroadcast = "data.bus.broadcast";
	private EventBus eb = null;
	
	private static int sequence = 0;
	private boolean conflated = false;
	
	private Integer[] bateList = {5051,6,22,25,19,12,13,32,11,56,3};
	private HashMap<String, String[]> symbData = new HashMap<String,String[]>();
	private HashMap<Integer,String> requestList = new HashMap<Integer,String>();
	private Integer counter = 0;
	private Integer numLoop = 0;
	private Integer randomMumber = 0;

	@Override
	public void init(Map<String, Object> cfg, EventBus eb) {
		this.eb = eb;
		// Depending on the configuration, we determine if the bus is conflated or not
		conflated = ((cfg.get("conflated")!=null && cfg.get("conflated").equals("false"))?false:true);
		// Generating the bus name to read in the requests
		busReq = cfg.get("data.bus.request.id").toString()+((conflated)?".conflated":"");
		System.out.println("Listening to requests on bus "+busReq);
		numLoop = Integer.parseInt(cfg.get("numLoop").toString());
		
		// Registering the handler for the specified bus above
		eb.registerHandler(busReq,new Handler<Message>()
		{
			public void handle(Message message)
			{
				String[] messageSplit = message.body().toString().split(":");
				if(messageSplit[0].equalsIgnoreCase("price"))
				{
					String symb = messageSplit[1].toUpperCase();
					
					System.out.println("Received: "+symb);
					
					//If we already have the 
					if(symbData.get(symb)!=null)
					{
						fullQuoteResponse(symb);
					}
					else
					{
						requestList.put(counter++, symb);

						Timer t = new Timer();
						t.scheduleAtFixedRate(new TimerTask(){
							@Override
							public void run() {
								//Send random updates - numLoop updates
								for (int i = 0; i < numLoop; i++){
									randomMumber = 0 + (int)(Math.random()*requestList.size());
									OnITPriceUpdate(randomMumber, null);
								}
							}},0,1000);
					}
				}
			}
		});
	}

	@Override
	public void eventUpdate(int type, Map<String, String> data) {
	}

	@Override
	public void getHistorical(Map<String, String> req) {
	}

	@Override
	public void monitorNews(Map<String, String> req) {
	}

	@Override
	public void monitorQuote(Map<String, String> req) {
	}

	@Override
	public void monitorTimeAndSales(Map<String, String> req) {
	}

	@Override
	public void setTypeFields(int type, ArrayList<String> fields) {
	}

	public void OnITBates(String arg0) {
	}

	public void OnITBroadcast(String arg0, boolean arg1) {
	}

	public void OnITChartHistory(int arg0, int arg1, boolean arg2) {
	}

	public void OnITChartUpdate(int arg0, boolean arg1) {
	}

	public void OnITConnect(String arg0) {
		eb.publish(busBroadcast,"Marketlink is ready.");
	}

	public void OnITDisconnect(String arg0) {
	}

	public void OnITError(int arg0, String arg1, long arg2) {
		eb.publish(busBroadcast,"Error: " + arg1);		
	}

	public void OnITExchanges(String arg0) {
	}

	public void OnITNewsHistory(String arg0) {
	}

	public void OnITNewsStory(int arg0, String arg1, String arg2) {
	}

	public void OnITNewsUpdate(int arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5, boolean arg6) {
	}

	public void OnITPriceUpdate(int arg0, int[] arg1) {
		// Verify if we already have requested for the symbol
		String symb = requestList.get(arg0).toString();
		String[] data = null;
		StringBuffer sb = new StringBuffer();
		boolean newData = false;
		
		if((data = symbData.get(symb))==null) data = new String[] {"","","","","","","","","","",""};
		sb.append("["+sequence);
		
		// Here we build the data string to publish for the symbol received in parameter
		for(int i = 0; i < bateList.length; i++)
		{
			sb.append(",");
			
			if(bateList[i]!=5051)
			{
				newData = true;
				double aaa = ( Math.random()*( 10 - 0 + 1 ) ) + 0;
				data[i] = Double.toString(aaa);
				sb.append(data[i]);					
			}
			else
			{
				if(bateList[i]==5051){
					sb.append(symb);
				}				
			}
		}
		sb.append("];");
		
		// Check if there's new data to send - in this test, it's always new data
		if(newData)
		{
			sequence++;
			// Publish the data onto the specific symbol bus
			eb.publish("data.bus.price."+((conflated)?"conflated.":"")+symb, sb.toString());
		}
		symbData.put(symb,data);
	}

	private void fullQuoteResponse(String symb)
	{
		String[] data = symbData.get(symb);
	
		// Verify if we already have data in the cache, if not, do not try to send the quote back
		if(data==null)return;
		
		StringBuffer sb = new StringBuffer();
		sb.append(sequence++);		
		for(int i = 0; i < bateList.length; i++)
		{
			sb.append(",");
			sb.append(data[i]);			
		}

		// Public the data onto the specific sy
		eb.publish("data.bus.price."+((conflated)?"conflated.":"")+symb, sb.toString());
	}
}
