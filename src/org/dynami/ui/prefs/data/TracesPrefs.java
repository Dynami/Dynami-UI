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

import javafx.scene.paint.Color;

@Prefs.Panel(name="Traces", description="Logging settings")
public class TracesPrefs extends Prefs {
	@Prefs.Parameter(
			name="Max rows",
			description="The max number of traces displayed",
			config=PrefsConstants.TRACES.MAX_ROWS,
			type=Prefs.Type.Integer,
			defaultValue="50")
	public Integer maxRows;

	@Prefs.Parameter(
			name="Debug",
			description="Debug traces color",
			config=PrefsConstants.TRACES.COLOR.DEBUG,
			type=Prefs.Type.Color,
			defaultValue="#D3D3D3")
	public Color debug;

	@Prefs.Parameter(
			name="Info",
			description="Info traces color",
			config=PrefsConstants.TRACES.COLOR.INFO,
			type=Prefs.Type.Color,
			defaultValue="#87CEFA")
	public Color info;

	@Prefs.Parameter(
			name="Warning",
			description="Warning traces color",
			config=PrefsConstants.TRACES.COLOR.WARN,
			type=Prefs.Type.Color,
			defaultValue="#FFA500")
	public Color warn;

	@Prefs.Parameter(
			name="Error",
			description="Error traces color",
			config=PrefsConstants.TRACES.COLOR.ERROR,
			type=Prefs.Type.Color,
			defaultValue="#FF4500")
	public Color error;
}
