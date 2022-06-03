package fr.harmonieMutuelle.bpm.lib.events;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.bonitasoft.engine.api.APIClient;
import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.data.DataInstance;
import org.bonitasoft.engine.bpm.data.DataNotFoundException;
import org.bonitasoft.engine.core.process.definition.model.SFlowNodeType;
import org.bonitasoft.engine.core.process.instance.api.ActivityInstanceService;
import org.bonitasoft.engine.core.process.instance.model.SFlowNodeInstance;
import org.bonitasoft.engine.core.process.instance.model.SHumanTaskInstance;
import org.bonitasoft.engine.events.model.SEvent;
import org.bonitasoft.engine.events.model.SHandler;
import org.bonitasoft.engine.events.model.SHandlerExecutionException;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.platform.LoginException;
import org.bonitasoft.engine.search.SearchOptionsBuilder;
import org.bonitasoft.engine.search.SearchResult;
import org.bonitasoft.engine.search.descriptor.SearchEntitiesDescriptor;
import org.bonitasoft.engine.search.descriptor.SearchUserDescriptor;
import org.bonitasoft.engine.search.identity.SearchUsersWhoCanExecutePendingHumanTaskDeploymentInfo;
import org.bonitasoft.engine.service.TenantServiceAccessor;
import org.bonitasoft.engine.service.impl.ServiceAccessorFactory;
import org.codehaus.groovy.tools.groovydoc.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;

import fr.harmonieMutuelle.bpm.lib.jms.config.JmsConfig;
import fr.harmonieMutuelle.bpm.lib.jms.model.EventHandlerMessage;
import fr.harmonieMutuelle.bpm.lib.jms.service.JmsSenderService;

@SuppressWarnings("serial")
@ComponentScan({"fr.harmonieMutuelle.bpm.lib.events","fr.harmonieMutuelle.bpm.lib.jms.config"})
public class EventHandler implements SHandler<SEvent> {

	private static Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

	long processInstanceId = 0L;

	@Autowired
	private JmsConfig jmsConfig;

	public EventHandler(long tenantId) {
		this.tenantId = tenantId;
	}

	public long tenantId;

	@Override
	public void execute(SEvent event) {
		LOGGER.info("Event Handler Bonita : executing event {}", event.getType());


		SHumanTaskInstance sHumanTaskInstance = (SHumanTaskInstance) event.getObject();
		LOGGER.warn("Event Handler Bonita : Start for the task n°" + sHumanTaskInstance.getId() + ", " + sHumanTaskInstance.getName() );
		processInstanceId = sHumanTaskInstance.getParentProcessInstanceId();

		//getBusiennData(processInstanceId);
		Long humanTaskInstanceId = sHumanTaskInstance.getId();
		TenantServiceAccessor tenantAccessor;
		try {
			tenantAccessor = getTenantServiceAccessor();

			final ActivityInstanceService activityInstanceService = tenantAccessor.getActivityInstanceService();
			final SearchEntitiesDescriptor searchEntitiesDescriptor = tenantAccessor.getSearchEntitiesDescriptor();
			final SearchUserDescriptor searchDescriptor = searchEntitiesDescriptor.getSearchUserDescriptor();
			SearchOptionsBuilder searchOptionBuilder = new SearchOptionsBuilder(0,10);
			// Build a searcher that returns a humanTask candidates
			final SearchUsersWhoCanExecutePendingHumanTaskDeploymentInfo searcher = 
					new	SearchUsersWhoCanExecutePendingHumanTaskDeploymentInfo(humanTaskInstanceId, activityInstanceService, searchDescriptor,searchOptionBuilder.done());
			try{
				searcher.execute();
				SearchResult<User> users = searcher.getResult();
				List<User> userList = users.getResult();
				for(User user : userList){
					LOGGER.info("Event Handler Bonita: user candidate n°" + user.getId() + "," + user.getUserName());
				}
			} catch (Exception e) {
				LOGGER.error("Event Handler Bonita e : Error in Event Handler " + e);
			}

		} catch (SHandlerExecutionException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("Event Handler Bonita e1 : Error in Event Handler " + e1);
		}
	}

	@SuppressWarnings("unused")
	private void getBusiennData(long processInstanceId) {
		final APIClient apiClient = new APIClient();
		try {
			apiClient.login("meharzi-a", "bpm");
		} catch (LoginException e1) {
			e1.printStackTrace();
		}
		final ProcessAPI processAPI = apiClient.getProcessAPI();
		try {
			DataInstance dataInstance = processAPI.getProcessDataInstance("prestationAdherent", processInstanceId);
			dataInstance.getValue();
		} catch (DataNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public boolean isInterested(SEvent event) {
		Properties properties = new Properties();
		File external = new File("conf\\event-handler.properties");
		if (external.exists())
			try {
				properties.load(new FileInputStream(external));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		else
			try {
				properties.load(Main.class.getClassLoader().getResourceAsStream("conf\\event-handler.properties"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
/*		        .getResourceAsStream("application.properties");

		try {
			properties.load(inputStream);
			LOGGER.info("props 1 : "+properties.getProperty("jms.server_url"));
			LOGGER.info("props 2 : "+jmsConfig.getServer_url());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.info("props exception : " +e.getMessage());
		}
	*/	
		String serverURL	=	properties.getProperty("jms.server_url");
		String username		=	properties.getProperty("jms.user_name");
		String password		=	properties.getProperty("jms.password");
		String queueName	=	properties.getProperty("jms.default_destination_name");
		
//		String serverURL	=	jmsConfig.getServer_url();
//		String username		=	jmsConfig.getServer_url();
//		String password		=	jmsConfig.getServer_url();
//		String queueName	=	jmsConfig.getDefault_destination_name();
		
		LOGGER.info("Event Handler Bonita - event {} - asks if we are interested in handling this event instance",event.getType());
		boolean isInterested = false;
		// Get the object associated with the event
		Object eventObject = event.getObject();
		// Check if the event is related to a task
		if (eventObject instanceof SFlowNodeInstance) {
			// Verify that the state of the task is ready. (4)
			SFlowNodeInstance flowNodeInstance = (SFlowNodeInstance) eventObject;
			isInterested = (flowNodeInstance.getType().equals(SFlowNodeType.USER_TASK) && flowNodeInstance.getStateId()==4);
			processInstanceId = flowNodeInstance.getParentProcessInstanceId();
		}

		EventHandlerMessage eventHandlerMessage = new EventHandlerMessage();
		eventHandlerMessage.setTypeEvent(event.getType());
		eventHandlerMessage.setIdDemande(processInstanceId);
		eventHandlerMessage.setDateEvent(LocalDateTime.now().toString());
		
		JmsSenderService jmsSenderService = new JmsSenderService();
		jmsSenderService.setServerUrl(serverURL);
		jmsSenderService.setUserName(username);
		jmsSenderService.setPassword(password);

		jmsSenderService.sendTopicMessage(queueName, eventHandlerMessage);
		
		return isInterested;
	}

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
