package org.camunda.bpm.extension.test.mockito;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.extension.test.mockito.DelegateExpressions.registerDelegateExpressionMocks;
import static org.camunda.bpm.extension.test.mockito.DelegateExpressions.verifyExecutionListenerMock;
import static org.camunda.bpm.extension.test.mockito.DelegateExpressions.verifyJavaDelegateMock;
import static org.camunda.bpm.extension.test.mockito.DelegateExpressions.verifyTaskListenerMock;
import static org.camunda.bpm.extension.test.mockito.MostUsefulProcessEngineConfiguration.mostUsefulProcessEngineConfiguration;

/**
 * If everything works as expected, the process can be deployed and executed without explicitly registering mocks for
 * the delegate, the execution- and the task-listener.
 *
 * @author Jan Galinski, Holisticon AG
 */
public class AutoMockProcessTest {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Rule
  public final ProcessEngineRule processEngineRule = new ProcessEngineRule(mostUsefulProcessEngineConfiguration().buildProcessEngine());

  @Test
  @Deployment(resources = "MockProcess.bpmn")
  public void register_mocks_for_all_listeners_and_delegates() throws Exception {
    registerDelegateExpressionMocks(getResource("MockProcess.bpmn"));

    final ProcessInstance processInstance = processEngineRule.getRuntimeService().startProcessInstanceByKey("process_mock_dummy");

    assertThat(processEngineRule.getTaskService().createTaskQuery().processInstanceId(processInstance.getId()).singleResult()).isNotNull();

    verifyTaskListenerMock("verifyData").executed();
    verifyExecutionListenerMock("startProcess").executed();
    verifyJavaDelegateMock("loadData").executed();
    verifyExecutionListenerMock("beforeLoadData").executed();
  }



}
