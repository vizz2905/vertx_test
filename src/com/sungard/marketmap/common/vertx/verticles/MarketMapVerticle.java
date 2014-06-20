package com.sungard.marketmap.common.vertx.verticles;

import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.sungard.marketmap.common.vertx.data.IMarketMapData;

public class MarketMapVerticle extends Verticle {
	@SuppressWarnings("unchecked")
	public void start() {
		System.out.println("Verticle start: "+vertx.currentContext().hashCode());
		JsonObject config = container.config();
		EventBus eb = vertx.eventBus();
		
		config = config.getObject("data_verticle");
		String dc = config.getString("data.class");
		System.out.println("Class: "+dc);
		Class clazz = null;
		IMarketMapData dataClass = null;
		try
		{
			clazz = Class.forName(dc);
			dataClass = (IMarketMapData)clazz.newInstance();
			dataClass.init(config.toMap(),eb);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
