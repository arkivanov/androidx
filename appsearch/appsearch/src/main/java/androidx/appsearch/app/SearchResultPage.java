/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.appsearch.app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.util.Preconditions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a page of {@link SearchResult}s
 * @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class SearchResultPage {
    public static final String RESULTS_FIELD = "results";
    public static final String NEXT_PAGE_TOKEN_FIELD = "nextPageToken";
    private final long mNextPageToken;

    @Nullable
    private List<SearchResult> mResults;

    @NonNull
    private final Bundle mBundle;

    public SearchResultPage(@NonNull Bundle bundle) {
        mBundle = Preconditions.checkNotNull(bundle);
        mNextPageToken = mBundle.getLong(NEXT_PAGE_TOKEN_FIELD);
    }

    /** Returns the {@link Bundle} of this class. */
    @NonNull
    public Bundle getBundle() {
        return mBundle;
    }

    /** Returns the Token to get next {@link SearchResultPage}. */
    public long getNextPageToken() {
        return mNextPageToken;
    }

    /** Returns all {@link androidx.appsearch.app.SearchResult}s of this page */
    @NonNull
    @SuppressWarnings("deprecation")
    public List<SearchResult> getResults() {
        if (mResults == null) {
            ArrayList<Bundle> resultBundles = mBundle.getParcelableArrayList(RESULTS_FIELD);
            if (resultBundles == null) {
                mResults = Collections.emptyList();
            } else {
                mResults = new ArrayList<>(resultBundles.size());
                for (int i = 0; i < resultBundles.size(); i++) {
                    mResults.add(new SearchResult(resultBundles.get(i)));
                }
            }
        }
        return mResults;
    }
}
