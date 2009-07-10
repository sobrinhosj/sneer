package sneer.bricks.snapps.diff.text.gui.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import sneer.bricks.hardware.gui.Action;
import sneer.bricks.hardware.io.IO;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.main.synth.scroll.SynthScrolls;
import sneer.bricks.skin.notmodal.filechooser.FileChoosers;
import sneer.bricks.snapps.diff.text.gui.TextDiffGui;
import sneer.bricks.software.diff.TextComparator;
import sneer.bricks.software.diff.TextComparator.TextBlock;
import sneer.foundation.lang.Consumer;

class TextDiffGuiImpl implements TextDiffGui{

	private final JEditorPane _htmlDif = new JEditorPane();
	private final JScrollPane _scroll = my(SynthScrolls.class).create();
	
	private final Consumer<File> _fileConsumer = new Consumer<File>(){	
		private File _file1;
		
		@Override public void consume(File value) {
			if(value==null) {
				_file1 = null;
				return;
			}
			
			if(_file1==null){
				_file1 = value;
				showFileChooser();
				return;
			}
			
			File tmp = _file1;
			_file1 = null;
			compare(tmp, value);
		}
	};
	private JFileChooser _chooser = fileChooser(_fileConsumer);
	
	private InstrumentPanel _container;
	
	TextDiffGuiImpl() {
		my(InstrumentRegistry.class).registerInstrument(this);	
		_htmlDif.setEditable(false);
	}

	@Override public int defaultHeight() { return 162; }
	@Override public String title() { return "Text Diff"; }
	
	@Override public void init(InstrumentPanel container) {
		_container = container;
		container.contentPane().setLayout(new BorderLayout());
		container.contentPane().add(_scroll, BorderLayout.CENTER);

		_scroll.getViewport().add(_htmlDif);
		
		container.actions().addAction(new Action(){
			@Override public String caption() { return "Compare...";}
			@Override public void run() {  
				_chooser.showOpenDialog(_container.contentPane()); 
			}
		});
	}

	private void compare(File file1, File file2) {
		String text1;
		String text2;
		try {
			text1 = read(file1);
			text2 = read(file2);
		} catch (IOException ignore) {
			return;
		}
		compare(text1, text2);
	}

	private String read(File file) throws IOException {
		try {
			return my(IO.class).files().readString(file);
		} catch (IOException e) {
			my(BlinkingLights.class).turnOn(LightType.ERROR, "Error", "Unable to read file: " + file.getAbsolutePath(),  e, 5*60*1000);
			throw e;
		}
	}

	private void compare(String text1, String text2) {
		TextComparator comparator = my(TextComparator.class);
		Iterator<TextBlock> diff = comparator.diff(text1, text2);
		String html = comparator.toPrettyHtml(diff);
		
		_htmlDif.setText(html);
	}

	private int showFileChooser() {
		return _chooser.showOpenDialog(_container.contentPane());
	}

	private JFileChooser fileChooser(Consumer<File> selectedFile) {
		return my(FileChoosers.class).newFileChooser(	selectedFile);
	}
}