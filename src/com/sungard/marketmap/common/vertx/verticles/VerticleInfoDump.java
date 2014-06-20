package com.sungard.marketmap.common.vertx.verticles;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class VerticleInfoDump extends Verticle {
	private Long numMessageTotal = 0L;
	private Long numMessages = 0L;
	private Long countMessages = 0L;
	private Integer numVerticles = 0;
	private Integer numSymbols = 0;
	private Long totalTransferedData = 0L;
	private Long transferedData = 0L;
	
	private Integer numSecondsDump = 5;
	
	private HashMap<String,String> verticles = new HashMap<String,String>();

	private String infoLog = "";

	SimpleDateFormat sdf = null;
	Calendar cal = null;
	Integer count = 0;
	
	public void start() {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask(){
			public void run() {
				cal = Calendar.getInstance();
				sdf = new SimpleDateFormat("HH:mm:ss");
				
				count++;

				if (countMessages == 0) {
					countMessages = 1L;
				}

				System.out.format("%s | Clients: %d | Msg: %d | Symb: %d | Avg Msg/KB/Sec: %.3f\n",
						sdf.format(cal.getTime()),
						numVerticles,
						numMessages,
						numSymbols,
						((transferedData/countMessages)/1024.0)
					);	

				infoLog += String.format("%s | Clients: %d | Msg: %d | Symb: %d | Avg Msg/KB/Sec: %.3f\n",
						sdf.format(cal.getTime()),
						numVerticles,
						numMessages,
						numSymbols,
						((transferedData/countMessages)/1024.0)
					) + System.getProperty("line.separator");	

				if (count == 60 / numSecondsDump) {
					count = 0;

					System.out.format("** %s | Clients: %d | Msg: %d | Symb: %d **\n",
						sdf.format(cal.getTime()),
						numVerticles,
						numMessageTotal,
						numSymbols
					);

					writeLog(infoLog + String.format("** %s | Clients: %d | Msg: %d | Symb: %d **\n",
						sdf.format(cal.getTime()),
						numVerticles,
						numMessageTotal,
						numSymbols
					) + System.getProperty("line.separator") );

					infoLog = "";
					totalTransferedData = 0L;
					numMessages = 0L;
					countMessages = 0L;
					numMessageTotal = 0L;
				}
				
				//System.out.println("ID: "+vertx.currentContext().hashCode()+"Messages: "+numMessageTotal+" - Symbols: "+numSymbols+" - Total KB: "+(totalTransferedData/1024.0)+" - KB/sec: "+(transferedData/1024.0/numSecondsDump));
				transferedData = 0L;
				numMessages = 0L;
				countMessages = 0L;
			}}, 0L, numSecondsDump*1000L);
		
		EventBus eb = vertx.eventBus();
		eb.registerHandler("data.verticle.info", new Handler<Message>()
		{
			@Override
			public void handle(Message arg0) {
				//System.out.println("message: "+arg0.body().toString());
				String[] s = arg0.body().toString().split(",");
				if(verticles.get(s[0])==null)
				{
					numVerticles++;
					verticles.put(s[0],"");
				}
				numMessageTotal+=Long.parseLong(s[1]);
				numMessages+=Long.parseLong(s[1]);
				countMessages+=Long.parseLong(s[1]);
				numSymbols+=Integer.parseInt(s[2]);
				//totalTransferedData+=Long.parseLong(s[3]);
				transferedData+=Long.parseLong(s[4]);
				totalTransferedData += transferedData;
				//System.out.println(totalTransferedData + " - " + Long.parseLong(s[4]));
			}
		});
	}

	public void writeLog(String text)
  	{
  		PrintWriter pw = null;
 
		try {
		     File file = new File("logs.txt");
		     FileWriter fw = new FileWriter(file, true);
		     pw = new PrintWriter(fw);
		     pw.println(text);
		  } catch (IOException e) {
		     e.printStackTrace();
		  } finally {
		     if (pw != null) {
		        pw.close();
		     }
		  }
  	}
}
