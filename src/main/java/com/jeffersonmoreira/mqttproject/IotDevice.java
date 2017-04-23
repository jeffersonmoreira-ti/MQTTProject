package com.jeffersonmoreira.mqttproject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class IotDevice implements MqttCallback, IMqttActionListener {
	public static final String COMMAND_KEY = "COMANDO";
	public static final String COMMAND_SEPARATOR = ":";
	public static final String GET_TEMPERATURA_COMMAND_KEY = "GET_TEMPERATURA";
	public static final String TOPIC = "jeffersonmoreira/temperatura"; // Tópico
	public static final String ENCODING = "UTF-8";
	public static final int QOS = 2; // Quality Of Service
	protected String deviceName;
	protected String clientId;
	protected MqttAsyncClient client;
	protected MemoryPersistence memoryPersistence;
	protected IMqttToken connectToken;
	protected IMqttToken subscribeToken;

	public IotDevice(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void connect() {
		try {
			MqttConnectOptions options = new MqttConnectOptions();
			memoryPersistence = new MemoryPersistence();
			String serverURI = "tcp://192.168.0.104:1883";
			clientId = MqttAsyncClient.generateClientId();
			client = new MqttAsyncClient(serverURI, clientId, memoryPersistence);

			client.setCallback(this);
			connectToken = client.connect(options, null, this);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return (client != null) && (client.isConnected());
	}

	public void connectionLost(Throwable causa) {
		causa.printStackTrace();

	}

	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	public void messageArrived(String topico, MqttMessage mensagem) throws Exception {
		if (!topico.equals(TOPIC)) {
			return;
		}

		String messageText = new String(mensagem.getPayload(), ENCODING);
		System.out.println(String.format("%s recebida %s: %s", deviceName, topico, messageText));
		String[] keyValue = messageText.split(COMMAND_SEPARATOR);

		if (keyValue.length != 3) {
			return;
		}

		/**
		 * Executa o comando para retornar a temperatura. Por enquanto está //
		 * sem nenhum valor obtido de algum sensor porque estou aguarndo o //
		 * sensor de temperatura chegar pra usar no RPi (ou pelo menos tentar //
		 * ;) ).
		 * @author jeffersonmoreira
		 **/

		if (keyValue[0].equals(COMMAND_KEY) && keyValue[1].equals(GET_TEMPERATURA_COMMAND_KEY)
				&& keyValue[2].equals(deviceName)) {
			double temperaturaEmCelcius = ThreadLocalRandom.current().nextDouble(-30, 40);
			System.out.println(String.format("%s Temperatura: %f celcius", deviceName, temperaturaEmCelcius));
			publishTextMessage(String.format("%s Temperatura: %f celcius", deviceName, temperaturaEmCelcius));
		}

	}

	public void onFailure(IMqttToken asyncActionToken, Throwable ex) {
		ex.printStackTrace();
		Logger log = Logger.getLogger(this.getClass().getSimpleName() + " - " + ex);
		log.log(Level.SEVERE, "Falha apresentada " + log.getName() + "-" + log.toString());
	}

	public void onSuccess(IMqttToken asyncActionToken) {
		if (asyncActionToken.equals(connectToken)) {
			System.out.println(String.format("%s conectado com sucesso.", deviceName));
			try {
				subscribeToken = client.subscribe(TOPIC, QOS, null, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (asyncActionToken.equals(subscribeToken)) {
			System.out.println(String.format("%s inscrito no tópico %s", deviceName, TOPIC));
			publishTextMessage(String.format("%s está aguardando mensagems.", deviceName));
		}
	}

	private MessageActionListener publishTextMessage(String mensagem) {
		byte[] bytesMessage;
		try {
			bytesMessage = mensagem.getBytes(ENCODING);
			MqttMessage mqttMessage;
			mqttMessage = new MqttMessage(bytesMessage);
			String userContext = "Aguardando mensagens";
			MessageActionListener actionListener = new MessageActionListener(TOPIC, mensagem, userContext);
			client.publish(TOPIC, mqttMessage, userContext, actionListener);
			return actionListener;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MqttPersistenceException e) {
			e.printStackTrace();
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return null;

	}

	public MessageActionListener publishCommand(String nomeComando, String nomeDestino) {

		String comando = String.format("%s%s%s%s%s", COMMAND_KEY, COMMAND_SEPARATOR, nomeComando, COMMAND_SEPARATOR,
				nomeDestino);
		System.out.println(comando);
		return publishTextMessage(comando);
	}
}
