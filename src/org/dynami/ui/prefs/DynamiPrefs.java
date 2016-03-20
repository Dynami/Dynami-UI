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
package org.dynami.ui.prefs;

import org.dynami.ui.prefs.data.BasicPrefs;
import org.dynami.ui.prefs.data.CommissionPrefs;
import org.dynami.ui.prefs.data.Prefs;
import org.dynami.ui.prefs.data.TracesPrefs;

public enum DynamiPrefs {
	Basic(new BasicPrefs()),
	Commissions(new CommissionPrefs()),
	Traces(new TracesPrefs());

	private final Prefs prefs;
	DynamiPrefs(Prefs prefs){
		this.prefs = prefs;
	}

	@SuppressWarnings("unchecked")
	public <T extends Prefs> T prefs(Class<T> clazz){
		return (T)prefs;
	}

	public Prefs  prefs(){
		return prefs;
	}
}
