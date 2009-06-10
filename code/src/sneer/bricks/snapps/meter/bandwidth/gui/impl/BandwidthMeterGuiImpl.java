package sneer.bricks.snapps.meter.bandwidth.gui.impl;

import static sneer.foundation.commons.environments.Environments.my;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JLabel;

import sneer.bricks.pulp.bandwidth.BandwidthCounter;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.main.synth.Synth;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.snapps.meter.bandwidth.gui.BandwidthMeterGui;
import sneer.foundation.commons.lang.Functor;

class BandwidthMeterGuiImpl implements BandwidthMeterGui {

	private final Synth _synth = my(Synth.class);
	
	private final JLabel _bpsPeakLabel = new JLabel("kB/s(Peak)");
	private final JLabel _uploadIcon = new JLabel();
	private final JLabel _downloadIcon = new JLabel();

	BandwidthMeterGuiImpl() {
		my(InstrumentRegistry.class).registerInstrument(this);
		initSynth();
	} 
	
	private void initSynth() {
		_synth.load(this);
		
		_synth.attach(_bpsPeakLabel);
		_bpsPeakLabel.setName("KbsPeakLabel");
		
		_synth.attach(_uploadIcon);
		_uploadIcon.setName("UploadIcon");
		
		_synth.attach(_downloadIcon);
		_downloadIcon.setName("DownloadIcon");
	}
	
	private class MaxHolderFunctor implements Functor<Integer, String>{
		int _maxValue = 0;
		@Override public String evaluate(Integer value) {
			if(_maxValue<value) _maxValue=value;
			return value + " (" + _maxValue + ")";
		}
	}
	
	@Override
	public void init(InstrumentPanel window) {
		Container container = window.contentPane();
		
		TextWidget<JLabel> _uploadText = newLabel(my(BandwidthCounter.class).uploadSpeed(), "UploadText");
		TextWidget<JLabel> _downloadText = newLabel(my(BandwidthCounter.class).downloadSpeed(), "DownloadText");
		
		container.add(_bpsPeakLabel);
		container.add(_uploadIcon);
		container.add(_uploadText.getComponent());
		container.add(_downloadIcon);
		container.add(_downloadText.getComponent());
		
		Dimension size = (Dimension) _synth.getDefaultProperty("BandwidthText.preferedSize");
		if(size!=null) return;	
		
		_uploadText.getComponent().setPreferredSize(size);
		_downloadText.getComponent().setPreferredSize(size);
	}
	
	private TextWidget<JLabel> newLabel(final Signal<Integer> source, String synthName){
		return my(ReactiveWidgetFactory.class).newLabel(maxHolder(source), synthName);
	}

	private Signal<String> maxHolder(Signal<Integer> input) {
		return my(Signals.class).adapt(input, new MaxHolderFunctor());
	}

	@Override
	public int defaultHeight() {
		return DEFAULT_HEIGHT;
	}

	@Override
	public String title() {
		return "Bandwith Meter";
	}
}