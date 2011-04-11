package com.buglabs.bug.ircbot.behavior.returngreeting;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.registerService(IChannelMessageConsumer.class.getName(), new GreetingsMessageConsumer(), null);		
	}

	public void stop(BundleContext context) throws Exception {
		
	}
}