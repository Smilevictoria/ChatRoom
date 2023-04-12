package boot.spring.controller;

import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSON;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.spring.po.User;
import boot.spring.po.onlineUser;
import boot.spring.service.LoginService;

import java.util.ArrayList;

@Controller
public class Login {
	@Autowired
	LoginService loginservice;

	@RequestMapping("/loginvalidate")
	public String loginvalidate(@RequestParam("username") String username, @RequestParam("password") String pwd, HttpSession httpSession) {

		ConnectionString connectionString = new ConnectionString("mongodb+srv://victoria91718:cody97028@cluster0.my3cg8b.mongodb.net/?retryWrites=true&w=majority");
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(connectionString)
				.build();
		MongoClient mongoClient = MongoClients.create(settings);
		MongoDatabase database = mongoClient.getDatabase("customer");
		MongoCollection<Document> collection = database.getCollection("user");
		//MongoCollection<Document> collection2 = database.getCollection("onlineUser");
		//Document whereQuery = new Document();
		MongoCursor<Document> doc = collection.find().iterator();
		/*User setUser = new User();
		setUser.setUsername("Devil");
		setUser.setPassword("0216");
		String logJson = JSON.toJSONString(setUser);
		Document indoc = Document.parse(logJson);
		collection.insertOne(indoc);*/

		while (doc.hasNext()) {
			ArrayList<Object> vi = new ArrayList(doc.next().values());
			String tmpNa = (String) vi.get(2);
			String tmpPw = (String) vi.get(1);
			if (username.equals(tmpNa)) {
				if (pwd.equals(tmpPw)) {
					//user.setPassword("NULL");
					//System.out.print(tmpNa);
					//System.out.print(tmpPw);
					User user = new User(username, pwd);
					/*onlineUser onUser = new onlineUser();
					onUser.setonliner(username);
					String onJson = JSON.toJSONString(onUser);
					Document ondoc = Document.parse(onJson);
					collection2.insertOne(ondoc);*/
					httpSession.setAttribute("user", user);

					return "chatroom";
				}
			}
		}
		return "loginfail";
		/*if (username == null) {
			return "/";
		}
		User user = loginservice.getUserByName(username);
		String realpwd = user.getPassword();
		if (realpwd != null && pwd.equals(realpwd)) {
			user.setPassword("NULL");
			httpSession.setAttribute("user", user);
			return "chatroom";
		} else {
			return "loginfail";
		}*/
	}

	@RequestMapping("/")
	public String login() {
		return "login";
	}

	@RequestMapping("/logout")
	public String logout(HttpSession httpSession) {
		httpSession.removeAttribute("user");
		return "login";
	}
	
}
