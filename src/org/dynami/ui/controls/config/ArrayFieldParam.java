package org.dynami.ui.controls.config;

import javafx.scene.control.TextField;

public class ArrayFieldParam<T> extends FieldParam {
	private final TextField text = new TextField();
	private final PropertyParam<String> prop;
	public ArrayFieldParam(PropertyParam<String> _prop){
		super(_prop.getName(), _prop.getDescription());
		prop = _prop;
		controlsContainer.getChildren().add(text);
	}

}
