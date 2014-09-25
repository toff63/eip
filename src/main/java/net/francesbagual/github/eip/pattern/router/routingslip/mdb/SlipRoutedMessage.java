package net.francesbagual.github.eip.pattern.router.routingslip.mdb;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;

import net.francesbagual.github.eip.pattern.message.Message;

import org.apache.commons.collections4.map.HashedMap;

public class SlipRoutedMessage implements Message<String> , Serializable{

	private Map<String, Object> headers = new HashedMap<String,Object>();
	private String body;
	
	public SlipRoutedMessage(String body) {
		setBody(body);
	}

	public void setPath(List<Destination> destinations){
		headers.put("path", destinations);
	}
	
	@SuppressWarnings("unchecked")
	public List<Destination> path(){return (List<Destination>) headers.get("path");}
	
	@Override
	public Map<String, Object> headers() {
		return headers;
	}

	@Override
	public String body() {
		return body;
	}
	
	public void setBody(String body){
		this.body = body;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SlipRoutedMessage other = (SlipRoutedMessage) obj;
		if (body == null) {
			if (other.body != null) return false;
		} else if (!body.equals(other.body)) return false;
		if (headers == null) {
			if (other.headers != null) return false;
		} else if (!headers.equals(other.headers)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "SlipRoutedMessage [headers=" + headers + ", body=" + body + "]";
	}
	
	

}
