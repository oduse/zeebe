/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.model.bpmn.validation;

import static io.zeebe.model.bpmn.validation.ExpectedValidationResult.expect;
import static java.util.Collections.singletonList;

import io.zeebe.model.bpmn.Bpmn;
import io.zeebe.model.bpmn.instance.zeebe.ZeebeCalledElement;
import org.junit.runners.Parameterized.Parameters;

public class ZeebeCallActivityTest extends AbstractZeebeValidationTest {

  @Parameters(name = "{index}: {1}")
  public static Object[][] parameters() {
    return new Object[][] {
      {
        Bpmn.createExecutableProcess("process").startEvent().callActivity("call").done(),
        singletonList(
            expect("call", "Must have exactly one 'zeebe:calledElement' extension element"))
      },
      {
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .callActivity("call", c -> c.zeebeProcessId(null))
            .endEvent()
            .done(),
        singletonList(
            expect(ZeebeCalledElement.class, "Attribute 'processId' must be present and not empty"))
      },
      {
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .callActivity("call", c -> c.zeebeProcessId("x"))
            .endEvent()
            .done(),
        valid()
      },
      {
        Bpmn.createExecutableProcess("process")
            .startEvent()
            .callActivity("call", c -> c.zeebeProcessIdExpression("y"))
            .endEvent()
            .done(),
        valid()
      },
    };
  }
}
