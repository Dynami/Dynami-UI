/*
 * Copyright 2015 Alessandro Atria - a.atria@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dynami.ui.prefs.data;

import java.io.File;

@Prefs.Panel(name="Basic", description="Basic settings mandatory to run properly Dynami")
public class BasicPrefs extends Prefs {

	@Prefs.Parameter(
			name="Strategy Repository",
			description="System directory where strategy-jars are deployed",
			config=PrefsConstants.BASIC.STRATS_DIR,
			type=Prefs.Type.Directory,
			defaultValue="D:/dynami-repo/Dynami-UI/resources/")
	public File strategyDirectory;

	@Prefs.Parameter(
			name="Timed Charts Bars",
			description="The max number of bars displayed in time charts",
			config=PrefsConstants.TIME_CHART.MAX_SAMPLE_SIZE,
			type=Prefs.Type.Integer,
			defaultValue="100")
	public Integer maxBars;

}
