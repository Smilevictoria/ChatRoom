package boot.spring.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.bson.Document;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import org.json.simple.JSONObject;

import boot.spring.po.Message;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.*;

@ServerEndpoint("/webSocket/{username}")
@Component
public class WebSocketServer {

	// concurrent package的線程安全設定，用来存放每个客户端對應的WebSocketServer對象。
	private static ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();

	// 建立連接成功調用
	@OnOpen
	public void onOpen(Session session, @PathParam(value = "username") String userName) {
		ConnectionString connectionString = new ConnectionString("mongodb+srv://victoria91718:cody97028@cluster0.my3cg8b.mongodb.net/?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase("history");
		MongoCollection<Document> collection = database.getCollection("chat");
		/*MongoDatabase database2 = mongoClient.getDatabase("customer");
		MongoCollection<Document> collection2 = database.getCollection("onlineUser");
		MongoCursor<Document> doc = collection2.find().iterator();
		ArrayList<Object> vi = new ArrayList(doc.next().values());
		//Set<String> nameset = new HashSet<String>();
		while (doc.hasNext()) {
			String tmpUser = (String) vi.get(1);
			if (!tmpUser.equals(userName)) {
				sessionPools.put(tmpUser, session);
			}
		}*/
		// link建立，儲存連接對象
		sessionPools.put(userName, session);
		// boardcast online message
		Message msg = new Message();
		msg.setFrom("系统消息");
		msg.setDate(new Date());
		msg.setTo("0");
		msg.setText(userName);
		String logJson = JSON.toJSONString(msg);
		Document indoc = Document.parse(logJson);
		collection.insertOne(indoc);
		broadcast(JSON.toJSONString(msg, true));
	}

	// 關閉連接時調用
	@OnClose
	public void onClose(@PathParam(value = "username") String userName) {
		ConnectionString connectionString = new ConnectionString("mongodb+srv://victoria91718:cody97028@cluster0.my3cg8b.mongodb.net/?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase("history");
		MongoCollection<Document> collection = database.getCollection("chat");
		sessionPools.remove(userName);
		// 广播下线消息
		Message msg = new Message();
		msg.setFrom("系统消息");
		msg.setDate(new Date());
		msg.setTo("-2");
		msg.setText(userName);
		String logJson = JSON.toJSONString(msg);
		Document indoc = Document.parse(logJson);
		collection.insertOne(indoc);
		broadcast(JSON.toJSONString(msg, true));
	}

	// 收到客户端信息后，根據接收人的username把消息私聊或者群發
	// to=-1群發消息
	@OnMessage
	public void onMessage(String message) throws IOException {
		ConnectionString connectionString = new ConnectionString("mongodb+srv://victoria91718:cody97028@cluster0.my3cg8b.mongodb.net/?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase("history");
		MongoCollection<Document> collection = database.getCollection("chat");
		Message msg = JSON.parseObject(message, Message.class);
		msg.setDate(new Date());
		if (msg.getTo().equals("-1")) {
			String logJson = JSON.toJSONString(msg);
			Document indoc = Document.parse(logJson);
			collection.insertOne(indoc);
			broadcast(JSON.toJSONString(msg, true));
		} else {
			String logJson = JSON.toJSONString(msg);
			Document indoc = Document.parse(logJson);
			collection.insertOne(indoc);
			sendInfo(msg.getTo(), JSON.toJSONString(msg, true));
		}
	}

	// 錯誤時調用
	@OnError
	public void onError(Session session, Throwable throwable) {
		throwable.printStackTrace();
	}

	// 给指定用户發送信息
	public void sendInfo(String userName, String message) {
		Session session = sessionPools.get(userName);
		try {
			sendMessage(session, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 群發消息
	public void broadcast(String message) {
		for (Session session : sessionPools.values()) {
			try {
				sendMessage(session, message);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	// 發送消息
	public void sendMessage(Session session, String message) throws IOException {
		if (session != null) {
			synchronized (session) {
				session.getBasicRemote().sendText(message);
			}
		}
	}

	public static ConcurrentHashMap<String, Session> getSessionPools() {
		return sessionPools;
	}
}
