package com.kunano.scansell_native.components.billing_component;

/* Copyright (c) 2012 Google Inc.
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

//import android.util.Base64;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Security-related methods. For a secure implementation, all of this code
 * should be implemented on a server that communicates with the
 * application on the device. For the sake of simplicity and clarity of this
 * example, this code is included here and is executed on the device. If you
 * must verify the purchases on the phone, you should obfuscate this code to
 * make it harder for an attacker to replace the code with stubs that treat all
 * purchases as verified.
 */
public class Security {
    public static Task<Boolean> verifyPurchase(String signedData, String signature) {
        FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();


        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("signedData", signedData);
        data.put("signature", signature);

        return mFunctions
                .getHttpsCallable("verifypurchase")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, Boolean>() {
                    @Override
                    public Boolean then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.

                        Object result = task.getResult().getData();

                        if (result instanceof  Boolean){
                            //System.out.println("Are they the same?: " + result);
                            return (boolean)result;
                        }else {
                            return false;
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("failed to call: " + e);
                    }
                });
    }
}