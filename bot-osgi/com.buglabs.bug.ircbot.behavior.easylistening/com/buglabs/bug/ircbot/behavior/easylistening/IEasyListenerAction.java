package com.buglabs.bug.ircbot.behavior.easylistening;

import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

public interface IEasyListenerAction {
	public String invoke(IChannelMessageEvent cme);
}
