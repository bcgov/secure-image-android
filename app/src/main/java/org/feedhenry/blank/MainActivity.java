/**
 * Copyright 2015 Red Hat, Inc., and individual contributors
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
package org.feedhenry.blank;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		init();
	}

	private void navigateTo(Fragment fragment) {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content, fragment)
				.commit();
	}

	private void init() {
		navigateTo(new InitFragment());
	}

	public void navigateToHome(String message) {
		Bundle args = new Bundle();
		args.putString(HomeFragment.MESSAGE, message);

		Fragment fragment = new HomeFragment();
		fragment.setArguments(args);

		navigateTo(fragment);
	}

}
