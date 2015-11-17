/**
 * The MIT License
 * Copyright (c) 2015 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ee.ria.xroad.common.monitoring;

import java.util.Date;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

import ee.ria.xroad.common.SystemProperties;

/**
 * Default implementation of the monitor agent interface.
 */
public class DefaultMonitorAgentImpl implements MonitorAgentProvider {

    private final ActorSelection actor;

    /**
     * Constructs a monitor agent that uses the given actor system.
     * @param actorSystem actor system to be used by this monitor agent
     */
    public DefaultMonitorAgentImpl(ActorSystem actorSystem) {
        String actorName = getActorName();
        actor = actorName != null
                ? actorSystem.actorSelection(actorName)
                : null;
    }

    @Override
    public void success(MessageInfo messageInfo, Date startTime, Date endTime) {
        tell(new SuccessfulMessage(messageInfo, startTime, endTime));
    }

    @Override
    public void serverProxyFailed(MessageInfo messageInfo) {
        tell(new ServerProxyFailed(messageInfo));
    }

    @Override
    public void failure(MessageInfo messageInfo, String faultCode,
            String faultMessage) {
        tell(new FaultInfo(messageInfo, faultCode, faultMessage));
    }

    private void tell(Object message) {
        if (actor != null) {
            actor.tell(message, ActorRef.noSender());
        }
    }

    private static String getActorName() {
        return System.getProperty(SystemProperties.MONITORING_AGENT_URI);
    }
}
