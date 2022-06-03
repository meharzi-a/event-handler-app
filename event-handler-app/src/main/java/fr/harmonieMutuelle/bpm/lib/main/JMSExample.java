package fr.harmonieMutuelle.bpm.lib.main;

import java.time.LocalDateTime;

import javax.jms.JMSException;

import fr.harmonieMutuelle.bpm.lib.jms.model.EventHandlerMessage;
import fr.harmonieMutuelle.bpm.lib.jms.service.JmsSenderService;

public class JMSExample {

	static JmsSenderService sender = new JmsSenderService();



    public static void main(String[] args) throws JMSException {
    	EventHandlerMessage eventHandlerMessage = new EventHandlerMessage();
    	eventHandlerMessage.setDateEvent(LocalDateTime.now().toString());
    	eventHandlerMessage.setIdDemande(12006);
    	eventHandlerMessage.setTypeEvent("PROCESSINSTANCE_STATE_UPDATED");
    	eventHandlerMessage.setBusinessDataType("");
    	eventHandlerMessage.setTypeProcessus("");
    	
    	sender.sendTopicMessage("queue.int.bna.datalab",eventHandlerMessage);
    	//sender.sendTopicMessage("queue.dev.bna.datalab",eventHandlerMessage);
    }
}