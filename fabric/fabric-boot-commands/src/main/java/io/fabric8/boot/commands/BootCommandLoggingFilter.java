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
package io.fabric8.boot.commands;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.karaf.shell.console.CommandLoggingFilter;
import org.apache.karaf.shell.console.RegexCommandLoggingFilter;

/**
 * Used to filter out passwords from command logging.
 */
@Component(immediate = true)
@Service({ CommandLoggingFilter.class })
public class BootCommandLoggingFilter extends RegexCommandLoggingFilter {
    public BootCommandLoggingFilter() {
        addCommandOption("--zookeeper-password", CreateCommand.FUNCTION_VALUE, JoinCommand.FUNCTION_VALUE);
        addCommandOption("--new-user-password", CreateCommand.FUNCTION_VALUE);
        addCommandOption("--external-git-password", CreateCommand.FUNCTION_VALUE);
    }
}
