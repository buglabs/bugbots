package com.buglabs.bug.ircbot.pub;

import java.util.List;

public interface IBotControl {
	public void connect(String server);
	public void disconnect(String server);
	public void join(String server, String channel);
	public void leave(String server, String channel);
	public List<String> getConnectedServers();
	public List<String> getJoinedChannels(String server);
	public void say(String server, String channel, String msg);
}
