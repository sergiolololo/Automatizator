package com.telefonica.modulos.comparador.poms.model;

import java.util.Objects;

public class JsonDependency {
	String name;
	String version;

	public JsonDependency(String name, String version) {
		super();
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonDependency other = (JsonDependency) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "JsonDepency [name=" + name + ", version=" + version + "]";
	}

}
