package fr.harmonieMutuelle.bpm.lib.events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.ApiAccessType;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.core.process.definition.model.SFlowNodeType;
import org.bonitasoft.engine.core.process.instance.model.SFlowNodeInstance;
import org.bonitasoft.engine.core.process.instance.model.SHumanTaskInstance;
import org.bonitasoft.engine.events.model.SEvent;
import org.bonitasoft.engine.events.model.SHandler;
import org.bonitasoft.engine.events.model.SHandlerExecutionException;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import org.bonitasoft.engine.util.APITypeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import fr.harmonieMutuelle.bpm.lib.jms.model.EventHandlerMessage;
import fr.harmonieMutuelle.bpm.lib.jms.service.JmsSenderService;

@ComponentScan({"fr.harmonieMutuelle.bpm.lib.events","fr.harmonieMutuelle.bpm.lib.jms.config"})
public class EventHandler implements SHandler<SEvent>, Serializable{

	private  static  final  long serialVersionUID =  1350092881346723535L;
	private static Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

	/*	@Autowired
        private JmsConfig jmsConfig;
    */
	public EventHandler(long tenantId) {
		this.tenantId = tenantId;
	}

	public long tenantId;

	@Override
	public void execute(SEvent event) {
		LOGGER.info("***** Event Handler Bonita : execute ");
		LOGGER.info("Event Handler Bonita : event.getType() ", event.getType());
		long processInstanceId = 0L;
		String serverURLFromConf	=	null;
		String usernameFromConf		=	null;
		String passwordFromConf		=	null;
		String queueNameFromConf	=	null;
		String bonitaUsername		= 	null;
		String bonitaPassword		=	null;
		String bonitaserverUrl		=	null;

		try {
			SHumanTaskInstance sHumanTaskInstance = (SHumanTaskInstance) event.getObject();
			LOGGER.info("Event Handler Bonita :task sHumanTaskInstance.getId() " + sHumanTaskInstance.getId() );
			processInstanceId = sHumanTaskInstance.getParentProcessInstanceId();
			LOGGER.info("Event Handler Bonita : processInstanceId" + processInstanceId);
			LOGGER.info("Event Handler Bonita : sHumanTaskInstance.getName()" + sHumanTaskInstance.getName());

		} catch (Exception e) {
			LOGGER.info("Event Handler Bonita e : Error in Event Handler ",e);
		}

		// Chargement des properties :
		LOGGER.info(" **** Event Handler - Chargement des properties ");
		Properties properties = new Properties();

		// Test premier repertoire
		//String filePath1 = "/logiciel/bonita/server/webapps/bonita/WEB-INF/event-handler.properties";
		String filePath1 = "C:\\Users\\meharzi-a\\Documents\\BONITA_BUNDLE\\server\\conf\\event-handler.properties";
		String filePath2 = "/logiciel/bonita/server/conf/event-handler.properties";
		try {
			File external1 = new File(filePath1);
			if (external1.exists()) {
				LOGGER.info(" **** Event Handler - external1 .exists() ");
				try {
					properties.load(new FileInputStream(external1));
				} catch (FileNotFoundException e1) {
					LOGGER.info("** props getClassLoader().getResourceAsStream  properties.load e1 ",e1);
				} catch (IOException e2) {
					LOGGER.info("** props getClassLoader().getResourceAsStream  properties.load e2 ",e2);
				}  catch (Exception e) {
					LOGGER.info("** props getClassLoader().getResourceAsStream  properties.load e",e);
				}
			}else {
				LOGGER.info(" **** Event Handler - external1. NOT exists() ");
			}

			// Test second repertoire
			File external2 = new File(filePath2);
			if (external2.exists()) {
				LOGGER.info(" **** Event Handler - external 2.exists() ");
				try {
					properties.load(new FileInputStream(external2));
				} catch (FileNotFoundException e1) {
					LOGGER.info("** props getClassLoader().getResourceAsStream 2  properties.load e1 ",e1);
				} catch (IOException e2) {
					LOGGER.info("** props getClassLoader().getResourceAsStream 2 properties.load e2 ",e2);
				}  catch (Exception e) {
					LOGGER.info("** props getClassLoader().getResourceAsStream 2 properties.load e",e);
				}
			}else {
				LOGGER.info(" **** Event Handler - external2. NOT exists() ");
			}

			LOGGER.info("** Event Handler -properties.load ");

			try {
				serverURLFromConf	=	properties.getProperty("jms.server_url");
				usernameFromConf	=	properties.getProperty("jms.user_name");
				passwordFromConf	=	properties.getProperty("jms.password");
				queueNameFromConf	=	properties.getProperty("jms.default_destination_name");
				bonitaUsername		=	properties.getProperty("bonita.user_name");
				bonitaPassword		=	properties.getProperty("bonita.password");
				bonitaserverUrl		=	properties.getProperty("bonita.server_url");
				LOGGER.info("** props 	fin properties.load  serverURLFromConf "+serverURLFromConf);
				LOGGER.info("** props 	fin properties.load usernameFromConf "+usernameFromConf);
				LOGGER.info("** props 	fin properties.load passwordFromConf "+passwordFromConf);
				LOGGER.info("** props 	fin properties.load queueNameFromConf "+queueNameFromConf);
				LOGGER.info("** props 	fin properties.load bonitaUsername "+bonitaUsername);
				LOGGER.info("** props 	fin properties.load bonitaPassword "+bonitaPassword);
				LOGGER.info("** props 	fin properties.load bonitaserverUrl "+bonitaserverUrl);
			} catch (Exception e) {
				LOGGER.info("** props properties.load(  exception :",e);
			}

			try {
				//LOGGER.info("props jmsConfig : "+jmsConfig.getServer_url());
			} catch (Exception e) {
				LOGGER.info("** props jmsConfig.getServer_url() :",e);
			}

		} catch (Exception e) {
			LOGGER.info("** EventHandler properties exception : ",e);
		}

		LOGGER.info(" **** Event Handler - Emission event ");
		/*		String serverURLFromConf	=	"tcp://esb37int5.hm.dm.ad:7222";
		String usernameFromConf		=	"bpm_hpd_user";
		String passwordFromConf		=	"JGZ8cW6Cyxdvs!SQ";
		String queueNameFromConf	=	"queue.int.bna.datalab";
		*/

		try {
			JmsSenderService jmsSenderService = new JmsSenderService();
			jmsSenderService.setServerUrl(serverURLFromConf);
			jmsSenderService.setUserName(usernameFromConf);
			jmsSenderService.setPassword(passwordFromConf);

			EventHandlerMessage eventHandlerMessage = new EventHandlerMessage();
			eventHandlerMessage.setTypeEvent(event.getType());
			eventHandlerMessage.setIdDemande(processInstanceId);
			eventHandlerMessage.setDateEvent(LocalDateTime.now().toString());
			eventHandlerMessage.setFamille("");
			eventHandlerMessage.setBusinessDataType("");

			Map<String, String> settings = new HashMap<String, String>();
			settings.put("server.url", bonitaserverUrl);
			settings.put("application.name", "bonita");
			APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, settings);
			org.bonitasoft.engine.api.APIClient apiClient = new APIClient();
			apiClient.login(bonitaUsername, bonitaPassword);
			final ProcessAPI processAPI = apiClient.getProcessAPI();
			ProcessInstance processInstance = processAPI.getProcessInstance(processInstanceId);

			eventHandlerMessage.setTypeProcessus(processInstance.getName());

			jmsSenderService.sendTopicMessage(queueNameFromConf, eventHandlerMessage);
		} catch (Exception e) {
			LOGGER.info("Published isInterested 1 exception",e);

		} catch (Error e1) {
			LOGGER.info("Published isInterested 2 exception",e1);

		}
		LOGGER.info("*****  Event Handler Fin envoi message ");
		LOGGER.info("***** FIN Event Handler Bonita : execute ");
	}





	@Override
	public boolean isInterested(SEvent event) {
		LOGGER.info("**** Event Handler - isInterested");

		boolean isInterested = false;

		// Get the object associated with the event

		Object eventObject = event.getObject();
		// Check if the event is related to a task
		if (eventObject instanceof SFlowNodeInstance) {
			// Verify that the state of the task is ready. (4)
			SFlowNodeInstance flowNodeInstance = (SFlowNodeInstance) eventObject;
			LOGGER.info(" **** Event Handler -  eventObject getType v5 "+flowNodeInstance.getType());
			LOGGER.info(" **** Event Handler -  eventObject getStateId v5 "+flowNodeInstance.getStateId());

			isInterested = (flowNodeInstance.getType().equals(SFlowNodeType.USER_TASK) && flowNodeInstance.getStateId()==4); if(isInterested) return isInterested;
			isInterested = (flowNodeInstance.getType().equals(SFlowNodeType.END_EVENT) && flowNodeInstance.getStateId()==1); if(isInterested) return isInterested;
			isInterested = (flowNodeInstance.getType().equals(SFlowNodeType.START_EVENT) && flowNodeInstance.getStateId()==0);
			LOGGER.info(" **** Event Handler - isInterested "+isInterested);

			//	processInstanceId = flowNodeInstance.getParentProcessInstanceId();
			//	LOGGER.info(" **** Event Handler - processInstanceId "+processInstanceId);

		}

		LOGGER.info(" **** Event Handler - return  isInterested"+isInterested);
		LOGGER.info(" *********************");
		return isInterested;
	}

	@SuppressWarnings("unused")
	private TenantServiceAccessor getTenantServiceAccessor() throws SHandlerExecutionException {
		try {
			ServiceAccessorFactory serviceAccessorFactory = ServiceAccessorFactory.getInstance();
			return serviceAccessorFactory.createTenantServiceAccessor(tenantId);
		} catch (Exception e) {
			throw new SHandlerExecutionException(e.getMessage(), null);
		}
	}

	@Override
	public String getIdentifier() {
		return UUID.randomUUID().toString();
	}
}