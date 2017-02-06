package audio;

import java.io.IOException;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class AudioTrack {

	private Audio audio;

	public AudioTrack(String path) {

		try {
			audio = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("res/" + path + ".ogg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playAsMusic() {
		audio.playAsMusic(1.0f, 1.0f, true);
	}

	public void stop() {
		audio.stop();
	}

	public void playAsSoundEffect() {
		if (!audio.isPlaying())
			audio.playAsSoundEffect(1.0f, 1.0f, false);
	}

	public void playAsSoundEffect(boolean repeat) {
		if (!audio.isPlaying())
			audio.playAsSoundEffect(1.0f, 1.0f, repeat);
	}

	public void play() {
		audio.playAsSoundEffect(1.0f, 1.0f, false);
	}
}
