package com.buglabs.bug.ircbot.pub;

/**
 * This interface provides a method of receiving channel messages.
 * @author aturley
 *
 */
public interface IChannelMessageConsumer {
	/**
	 * Invoked when a channel message is received.
	 * @param e
	 * @return
	 */
	public String onChannelMessage(final IChannelMessageEvent e);
}
