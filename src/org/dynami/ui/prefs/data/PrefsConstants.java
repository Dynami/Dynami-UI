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

public class PrefsConstants {

	public static final class BASIC {
		public static final String STRATS_DIR = "basic_strategies-directory";
	}

	public static final class TIME_CHART {
		public static final String MAX_SAMPLE_SIZE = "time-chart_max-sample-size";
	}

	public static final class COMMISSION {
		public static final String PER_CONTRACT = "commission_per-contract";
	}

	public static final class TRACES {
		public static final String MAX_ROWS = "traces_max-rows";

		public static final class COLOR {
			public static final String INFO = "traces_color_info";
			public static final String DEBUG = "traces_color_debug";
			public static final String WARN = "traces_color_warning";
			public static final String ERROR = "traces_color_error";
		}
	}
}
