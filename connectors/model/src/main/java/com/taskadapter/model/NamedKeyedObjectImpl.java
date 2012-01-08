package com.taskadapter.model;

public class NamedKeyedObjectImpl implements NamedKeyedObject {
	private String key;
	private String name;

	public NamedKeyedObjectImpl(String key, String name) {
		super();
		this.key = key;
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public String getName() {
		return name;
	}
}
