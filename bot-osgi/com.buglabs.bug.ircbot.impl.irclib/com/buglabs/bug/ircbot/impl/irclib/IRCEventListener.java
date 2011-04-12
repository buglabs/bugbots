package com.buglabs.bug.ircbot.impl.irclib;

import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;
import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;

import org.schwering.irc.lib.IRCEventAdapter;
import org.schwering.irc.lib.IRCUser;
import org.schwering.irc.lib.IRCConnection;

import org.osgi.util.tracker.ServiceTracker;

public class IRCEventListener extends IRCEventAdapter {
	private ServiceTracker channelMessageConsumerTracker;
	private String botName; 
	IRCConnection connection;
	public IRCEventListener(ServiceTracker tracker, String botName, IRCConnection connection) {
		super();
		channelMessageConsumerTracker = tracker;
		this.botName = botName;
		this.connection = connection;
	}

	@Override
	public void onRegistered() {
		System.out.println("registered!");
		System.out.println("calling doJoin");
		connection.doJoin("#botwar");
		System.out.println("called doJoin");
	}
	@Override
	public void onPrivmsg(java.lang.String target, IRCUser user, java.lang.String msg) {
		System.out.println("received channel message");
		Object channelMessageConsumers[] = channelMessageConsumerTracker.getServices();
		
		if (channelMessageConsumers != null) {
			IChannelMessageEvent cme = new ChannelMessageEvent(target, user, msg, botName);
			for (Object cmc : channelMessageConsumers) {
				String message = ((IChannelMessageConsumer) cmc).onChannelMessage(cme);
				if (message != null) {
					connection.doPrivmsg(target, message);
				}
			}
		} else {
			System.out.println("Sigh ... no consumers");
		}
	}
}
