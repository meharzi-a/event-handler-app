package fr.harmonieMutuelle.bpm.lib.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import fr.harmonieMutuelle.bpm.lib.jms.config.JmsConfig;
import fr.harmonieMutuelle.bpm.lib.jms.model.EventHandlerMessage;
import fr.harmonieMutuelle.bpm.lib.jms.service.JmsSenderService;

@SpringBootApplication(scanBasePackages={
		"fr.harmonieMutuelle.bpm.lib.main", "fr.harmonieMutuelle.bpm.lib.jms.config"})
public class DemoApplication implements CommandLineRunner {
	
	private static Logger LOGGER = LoggerFactory.getLogger(DemoApplication.class);
	
	@Autowired
	private JmsConfig jmsConfig;
	
    @Override
    public void run(String... args) {
    	Path currentRelativePath = Paths.get("");
    	String s = currentRelativePath.toAbsolutePath().toString();
    	LOGGER.info("Current absolute path is: " + s);
    	
    	JmsSenderService sender = new JmsSenderService();
	  	EventHandlerMessage eventHandlerMessage = new EventHandlerMessage();
	  	eventHandlerMessage.setDateEvent(LocalDateTime.now().toString());
	  	eventHandlerMessage.setIdDemande(37020);
	  	eventHandlerMessage.setTypeEvent("PROCESSINSTANCE_STATE_UPDATED");
	  	eventHandlerMessage.setBusinessDataType("");
	  	eventHandlerMessage.setTypeProcessus("");
	  	
	  	sender.setServerUrl(jmsConfig.getServer_url());
	  	sender.setUserName(jmsConfig.getUser_name());
	  	sender.setPassword(jmsConfig.getPassword());
		String propPath = System.getenv( "EVENT_HANDLER_PROPERTIES" );
		LOGGER.info("Prop Path " + propPath);
	  	sender.sendTopicMessage(jmsConfig.getDefault_destination_name(),eventHandlerMessage);
	  	
    }
   public static void main(String[] args) {
      	SpringApplication.run(DemoApplication.class, args);
   }
} 
