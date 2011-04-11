package com.buglabs.bug.ircbot.behavior.easylistening;

import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

public class EasyListenerConsumer implements IChannelMessageConsumer {
	private class Entry {
		private Pattern pattern;
		private IEasyListenerAction action;
		public Entry(Pattern pattern, IEasyListenerAction action) {
			this.pattern = pattern;
			this.action = action;
		}
		public Pattern getPattern() {
			return pattern;
		}
		public IEasyListenerAction getAction() {
			return action;
		}
	}
	
	private LinkedList<Entry> patternActions;
	
	@Override
	public String onChannelMessage(IChannelMessageEvent e) {
		StringBuilder builder = new StringBuilder();
		String message = e.getMessage();
		Boolean foundMatch = false;
		System.out.println("EasyListener got message " + e.getMessage());
		for (Entry entry : patternActions) {
			Matcher m = entry.getPattern().matcher(message);
			if (m.matches()) {
				System.out.println("Found match");
				String s = entry.getAction().invoke(e);
				if (s != null) {
					builder.append(s);
					foundMatch = true;
				}
			} else {
				System.out.println("No match");
			}
		}
		if (foundMatch) {
			return builder.toString();
		}
		return null;
	}

	public void add(String regex, IEasyListenerAction action) {
		Pattern pattern = Pattern.compile(regex);
		patternActions.add(new Entry(pattern, action));
	}
}
