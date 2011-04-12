package com.buglabs.bug.ircbot.impl.jerklib;

import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;
import jerklib.events.ChannelMsgEvent;

public class ChannelMessageEvent implements IChannelMessageEvent {
	private String channel;
	private String hostName;
	private String message;
	private String nick;
	private String userName;
	private String botName;
	
	public ChannelMessageEvent(ChannelMsgEvent cme, String botName) {
		channel = cme.getChannel().getName();
		hostName = cme.getHostName();
		message = cme.getMessage();
		nick = cme.getNick();
		userName = cme.getUserName();
		this.botName = botName;
	}
	@Override
	public String getChannel() {
		return channel;
	}

	@Override
	public String getHostName() {
		return hostName;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getNick() {
		return nick;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getBotName() {
		return botName;
	}

}
