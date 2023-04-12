package boot.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebScoket配置處理器
 */
@Configuration
public class WebSocketConfig {
	/**
	 * ServerEndpointExporter 作用 Bean會自動注册使用@ServerEndpoint注解聲明的websocket endpoint
	 */
	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
