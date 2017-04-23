package com.jeffersonmoreira.mqttproject;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MessageActionListener implements IMqttActionListener {
	protected final String messageText;
	protected final String topic;
	protected final String userContext;

	public MessageActionListener(String messageText, String topic, String userContext) {
		super();
		this.messageText = messageText;
		this.topic = topic;
		this.userContext = userContext;
	}

	public void onSuccess(IMqttToken asyncActionToken) {
		if ((asyncActionToken != null) && asyncActionToken.getUserContext().equals(userContext)) {
			System.out.println(String.format("Mensagem '%s' publicada para o tópico '%s' ", messageText, topic));
		}
	}

	public void onFailure(IMqttToken asyncActionToken, Throwable ex) {
		ex.printStackTrace();
		Logger log = Logger.getLogger(this.getClass().getSimpleName() + " - " + ex);
		log.log(Level.SEVERE, "Falha apresentada " + log.getName() + "-" + log.toString());
	}

}
