package boot.spring.po;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import org.json.simple.*;

public class Message {
	// sender name
	public String from;
	// receiver name 0(online) -1(for all) -2(offline)
	public String to;
	// sender's text
	public String text;
	// send time
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	public Date date;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
