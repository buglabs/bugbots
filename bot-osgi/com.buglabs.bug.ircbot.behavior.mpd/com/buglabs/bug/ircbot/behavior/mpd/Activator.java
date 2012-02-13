package com.buglabs.bug.ircbot.behavior.mpd;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDFile;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.objects.MPDSong;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.exception.MPDDatabaseException;
import org.bff.javampd.exception.MPDPlayerException;
import org.bff.javampd.exception.MPDPlaylistException;

import org.osgi.service.log.LogService;

import com.buglabs.bug.ircbot.pub.IBotControl;
import com.buglabs.bug.ircbot.pub.IChannelMessageConsumer;
import com.buglabs.bug.ircbot.pub.IChannelMessageEvent;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.buglabs.util.LogServiceUtil;

/**
 * BundleActivator for Com_buglabs_bug_ircbot_behavior_fun.  The OSGi entry point to the application.
 *
 */
public class Activator implements BundleActivator {
	private MPD mpd;
	private LogService logger = null;
	private IBotControl irc = null;

	public MPD getMPD() throws UnknownHostException, MPDConnectionException {
		if (mpd != null && mpd.isConnected()) {
			logger.log(LogService.LOG_INFO, "connected to mpd");
			return mpd;
		}
		mpd = new MPD("mpd.local", 6600);
		logger.log(LogService.LOG_INFO, "connected to mpd");
		return mpd;
	}
	
	private boolean quickPlay(String filename, long startpos, long duration_millis){
		try {
			mpd = getMPD();
			MPDPlayer player = mpd.getMPDPlayer();
			MPDSong song = player.getCurrentSong();
			long pos = player.getElapsedTime();
			Collection<MPDSong> songs = mpd.getMPDDatabase().searchFileName(filename);
			Iterator it = songs.iterator();
			if (it.hasNext()){
				MPDSong gong = (MPDSong)it.next();
				mpd.getMPDPlaylist().addSong(gong);
				List<MPDSong> playlist = mpd.getMPDPlaylist().getSongList(); 
				MPDSong realgong = playlist.get(playlist.size()-1);
				mpd.getMPDPlayer().playId(realgong);
				mpd.getMPDPlayer().seek(startpos);
				Thread.sleep(duration_millis);
				mpd.getMPDPlayer().playId(song);
				mpd.getMPDPlayer().seek(pos);
				mpd.getMPDPlaylist().removeSong(realgong);
			} else {
				logger.log(LogService.LOG_INFO, "Couldn't find gongfile :(");
				return false;
			}
		} catch (MPDPlayerException e1) {
			e1.printStackTrace();
			return false;
		} catch (MPDConnectionException e2) {
			e2.printStackTrace();
			return false;
		} catch (MPDDatabaseException e3) {
			e3.printStackTrace();
			return false;
		} catch (InterruptedException e4) {
			
		} catch (UnknownHostException e5) {
			e5.printStackTrace();
			return false;
		} catch (MPDPlaylistException e6) {
			e6.printStackTrace();
			return false;
		} 
		return true;
	}
	
	private boolean updateMPD(){
		return updateMPD(true);
	}
	
	private boolean updateMPD(boolean announce){
		try {
			long previous = getMPD().getMPDDatabase().getLastUpdateTime();
			mpd.getMPDAdmin().updateDatabase();
			while (previous == mpd.getMPDDatabase().getLastUpdateTime()){
				Thread.sleep(1000);
			}
			logger.log(LogService.LOG_INFO, "Finished updating!");
			if (announce)
				irc.say("bugcamp.net", "#buglabs", "Finished updating!");
			return true;
		} catch (Exception e) {
			logger.log(LogService.LOG_INFO, "error while updating");
			return false;
		}
	}
	
	private void youtubeAdd(String url){
		final String data = url;
		new Thread() {
			public void run() {
				try {
					logger.log(LogService.LOG_INFO, "downloading youtube link "+data);
					String result = syscall("ssh buglabs@mpd.local \"cd /media/musicsrv/music/youtube && youtube-dl -t --no-progress --extract-audio --audio-format mp3 "+data+"\"");
					logger.log(LogService.LOG_INFO, "Result of youtube extraction:");
					System.out.println(result);
					String[] lines = result.split("\n");
					int idx = result.lastIndexOf("Destination: "); 
					if (idx == -1){
						logger.log(LogService.LOG_INFO, "Error retreiving video: "+lines[lines.length-1]);
						irc.say("bugcamp.net", "#buglabs", "Error retreiving video: "+lines[lines.length-1]);
						return;
					}
					String filename = result.substring(idx+13).split("\n")[0];
					logger.log(LogService.LOG_INFO, "Retrieved filename "+filename);
					updateMPD(false);
					//Add to playlist
					mpd = getMPD();
					Collection<MPDSong> songs = mpd.getMPDDatabase().searchFileName(filename);
					Iterator it = songs.iterator();
					if (it.hasNext()){
						MPDSong song = (MPDSong)it.next();
						mpd.getMPDPlaylist().addSong(song);
						irc.say("bugcamp.net", "#buglabs", "Added "+filename+" to playlist");
					} else {
						logger.log(LogService.LOG_INFO, "Couldn't find "+filename+" in database :(");
						irc.say("bugcamp.net", "#buglabs", "Couldn't find "+filename+" in database :(");
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (MPDConnectionException e) {
					e.printStackTrace();
				} catch (MPDDatabaseException e) {
					e.printStackTrace();
				} catch (MPDPlaylistException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		irc = (IBotControl) context.getService(context.getServiceReference(IBotControl.class.getName()));
		logger = LogServiceUtil.getLogService(context);
		logger.log(LogService.LOG_INFO, "ircbot behavior mpd START");
		context.registerService(IChannelMessageConsumer.class.getName(), new IChannelMessageConsumer() {
			public String onChannelMessage(IChannelMessageEvent e) {
				logger.log(LogService.LOG_DEBUG, "got message:" + e.getMessage());
				if (e.getMessage().contains("gong")){
					if (quickPlay("paiste_sound_creation_gong_26inch_at_gongs_unlimited.mp3",1,3500))
						return "I only gong for you, "+e.getNick();
				} else if (e.getMessage().contains("trololol")){
					if (quickPlay("trololololo.mp3",0,10000))
						return e.getNick()+": you trololol so well!";
				} else if (e.getMessage().startsWith(e.getBotName() + ": mpd ")) {
					String command = e.getMessage().substring((e.getBotName() + ": mpd ").length());
					if (command.startsWith("youtube ")){
						String url = command.substring(("youtube ").length());
						youtubeAdd(url);
					}
					if (command.compareTo("song") == 0) {
						try {
							MPDSong song = getMPD().getMPDPlayer().getCurrentSong();
							return ("Title: " + song.getTitle() + ", Artist: " + song.getArtist() + ", File: " + song.getFile());
						} catch (Exception ex) {
							logger.log(LogService.LOG_INFO, "problem getting current song");
							ex.printStackTrace();
						}
					}
					
					if (command.compareTo("track length") == 0) {
						try {
							NumberFormat format = new DecimalFormat("#00");
							MPDSong song = getMPD().getMPDPlayer().getCurrentSong();
							int hours = (song.getLength() / 3600);
							int minutes = (song.getLength() / 60) % 60;
							int seconds = song.getLength() % 60;
							return ("" + format.format(hours) + ":" + format.format(minutes) + ":" + format.format(seconds));
						} catch (Exception ex) {
							logger.log(LogService.LOG_INFO, "problem getting current song");
							ex.printStackTrace();
						}
					}
					
					if (command.compareTo("play") == 0) {
						try {
							getMPD().getMPDPlayer().play();
							return "playing";
						} catch (Exception ex) {
							logger.log(LogService.LOG_INFO, "problem getting current song");
							ex.printStackTrace();
						}
					}
					
					if (command.compareTo("pause") == 0) {
						try {
							getMPD().getMPDPlayer().pause();
							return "pausing";
						} catch (Exception ex) {
							logger.log(LogService.LOG_INFO, "problem getting current song");
							ex.printStackTrace();
						}
					}
					
					if (command.compareTo("skip") == 0) {
						try {
							getMPD().getMPDPlayer().playNext();
							return "skipping";
						} catch (Exception ex) {
							logger.log(LogService.LOG_INFO, "problem skipping song");
							ex.printStackTrace();
						}
					}
					
					if (command.compareTo("update") == 0) {
						try {
							new Thread() {
								public void run() {
									updateMPD();
								}
							}.start();
							return "updating";
						} catch (Exception ex) {
							logger.log(LogService.LOG_INFO, "problem updating dabase");
							ex.printStackTrace();
						}
					}
				}
				return null;
			}
		}, null);
	}

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		if (mpd != null) {
			mpd.close();
		}
	}
	
	private String syscall(String command){
		String[] c = {
				"/bin/bash",
				"-c",
				command
			};
		String ret = "";
	
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(c);
			InputStreamReader in = new InputStreamReader(proc.getInputStream());
			//proc.waitFor();
			//While process is running, read from stream
			while (processRunning(proc)){
				if (in.ready())
					ret += ""+(char)in.read();
			}
			//When process is done, make sure we've cleared the buffer
			while (in.ready())
				ret += ""+(char)in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	private boolean processRunning(Process proc){
		try {
			proc.exitValue();
		} catch (IllegalThreadStateException e){
			return true;
		}
		return false;	
	}
	
}