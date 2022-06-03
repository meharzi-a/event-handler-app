package fr.harmonieMutuelle.bpm.lib.jms.service;

import java.util.logging.Logger;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueReceiver;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.harmonieMutuelle.bpm.lib.jms.model.EventHandlerMessage;

public class JmsSenderService {

	static Logger LOGGER = Logger.getLogger("fr.harmonieMutuelle.bpm.lib.jms.JmsSenderService");

	private String serverUrl;

	private String userName;

	private String password;


	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	static QueueConnection connection;
	static QueueReceiver queueReceiver;
	static Queue queue;

	static TextMessage message;

	public void sendTopicMessage(String topicName, EventHandlerMessage message) {

		Connection connection = null;
		Session session = null;
		MessageProducer msgProducer = null;
		Destination destination = null;


		try {
			TextMessage msg;

			LOGGER.info("Publishing to destination '" + topicName + "'\n");

			ConnectionFactory factory = new com.tibco.tibjms.TibjmsConnectionFactory(
					serverUrl);

			connection = factory.createConnection(userName, password);


			session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);



			destination = session.createQueue(topicName);


			msgProducer = session.createProducer(null);



			msg = session.createTextMessage();

			ObjectMapper objestMapper = new ObjectMapper();  
			String jsonMessage = objestMapper.writeValueAsString(message);  
			msg.setText(jsonMessage);

			msgProducer.send(destination, msg);



			LOGGER.info("Published message: " + jsonMessage);


			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
