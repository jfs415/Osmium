package com.kmecpp.osmium.api.config;

import java.lang.reflect.Field;

public class FieldData {

	private final Field field;
	private final String name;
	private final Setting setting;
	private final TypeData typeData;

	//	public FieldData(Field field, TypeData typeData) {
	//		this.field = field;
	//		this.setting = field.getAnnotation(Setting.class);
	//		this.name = getName(field, setting);
	//		this.typeData = typeData;
	//	}

	public FieldData(Field field, String name, Setting setting, TypeData typeData) {
		this.field = field;
		this.name = name;
		this.setting = setting;
		this.typeData = typeData;

		this.field.setAccessible(true);
	}

	public Object getFieldValue() {
		try {
			return field.get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException("Config fields must be static", e);
		}
	}

	public void load(Object loadedValue) {
		typeData.convert(loadedValue);
	}

	public String getName() {
		return name;
	}

	public Setting getSetting() {
		return setting;
	}

	public Field getField() {
		return field;
	}

	public Class<?> getType() {
		return typeData.getType();
	}

	public TypeData getTypeData() {
		return typeData;
	}

	public void setValue(Object value) throws IllegalArgumentException, IllegalAccessException {
		field.set(null, value);
	}

}
