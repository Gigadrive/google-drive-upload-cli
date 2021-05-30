/*
 * Copyright (c) 2021 Gigadrive UG, Mehdi Baaboura
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * https://gigadrivegroup.com
 * https://github.com/Gigadrive/google-drive-upload-cli
 */

package com.gigadrivegroup.googledriveuploadcli.manager

import com.gigadrivegroup.googledriveuploadcli.GSON
import com.gigadrivegroup.kotlincommons.feature.CommonsManager
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.google.gson.JsonParser

/** A manager used for interacting with the Google API. */
public class GoogleAPIManager : CommonsManager() {
    /**
     * Starts the authorization process with the specified [clientId] and [scope].
     * @throws Exception Throws an exception if the response is not as expected.
     */
    @Throws(Exception::class)
    public fun verifyDevice(
        clientId: String,
        scope: String = "https://www.googleapis.com/auth/drive.file"
    ): GoogleAPIDeviceCodeResponse {
        val (request, response, result) =
            addHeaders(
                    "https://oauth2.googleapis.com/device/code"
                        .httpPost()
                        .body(GSON.toJson(listOf("client_id" to clientId, "scope" to scope))))
                .responseString()

        when (result) {
            is Result.Failure -> throw Exception("Failed to get device code.")
            is Result.Success -> {
                val responseObject =
                    (JsonParser()).parse(response.body().asString(null)).asJsonObject

                if (responseObject.has("error_code")) {
                    throw Exception(
                        "Google API returned an error: ${responseObject.get("error_code").asString}.")
                }

                if (!responseObject.has("device_code") ||
                    !responseObject.has("expires_in") ||
                    !responseObject.has("interval") ||
                    !responseObject.has("user_code") ||
                    !responseObject.has("verification_url")) {
                    throw Exception("Google returned an invalid response.")
                }

                return GSON.fromJson(responseObject, GoogleAPIDeviceCodeResponse::class.java)
            }
        }
    }

    /** Adds the necessary headers to a [request]. */
    private fun addHeaders(request: Request): Request {
        return request.appendHeader(
            "User-Agent" to
                "google-drive-upload-cli (https://github.com/Gigadrive/google-drive-upload-cli)",
            "Content-Type" to "application/json")
    }
}

/**
 * The response provided by the Google API when requesting a device code.
 * https://developers.google.com/identity/protocols/oauth2/limited-input-device#step-2:-handle-the-authorization-server-response
 */
public class GoogleAPIDeviceCodeResponse(
    /**
     * A value that Google uniquely assigns to identify the device that runs the app requesting
     * authorization. The user will be authorizing that device from another device with richer input
     * capabilities. For example, a user might use a laptop or mobile phone to authorize an app
     * running on a TV. In this case, the [deviceCode] identifies the TV.
     *
     * This code lets the device running the app securely determine whether the user has granted or
     * denied access.
     */
    public val deviceCode: String,

    /**
     * A case-sensitive value that identifies to Google the scopes that the application is
     * requesting access to. Your user interface will instruct the user to enter this value on a
     * separate device with richer input capabilities. Google then uses the value to display the
     * correct set of scopes when prompting the user to grant access to your application.
     */
    public val userCode: String,

    /**
     * The length of time, in seconds, that the [deviceCode] and [userCode] are valid. If, in that
     * time, the user doesn't complete the authorization flow and your device doesn't also poll to
     * retrieve information about the user's decision, you might need to restart this process from
     * step 1.
     */
    public val expiresIn: Long,

    /**
     * The length of time, in seconds, that your device should wait between polling requests. For
     * example, if the value is 5, your device should send a polling request to Google's
     * authorization server every five seconds. See step 3 for more details.
     */
    public val interval: Int,

    /**
     * A URL that the user must navigate to, on a separate device, to enter the user_code and grant
     * or deny access to your application. Your user interface will also display this value.
     */
    public val verificationUrl: String
)
