package com.buglabs.bug.ircbot.behavior.easygoodbye;

import com.buglabs.bug.ircbot.behavior.easylistening.EasyListener;
import com.buglabs.bug.ircbot.behavior.easylistening.IEasyListenerAction;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

public class Activator extends EasyListener {
	public void registration() {
		onChannel("^goodbye", new IEasyListenerAction() {
			public String invoke(IChannelMessageEvent cme) {
				return "so long";
			}
		});
	}
}