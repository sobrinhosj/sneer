package dfcsantos.wusic.impl;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Functor;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.folder.TracksFolderKeeper;
import dfcsantos.wusic.Wusic;

public class WusicImpl implements Wusic {

	private static final Format TIME_FORMATTER = new SimpleDateFormat("mm:ss");

	private OperatingMode _currentOperatingMode = OperatingMode.OWN;

	private TrackSourceStrategy _trackSource = OwnTracks.INSTANCE;

	private final Register<Track> _trackToPlay = my(Signals.class).newRegister(null);

	private final DJ _dj = new DJ(_trackToPlay.output(), new Runnable() { @Override public void run() {
		skip();
	}});

	@Override
	public void setOperatingMode(OperatingMode mode) {
		_trackSource = (mode == OperatingMode.OWN) ? OwnTracks.INSTANCE : SharedTracks.INSTANCE;
	}


	@Override
	public OperatingMode operatingMode() {
		return _currentOperatingMode;
	}


	@Override
	public void setPlayingFolder(File ownTracksFolder) {
		my(TracksFolderKeeper.class).setOwnTracksFolder(ownTracksFolder);
		skip();
	}


	@Override
	public void setSharedTracksFolder(File sharedTracksFolder) {
		my(TracksFolderKeeper.class).setSharedTracksFolder(sharedTracksFolder);
	}


	@Override
	public void setShuffle(boolean shuffle) {
		_trackSource.setShuffle(shuffle);
	}


	@Override
	public void start() {
		skip();
	}


	@Override
	public Signal<String> playingTrackName() {
		return my(Signals.class).adapt(_trackToPlay.output(), new Functor<Track, String>() { @Override public String evaluate(Track track) {
			return (track == null)
					? "<No track to play>"
					: (track.name().length() >= 54) ? track.name().substring(0, 51).concat("...") : track.name();
		}});
	}


	@Override
	public Signal<String> playingTrackTime() {
		return my(Signals.class).adapt(_dj.trackElapsedTime(), new Functor<Integer, String>() { @Override public String evaluate(Integer timeElapsed) {
			return TIME_FORMATTER.format(new Date(timeElapsed));
		}});
	}


	@Override
	public void pauseResume() {
		if (currentTrack() == null)
			skip();
		else
			_dj.pauseResume();
	}


	private Track currentTrack() {
		return _trackToPlay.output().currentValue();
	}


	@Override
	public void back() {
		play(_trackSource.previousTrack());
	}


	@Override
	public void skip() {
		play(_trackSource.nextTrack());
	}


	private void play(final Track track) {
		_trackToPlay.setter().consume(track);
	}


	@Override
	public void stop() {
		play(null);
	}


	@Override
	public void meToo() {
		((SharedTracks)_trackSource).meToo(_trackToPlay.output().currentValue());
	}

	
	@Override
	public void noWay() {
		Track currentTrack = currentTrack();
		if (currentTrack == null) return;

		skip();
		((SharedTracks)_trackSource).noWay(currentTrack);
	}


	@Override
	public Signal<String> numberOfSharedTracks() {
		return my(Signals.class).adapt(my(TrackClient.class).numberOfSharedTracks(), new Functor<Integer, String>() { @Override public String evaluate(Integer numberOfTracks) {
			return "Shared Tracks <" + numberOfTracks + ">";
		}});
	}


	@Override
	public Signal<Boolean> isPlaying() {
		return _dj.isPlaying();
	}

}
