package bearbot;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

import humana_bearcontroller.bear_controller;


public class Activator implements BundleActivator, IChannelMessageConsumer {
	
	String voice = "default";
	bear_controller bear;

	public void start(BundleContext context) throws Exception {
		System.out.println("[bearbot] start");
		context.registerService(IChannelMessageConsumer.class.getName(), this, null);
		
		bear = (bear_controller) context.getService(context.getServiceReference(bear_controller.class.getName()));
		
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("[bearbot] stop");
	}

	@Override
	public String onChannelMessage(IChannelMessageEvent e) {
		if (e.getMessage().startsWith(e.getBotName() + ": say ")){
			String command = e.getMessage().substring((e.getBotName() + ": say ").length());
			System.out.println("Saying "+command);
			Runtime r = Runtime.getRuntime();
			try {
				String[] cmd = {
					"/bin/bash",
					"-c",
					"espeak \""+command+"\" -a 200 -s 100 -v "+voice+" --stdout | sox -t wav - -t alsa hw:1,0 &> /home/root/bearout.log"
				};
				bear.blink('N', -1, 25);
				Process p = r.exec(cmd);
				try {
					p.waitFor();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				bear.clear('N');
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} else if (e.getMessage().startsWith(e.getBotName() + ": voice ")){
			String command = e.getMessage().substring((e.getBotName() + ": voice ").length());
			voice = command;
			return null;
		} else if (e.getMessage().startsWith(e.getBotName() + ": voices")){
			String ret = "";
			File dir = new File("/usr/share/espeak-data/voices/");
			String[] children = dir.list();
			System.out.println("Voices list: "+children.length);
			if (children == null) {
			    // Either dir does not exist or is not a directory
			} else {
			    for (int i=0; i<children.length; i++) {
			        // Get filename of file or directory
			        ret += children[i]+", ";
			    }
			}
			return ret;
		} else if (e.getMessage().startsWith(e.getBotName() + ": stop")){
			Runtime r = Runtime.getRuntime();
			try {
				String[] cmd = {
					"/bin/bash",
					"-c",
					"kill `pgrep espeak`"
				};
				r.exec(cmd);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return null;
		} else if (e.getMessage().startsWith(e.getBotName() + ": blink ")){
			String command = e.getMessage().substring((e.getBotName() + ": blink ").length());
			bear.blink(command.charAt(0));
			return null;
		} else if (e.getMessage().startsWith(e.getBotName() + ": set ")){
			String command = e.getMessage().substring((e.getBotName() + ": set ").length());
			bear.set(command.charAt(0));
			return null;
		} else if (e.getMessage().startsWith(e.getBotName() + ": clear ")){
			String command = e.getMessage().substring((e.getBotName() + ": clear ").length());
			bear.clear(command.charAt(0));
			return null;
		} else if (e.getMessage().startsWith(e.getBotName() + ": targets")){
			return "H (Left Hand), h (Right Hand), F (Left Foot), f (Right Foot), N (Nose), C (Heart), M (Motor)";
		} else if (e.getMessage().startsWith(e.getBotName()+":")) {
			return "say <message>, stop, voice <voice>, voices, blink <target>, set <target>, clear <target>, targets";
		}
		return null;
	}
}