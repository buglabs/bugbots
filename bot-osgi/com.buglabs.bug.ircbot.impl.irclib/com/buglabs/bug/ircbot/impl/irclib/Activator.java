package com.buglabs.bug.ircbot.impl.irclib;

import java.io.IOException;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;

import org.schwering.irc.lib.IRCConnection;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	private IRCConnection manager;
	private static final String botName = "bot-bundle";
	private static final String serverName = "bugcamp.net";
	private static final String channelName = "#buglabs";
	// private static final String serverName = "irc.freenode.net";
	// private static final String channelName = "#botwar";
	private ServiceTracker channelMessageConsumerTracker;
	
	public void start(BundleContext context) throws Exception {
		System.out.println("starting irclib based irc bot");
		manager = new IRCConnection(
                serverName, 
                6667, 
                6669, 
                null, 
                botName, 
                botName + "1", 
                botName + "@buglabs" 
              ); 
		channelMessageConsumerTracker = new ServiceTracker(context, IChannelMessageConsumer.class.getName(), null);
		channelMessageConsumerTracker.open();
		manager.addIRCEventListener(new IRCEventListener(channelMessageConsumerTracker, botName, manager)); 
		manager.setDaemon(true);
		manager.setColors(false); 
		manager.setPong(true); 

		try {
			System.out.println("calling connect");
			manager.connect(); // Try to connect!!! Don't forget this!!!
			System.out.println("called connect");
		} catch (IOException ioexc) {
			ioexc.printStackTrace(); 
		}
	}

	public void stop(BundleContext context) throws Exception {
		if (manager != null) {
			manager.close();
			manager = null;
		}
	}
}