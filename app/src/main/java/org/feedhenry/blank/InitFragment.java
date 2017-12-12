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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.feedhenry.sdk.FH;
import com.feedhenry.sdk.FHActCallback;
import com.feedhenry.sdk.FHResponse;

public class InitFragment extends Fragment {

	private static final String TAG = InitFragment.class.getName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.init_fragment, null);

		FH.init(getActivity(), new FHActCallback() {
			@Override
			public void success(FHResponse fhResponse) {
				Log.d(TAG, "init - success");
				MainActivity activity = (MainActivity) getActivity();
				activity.navigateToHome("Yay, this app is ready to go!");
				//NOTE: other FH methods can only be called after FH.init succeeds
			}

			@Override
			public void fail(FHResponse fhResponse) {
				Log.d(TAG, "init - fail");
				Log.e(TAG, fhResponse.getErrorMessage(), fhResponse.getError());
				MainActivity activity = (MainActivity) getActivity();
				activity.navigateToHome("Oops, an error occurred while processing FH.init");
			}
		});

		return view;
	}

}

