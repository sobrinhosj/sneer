package wheel.io.codegeneration;

import java.io.File;

import org.apache.commons.io.FilenameUtils;


public class SimpleMetaClass extends MetaClassSupport {

	public SimpleMetaClass(File rootDirectory, File classFile) {
		super(rootDirectory, classFile);
	}

	@Override
	protected void parse() {
		String rootPath = _root.getAbsolutePath();
		String path = _classFile.getAbsolutePath();
		if (!path.startsWith(rootPath))
			throw new MetaClassException("Class file " + path + " on wrong directory");

		_className = FilenameUtils.separatorsToUnix(path.substring(rootPath.length() + 1));
		_className = _className.substring(0, _className.indexOf(".class"));
		_className = _className.replaceAll("/", ".");
		_packageName = _className.substring(0, _className.lastIndexOf("."));
		_isInterface = _packageName.indexOf("impl") < 0;
	}

}
