package com.buglabs.bug.ircbot.behavior.easylistening;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;

public abstract class EasyListener implements BundleActivator {
	private EasyListenerConsumer consumer;
	public void start(BundleContext context) throws Exception {
		if (consumer == null) {
			consumer = new EasyListenerConsumer();
		}
		context.registerService(IChannelMessageConsumer.class.getName(), consumer, null);
		registration();
	}

	public void stop(BundleContext context) throws Exception {
		consumer = null;
	}
	
	public void on(String regex, IEasyListenerAction action) {
		if (consumer != null) {
			consumer.add(regex, action);
		}
	}
	
	public abstract void registration();
}