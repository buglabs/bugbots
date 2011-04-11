package com.buglabs.bug.ircbot.impl;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import jerklib.ConnectionManager;
import jerklib.ProfileImpl;
import jerklib.Session;
import jerklib.events.ChannelMsgEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.events.listeners.IRCEventListener;

public class Activator implements BundleActivator, IRCEventListener {

	private ConnectionManager manager;
	private static final String botName = "bot-bundle";
	// private static final String serverName = "bugcamp.net";
	private static final String serverName = "irc.freenode.net";
	private static final String channelName = "#botwar";
	private ServiceTracker channelMessageConsumerTracker;
	public void start(BundleContext context) throws Exception {
		manager = new ConnectionManager(new ProfileImpl("OSGi Robot", botName, botName + "1", botName + "2"));
		Session session = manager.requestConnection(serverName);
		session.addIRCEventListener(this);
		channelMessageConsumerTracker = new ServiceTracker(context, IChannelMessageConsumer.class.getName(), null);
		channelMessageConsumerTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		if (manager != null) {
			for(Session s : manager.getSessions()) {
				for (String cn : s.getChannelNames()) {
					s.close(cn);
				}
			}
			manager.quit();
			manager = null;
		}
	}
	
	// APIs need editors.
	public void recieveEvent(IRCEvent e) {
		if (e.getType() == Type.CONNECT_COMPLETE) {
			System.out.println("connected, trying to join channel");
			e.getSession().joinChannel(channelName);
		}
		if (e.getType() == Type.CHANNEL_MESSAGE) {
			System.out.println("received channel message");
			Object channelMessageConsumers[] = channelMessageConsumerTracker.getServices();
			
			if (channelMessageConsumers != null) {
				ChannelMsgEvent me = (ChannelMsgEvent) e;
				IChannelMessageEvent cme = new ChannelMessageEvent(me, botName);
				for (Object cmc : channelMessageConsumers) {
					String message = ((IChannelMessageConsumer) cmc).onChannelMessage(cme);
					if (message != null) {
						me.getChannel().say(message);
					}
				}
			} else {
				System.out.println("Sigh ... no consumers");
			}
		}
		if (e.getType() == Type.ERROR) {
			System.out.println("Got an error");
			System.out.println(e.getRawEventData());
		}
		if (e.getType() == Type.EXCEPTION) {
			System.out.println("Got an exception");
			System.out.println(e.getRawEventData());
		}
	}
}