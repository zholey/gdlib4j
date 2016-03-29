/*
 * 版权所有 ©2011-2013 格点软件(北京)有限公司 All rights reserved.
 * 
 * 未经书面授权，不得擅自复制、影印、储存或散播。
 */
package org.gridsofts.media;

import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JOptionPane;

public class PlaySound extends Thread {
	private URL url;

	private boolean isloop;

	private volatile boolean isStoped = false;

	private PlaySound selfthread;

	public PlaySound(URL url) {
		selfthread = this;
		this.url = url;
		this.isloop = false;
	}

	public PlaySound(URL url, boolean isloop) {
		selfthread = this;
		this.url = url;
		this.isloop = isloop;
	}

	public void run() {
		try {
			AudioInputStream stream = AudioSystem.getAudioInputStream(url);
			AudioFormat format = stream.getFormat();

			DataLine.Info info = new DataLine.Info(Clip.class, stream.getFormat(),
					((int) stream.getFrameLength() * format.getFrameSize()));
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.addLineListener(new LineListener() {
				public void update(LineEvent e) {
					synchronized (selfthread) {
						if (e.getType().equals(LineEvent.Type.STOP)) {
							isStoped = true;
							selfthread.notify();
						}
					}
				}
			});

			clip.open(stream);

			do {
				clip.start();
				synchronized (this) {
					while (!isStoped) {
						try {
							this.wait();
						} catch (Exception e) {
						}
					}
				}
				isStoped = false;
				clip.setFramePosition(0);
			} while (isloop);
			clip.close();
			stream.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "PlaySound", JOptionPane.ERROR_MESSAGE);
		}
	}
}