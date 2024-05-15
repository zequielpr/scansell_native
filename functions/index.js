/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */


/**
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// [START imports]
// Dependencies for callable functions.
const {onCall, HttpsError} = require("firebase-functions/v2/https");
const {logger} = require("firebase-functions/v2");
//const sanitizer = require('sanitize-html');


//Verify purchase
exports.verifypurchase = onCall((request) => {

  const PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwORzFe1iIxWpQabICxDLvWL3nSKWz+VSVGqtT0QCtFGdgWoNBj0+s6KrxtL52LA5VCFghd1mjaLlQPeTnP2Vg1U19dTq8r+4RIvyyt+yLSmacW/8MJ8hG7iBDaVYz6QRn2SeWBPKAu8U4EJn9G7u38QiWQZ+xRIdCWJOB6HPwdCuXEPaybc4eG1YFgawY3NTtwyMREYQ2tWADY3i68KuZNeJK341oVIIfPeBMZ351YN+oyHGhkMg8CyodcCjL9zcjzyGl+RkT0TdGQGEuQ5oKDKBcYwMlqmSJVJ41rApjMTM+PNZbRTXG+zk4orETNOkQP7hrv+V4pPjTy+8DycplQIDAQAB";
  const SIGNED_DATA = request.data.signedData;
  const SIGNATURE_DATA = request.data.signature;

  // Checking that the user is authenticated.
  if (!request.auth) {
    // Throwing an HttpsError so that the client gets the error details.
    throw new HttpsError("failed-precondition", "The function must be " +
            "called while authenticated.");
  }
  // [END v2messageHttpsErrors]

  // [START v2returnMessageAsync]
  // Saving the new message to the Realtime Database.
  //const sanitizedSignedData = sanitizer.sanitizeText(SIGNED_DATA); // Sanitize signedData
  //const sanitizedSignature = sanitizer.sanitizeText(SIGNATURE_DATA); // Sanitize signature.

  if(SIGNED_DATA === SIGNATURE_DATA){
    return {text: true};
  }else{
    return {text: false};
  }
});