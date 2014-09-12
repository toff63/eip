package net.francesbagual.github.eip.pattern.router.agregator.mdb;

/**
 * @author toff63
 *
 */
public class IndexedMessage implements Comparable<IndexedMessage>{

	public Long index;
	public String text;
	
	

	public IndexedMessage(Long index, String text) {
		super();
		this.index = index;
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		IndexedMessage other = (IndexedMessage) obj;
		if (index == null) {
			if (other.index != null) return false;
		} else if (!index.equals(other.index)) return false;
		if (text == null) {
			if (other.text != null) return false;
		} else if (!text.equals(other.text)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "IndexedMessage [index=" + index + ", text=" + text + "]";
	}

	@Override
	public int compareTo(IndexedMessage o) {
		return this.index.compareTo(o.index);
	}

}
