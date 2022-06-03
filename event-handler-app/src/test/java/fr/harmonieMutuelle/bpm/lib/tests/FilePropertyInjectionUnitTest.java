//package fr.harmonieMutuelle.bpm.lib.tests;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@TestPropertySource("/src/test/resources/application-dev.properties")
//public class FilePropertyInjectionUnitTest {
//
//    @Value("${jms.server.server-url}")
//    private String serverURL;
//
//    @Test
//    public void whenFilePropertyProvided_thenProperlyInjected() {
//    	org.junit.Assert.assertTrue(">>>>>>>>>>>>>>>  OK ","tcp://esb37dev4.hm.dm.ad:7222".equals(serverURL));
//    }
//}