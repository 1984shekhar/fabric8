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
package io.fabric8.commands;

import static io.fabric8.utils.FabricValidations.validateContainerName;

import io.fabric8.api.*;
import io.fabric8.boot.commands.support.FabricCommand;

import java.util.Arrays;
import java.util.Collection;

import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;

@Command(name = ContainerDelete.FUNCTION_VALUE, scope = ContainerDelete.SCOPE_VALUE, description = ContainerDelete.DESCRIPTION, detailedDescription = "classpath:containerDelete.txt")
public class ContainerDeleteAction extends AbstractContainerLifecycleAction {

    protected final RuntimeProperties runtimeProperties;
    protected final ZooKeeperClusterService clusterService;

    @Option(name = "-r", aliases = {"--recursive"}, multiValued = false, required = false, description = "Recursively stops and deletes all child containers")
    protected boolean recursive = false;

    ContainerDeleteAction(FabricService fabricService, ZooKeeperClusterService zooKeeperClusterService, RuntimeProperties runtimeProperties) {
        super(fabricService);
        this.clusterService = zooKeeperClusterService;
        this.runtimeProperties = runtimeProperties;
    }

    @Override
    protected Object doExecute() throws Exception {
        Collection<String> expandedNames = super.expandGlobNames(containers);
        for (String containerName : expandedNames) {
            validateContainerName(containerName);
            if (FabricCommand.isPartOfEnsemble(fabricService, containerName) && !force) {
                System.out.println("Container is part of the ensemble. If you still want to delete it, please use --force option.");
                return null;
            }

            String runtimeIdentity = runtimeProperties.getRuntimeIdentity();
            if (containerName.equals(runtimeIdentity) && !force) {
                System.out.println("You shouldn't delete current container. If you still want to delete it, please use --force option.");
                return null;
            }

            Container found = FabricCommand.getContainerIfExists(fabricService, containerName);
            if (found != null) {
                applyUpdatedCredentials(found);
                if(found.isEnsembleServer()){
                    CreateEnsembleOptions.Builder<?> builder = CreateEnsembleOptions.builder();
                    String password = fabricService.getZookeeperPassword();
                    CreateEnsembleOptions options = builder.zookeeperPassword(password).force(true).build();
                    clusterService.removeFromCluster(Arrays.asList(found.getId()), options);
                }
                if (recursive || force) {
                    for (Container child : found.getChildren()) {
                        try{
                            child.destroy(force);
                        } catch (FabricException e){
                            handleException(e, child);
                        }
                    }
                }
                try{
                    found.destroy(force);
                } catch (FabricException e){
                    handleException(e, found);
                }

            } else if (force) {
                //We also want to try and delete any leftover entries
                fabricService.adapt(DataStore.class).deleteContainer(fabricService, containerName);
            }
        }
        return null;
    }

    protected void handleException(FabricException e, Container container) {
        if(force && e.getMessage().contains("not managed")){
            String message = "Container [" + container.getId() + "] has not be provisioned from within Fabric. " +
                    "All the references to it have been deleted from the central registry but " +
                    "you might still be required to manually stop and delete manually the remote process instance.";
            throw new FabricException(message);
        } else{
            throw e;
        }
    }

}
