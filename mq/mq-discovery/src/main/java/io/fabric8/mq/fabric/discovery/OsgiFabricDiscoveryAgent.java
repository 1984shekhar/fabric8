/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.mq.fabric.discovery;

import org.apache.activemq.transport.discovery.DiscoveryAgent;
import org.apache.curator.framework.CuratorFramework;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsgiFabricDiscoveryAgent extends FabricDiscoveryAgent implements ServiceTrackerCustomizer {
    private static final Logger LOG = LoggerFactory.getLogger(OsgiFabricDiscoveryAgent.class);

    ServiceTracker tracker;
    BundleContext context;

    public OsgiFabricDiscoveryAgent() {
        // Given we're a fragment, we need to use the host bundle context
        init(findBundleContextToUse());
    }

    // for testing
    public OsgiFabricDiscoveryAgent(BundleContext context) {
        init(context);
    }

    private void init(BundleContext context) {
        this.context = context;
        tracker = new ServiceTracker(context, CuratorFramework.class.getName(), this);
        tracker.open();
    }

    private BundleContext findBundleContextToUse() {
        // The osgi discovery is in a fragment attached to activemq
        // We're using our caller bundle
        Bundle activemqBundle = FrameworkUtil.getBundle(DiscoveryAgent.class);
        Class[] classCtx = new SecurityManagerEx().getClassContext();
        for (Class aClassCtx : classCtx) {
            Bundle bundle = FrameworkUtil.getBundle(aClassCtx);
            if (bundle != activemqBundle) {
                BundleContext context = bundle != null ? bundle.getBundleContext() : null;
                if (context != null) {
                    return context;
                }
            }
        }
        return null;
    }

    static class SecurityManagerEx extends SecurityManager
    {
        public Class[] getClassContext()
        {
            return super.getClassContext();
        }
    }

    @Override
    public Object addingService(ServiceReference serviceReference) {
        LOG.debug("tracker - addingService, this=" + this  + ", curator:" + getCurator());
        try {
            updateCurator((CuratorFramework) context.getService(serviceReference));
        } catch (Exception e) {
            LOG.error("Error on updateCurator on service addition with: " + serviceReference, e);
        }
        return curator;
    }

    @Override
    public void modifiedService(ServiceReference serviceReference, Object o) {
        LOG.debug("tracker - modifiedService, this=" + this);
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        LOG.debug("tracker - removedService, ref=" + o + ", this=" + this + ", curator:" + getCurator());
    }

    @Override
    public synchronized void stop() throws Exception {
        super.stop();

        if (tracker != null) {
            LOG.debug("closing tracker");
            tracker.close();
        }
    }
}
