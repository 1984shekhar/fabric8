/**
 *  Copyright 2005-2015 Red Hat, Inc.
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
package io.fabric8.patch.management;

/**
 * Command object to update a bundle with <code>symbolicName</code> from previous version:location to new
 * version:location.
 */
public class BundleUpdate {

    private final String symbolicName;

    private final String previousVersion;
    private final String previousLocation;

    private final String newVersion;
    private final String newLocation;

    public BundleUpdate(String symbolicName, String newVersion, String newLocation, String previousVersion, String previousLocation) {
        this.symbolicName = symbolicName;
        this.newVersion = newVersion;
        this.newLocation = newLocation;
        this.previousVersion = previousVersion;
        this.previousLocation = previousLocation;
    }

    public String getSymbolicName() {
        return symbolicName;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public String getNewLocation() {
        return newLocation;
    }

    public String getPreviousVersion() {
        return previousVersion;
    }

    public String getPreviousLocation() {
        return previousLocation;
    }

}
