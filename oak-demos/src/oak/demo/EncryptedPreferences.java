/*
 * Copyright (c) 2011. WillowTree Apps, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package oak.demo;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import oak.app.CryptoSharedPreferences;


/**
 * User: mlake Date: 12/19/11 Time: 11:16 AM
 */
// START SNIPPET: encrypted_prefs
public class EncryptedPreferences extends CryptoSharedPreferences {

    public EncryptedPreferences(Context context, SharedPreferences delegate) {
        super(context, delegate);
    }

    /**
     * This should be replaced with a user input pass phrase or an externally
     * retrieved pass phrase if possible.
     *
     * @return
     */
    @Override
    protected char[] getSpecialCode() {
        return "THIS IS MY ENCRYPTING KEY PHRASE@@!".toCharArray();
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        return null;
    }
}
// END SNIPPET: encrypted_prefs