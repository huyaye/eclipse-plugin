package com.packtpub.e4.minimark.ui;

import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

public class MinimarkEditor extends AbstractDecoratedTextEditor {

	public MinimarkEditor() {
		// DocumentProvider makes synchronization editor with file's content.
		setDocumentProvider(new TextFileDocumentProvider());
	}
}
