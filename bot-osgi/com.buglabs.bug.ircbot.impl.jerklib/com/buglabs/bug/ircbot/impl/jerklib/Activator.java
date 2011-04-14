package com.buglabs.bug.ircbot.impl.jerklib;

import java.util.LinkedList;
import java.util.List;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;
import com.buglabs.bug.ircbot.pub.IBotControl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.Channel;
import jerklib.events.MessageEvent;
import jerklib.events.IRCEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.listeners.IRCEventListener;

public class Activator implements BundleActivator, IRCEventListener, IBotControl {

	private ConnectionManager manager;
	private static final String botName = "bot-bundle";
	private static final String serverName = "bugcamp.net";
	// private static final String serverName = "irc.freenode.net";
	private static final String channelName = "#buglabs";
	private ServiceTracker channelMessageConsumerTracker;
	public void start(BundleContext context) throws Exception {
		manager = new ConnectionManager(new Profile("OSGi Robot", botName, botName + "1", botName + "2"));
		final Activator me = this;
		new Thread() {
			public void run() {
				me.join(serverName, channelName);
			}
		}.start();
		channelMessageConsumerTracker = new ServiceTracker(context, IChannelMessageConsumer.class.getName(), null);
		channelMessageConsumerTracker.open();
		context.registerService(IBotControl.class.getName(), this, null);
	}

	public void stop(BundleContext context) throws Exception {
		if (manager != null) {
			for(Session s : manager.getSessions()) {
				for (Channel cn : s.getChannels()) {
					s.close(cn.getName());
				}
			}
			manager.quit();
			manager = null;
		}
	}
	
	public void receiveEvent(IRCEvent e) {
		if (e.getType() == Type.CHANNEL_MESSAGE) {
			System.out.println("received channel message");
			Object channelMessageConsumers[] = channelMessageConsumerTracker.getServices();
			
			if (channelMessageConsumers != null) {
				MessageEvent me = (MessageEvent) e;
				IChannelMessageEvent cme = new ChannelMessageEvent(me, botName);
				for (Object cmc : channelMessageConsumers) {
					String message = ((IChannelMessageConsumer) cmc).onChannelMessage(cme);
					if (message != null) {
						System.out.println("Saying:" + message);
						me.getChannel().say(message);
					} else {
						System.out.println("Did not send null message.");
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
	
	public void connect(String server) {
		// TODO: do something here
	}
	public void disconnect(String server) {
		// TODO: do something here
	}
	public void join(final String server, final String channel) {
		Session session = null;
		for (Session s : manager.getSessions()) {
			if (s.getConnectedHostName().compareTo(server) == 0) {
				session = s;
				for (Channel c : s.getChannels()) {
					if (c.getName().compareTo(channel) == 0) {
						return;
					}
				}
			}
		}
		
		if (session == null) {
			session = manager.requestConnection(server);
			session.addIRCEventListener(new IRCEventListener() {
				boolean didConnect = false;
				public void receiveEvent(IRCEvent e) {
					if (e.getType() == Type.CONNECT_COMPLETE) {
						if (!didConnect) {
							didConnect = true;
							System.out.println("connected, trying to join channel '" + channel + "'");
							e.getSession().join(channel);
						}
					}
				}
			});
			session.addIRCEventListener(this);
		} else {
			session.join(channel);
		}
	}
	
	public void leave(String server, String channel) {
	}
	public List<String> getConnectedServers() {
		LinkedList<String> servers = new LinkedList<String>();
		for (Session s : manager.getSessions()) {
			servers.add(s.getConnectedHostName());
		}
		return servers;
	}
	public List<String> getJoinedChannels(String server) {
		LinkedList<String> channels = new LinkedList<String>();
		for (Session s : manager.getSessions()) {
			if (s.getConnectedHostName().compareTo(server) == 0) {
				for (Channel c : s.getChannels()) {
					channels.add(c.getName());
				}
			}
		}
		return channels;
	}
	public void say(String server, String channel, String message) {
		for (Session s : manager.getSessions()) {
			if (s.getConnectedHostName().compareTo(server) == 0) {
				for (Channel c : s.getChannels()) {
					if (c.getName().compareTo(channel) == 0) {
						c.say(message);
					}
				}
			}
		}
	}
}