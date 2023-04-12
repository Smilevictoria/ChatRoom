package boot.spring.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import boot.spring.po.onlineUser;
import com.alibaba.fastjson.JSON;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import boot.spring.po.User;
import boot.spring.service.LoginService;
import boot.spring.service.WebSocketServer;

@Controller
@CrossOrigin
public class ChatController {

	@Autowired
	LoginService loginservice;
	
	/**
	 * OnlineUser
	 */
	@RequestMapping("/onlineusers")
	@ResponseBody
	public Set<String> onlineusers(@RequestParam("currentuser") String currentuser) {
		ConcurrentHashMap<String, Session> map = WebSocketServer.getSessionPools();
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		Set<String> nameset = new HashSet<String>();
		while (it.hasNext()) {
			String entry = it.next();
			if (!entry.equals(currentuser))
				nameset.add(entry);
		}
		return nameset;
		/*ConnectionString connectionString = new ConnectionString("mongodb+srv://victoria91718:cody97028@cluster0.my3cg8b.mongodb.net/?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase("customer");
		MongoCollection<Document> collection = database.getCollection("onlineUser");
		MongoCursor<Document> doc = collection.find().iterator();
		ArrayList<Object> vi = new ArrayList(doc.next().values());
		Set<String> nameset = new HashSet<String>();
		while (doc.hasNext()) {
			String tmpUser = (String) vi.get(1);
			if (!tmpUser.equals(currentuser)) {
				nameset.add(tmpUser);
			}
		}
		return nameset;*/
	}
	
	/**
	 * CurrentUser
	 */
	@RequestMapping(value = "/currentuser", method = RequestMethod.GET)
	@ResponseBody
	public User currentuser(HttpSession httpSession) {
		User user = (User) httpSession.getAttribute("user");
		return user;
	}
}
