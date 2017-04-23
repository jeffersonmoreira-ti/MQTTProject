package com.jeffersonmoreira.mqttproject;

import java.util.concurrent.ThreadLocalRandom;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttProject {

	public static void main(String[] args) {

		System.out.println("Main Class");
		IotDevice device1 = new IotDevice("[Device 1]");
		device1.connect();
		IotDevice device2 = new IotDevice("[Device 2]");
		device2.connect();
		IotDevice devicePrincipal = new IotDevice("*Device Principal*");
		devicePrincipal.connect();
		try {
			while (true) {
				try {
					Thread.sleep(5000);
					int r = ThreadLocalRandom.current().nextInt(1, 11);
					if ((r < 5) && device1.isConnected()) {
						devicePrincipal.publishCommand(IotDevice.GET_TEMPERATURA_COMMAND_KEY, device1.getDeviceName());
					} else if (device2.isConnected()) {
						devicePrincipal.publishCommand(IotDevice.GET_TEMPERATURA_COMMAND_KEY, device2.getDeviceName());
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (device1.isConnected()) {
				try {
					device1.client.disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
			if (device2.isConnected()) {
				try {
					device2.client.disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
			if (devicePrincipal.isConnected()) {
				try {
					devicePrincipal.client.disconnect();
				} catch (MqttException e) {
					e.printStackTrace();
				}
			}
		}

		/*
		 * String topic = "sul/pocos/CS-125/injecao/instantanea"; String content
		 * = "Injeção Instantânea Poço CS-125: "+ThreadLocalRandom.current().
		 * nextDouble(0, 800); int qos = 2; String broker =
		 * "tcp://192.168.0.104:1883"; String clientId = "JavaSample";
		 * MemoryPersistence persistence = new MemoryPersistence();
		 * 
		 * try { MqttClient sampleClient = new MqttClient(broker, clientId,
		 * persistence); MqttConnectOptions connOpts = new MqttConnectOptions();
		 * connOpts.setCleanSession(true);
		 * System.out.println("Connecting to broker: " + broker);
		 * sampleClient.connect(connOpts); System.out.println("Connected");
		 * System.out.println("Publishing message: " + content); MqttMessage
		 * message = new MqttMessage(content.getBytes()); message.setQos(qos);
		 * sampleClient.publish(topic, message);
		 * System.out.println("Message published"); sampleClient.disconnect();
		 * System.out.println("Disconnected"); System.exit(0); } catch
		 * (MqttException me) { System.out.println("reason " +
		 * me.getReasonCode()); System.out.println("msg " + me.getMessage());
		 * System.out.println("loc " + me.getLocalizedMessage());
		 * System.out.println("cause " + me.getCause());
		 * System.out.println("excep " + me); me.printStackTrace(); }
		 */

	}

}
