package com.buglabs.bug.ircbot.pub;

/**
 * This event is used to pass information to classes that implement the 
 * IChannelMessageConsumer interface.
 * @author aturley
 *
 */
public interface IChannelMessageEvent {
	public String getChannel();
	public String getHostName();
	public String getMessage();
	public String getNick();
	public String getUserName();
	public String getBotName();
}
