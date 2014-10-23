package net.francesbagual.github.eip.pattern.transformation.normalizer;

import java.io.Serializable;

public class NormalizedMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	public String content;

	public NormalizedMessage(String content) {
		super();
		this.content = content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NormalizedMessage other = (NormalizedMessage) obj;
		if (content == null) {
			if (other.content != null) return false;
		} else if (!content.equals(other.content)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "NormalizedMessage [content=" + content + "]";
	}
	
	
}
