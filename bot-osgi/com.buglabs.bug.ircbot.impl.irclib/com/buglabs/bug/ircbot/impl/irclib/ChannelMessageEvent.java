package com.buglabs.bug.ircbot.impl.irclib;

import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;
import org.schwering.irc.lib.IRCUser;

public class ChannelMessageEvent implements IChannelMessageEvent {
	private String channel;
	private String hostName;
	private String message;
	private String nick;
	private String userName;
	private String botName;
	
	public ChannelMessageEvent(String channel, IRCUser user, String message, String botName) {
		this.channel = channel;
		hostName = user.getServername();
		this.message = message;
		nick = user.getNick();
		userName = user.getUsername();
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
