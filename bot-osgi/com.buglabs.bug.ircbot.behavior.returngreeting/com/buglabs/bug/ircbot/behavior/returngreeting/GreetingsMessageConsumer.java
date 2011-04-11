package com.buglabs.bug.ircbot.behavior.returngreeting;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

public class GreetingsMessageConsumer implements IChannelMessageConsumer {

	@Override
	public String onChannelMessage(IChannelMessageEvent e) {
		String message = e.getMessage();
		String botName = e.getBotName();
		System.out.println("Saw message: " + message);
		String outgoingMessage = "";
		if (message.startsWith(botName + ":") || message.startsWith(botName + ",")) {
			outgoingMessage = "" + e.getNick() + ": hello";
			System.out.println("I would say: '" + outgoingMessage + "'");
		}
		return outgoingMessage;
	}
}
