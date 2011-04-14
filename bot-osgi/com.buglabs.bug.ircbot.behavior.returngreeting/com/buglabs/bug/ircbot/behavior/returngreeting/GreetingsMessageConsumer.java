package com.buglabs.bug.ircbot.behavior.returngreeting;

import java.util.regex.Pattern;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

public class GreetingsMessageConsumer implements IChannelMessageConsumer {
	
	@Override
	public String onChannelMessage(IChannelMessageEvent e) {
		String message = e.getMessage();
		String botName = e.getBotName();
		System.out.println("Saw message: " + message);
		String outgoingMessage = null;
		if (message.compareTo(botName + ": hello") == 0) {
			outgoingMessage = "" + e.getNick() + ": hello";
			System.out.println("I would say: '" + outgoingMessage + "'");
		}
		if (Pattern.compile("(hi|hello|hola) " + e.getBotName()).matcher(e.getMessage().toLowerCase()).matches()) {
			outgoingMessage = "" + e.getNick() + ": hello";
		}
		return outgoingMessage;
	}
}
