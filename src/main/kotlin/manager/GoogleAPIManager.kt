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
     *
     * https://developers.google.com/identity/protocols/oauth2/limited-input-device#step-1:-request-device-and-user-codes
     */
    @Throws(Exception::class)
    public fun verifyDevice(
        clientId: String,
        scope: String = "https://www.googleapis.com/auth/drive.file"
    ): GoogleAPIDeviceCodeResponse {
        val (_, response, result) =
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

                if (responseObject.has("error") && responseObject.has("error_description")) {
                    throw Exception(
                        "Google API returned an error (${responseObject.get("error").asString}): ${responseObject.get("error_description").asString}.")
                }

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

    /**
     * Polls authorization details when the user is opening the Google OAuth page. Returns null if
     * the user has not yet authorized the application.
     *
     * @throws Exception Throws an exception if the response is not as expected.
     *
     * @param clientId The client ID for your application. You can find this value in the API
     * Console Credentials page.
     * @param clientSecret The client secret for the provided client_id. You can find this value in
     * the API Console Credentials page.
     * @param deviceCode The device_code returned by the authorization server in
     * [GoogleAPIDeviceCodeResponse.deviceCode].
     * @param grantType Set this value to urn:ietf:params:oauth:grant-type:device_code.
     *
     * https://developers.google.com/identity/protocols/oauth2/limited-input-device#step-4:-poll-googles-authorization-server
     */
    @Throws(Exception::class)
    public fun pollAuthorization(
        clientId: String,
        clientSecret: String,
        deviceCode: String,
        grantType: String = "urn:ietf:params:oauth:grant-type:device_code"
    ): GoogleAPIAuthorizationPollingResponse? {
        val (_, response, result) =
            addHeaders(
                    "https://oauth2.googleapis.com/token"
                        .httpPost()
                        .body(
                            GSON.toJson(
                                listOf(
                                    "client_id" to clientId,
                                    "client_secret" to clientSecret,
                                    "device_code" to deviceCode,
                                    "grant_type" to grantType))))
                .responseString()

        when (result) {
            is Result.Failure -> throw Exception("Failed to poll authorization.")
            is Result.Success -> {
                val responseObject =
                    (JsonParser()).parse(response.body().asString(null)).asJsonObject

                if (responseObject.has("error") && responseObject.has("error_description")) {
                    val error = responseObject.get("error").asString

                    // user pending or requesting too quickly
                    if (error === "authorization_pending" || error === "slow_down") {
                        return null
                    }

                    throw Exception(
                        "Google API returned an error (${error}): ${responseObject.get("error_description").asString}.")
                }

                if (responseObject.has("error_code")) {
                    throw Exception(
                        "Google API returned an error: ${responseObject.get("error_code").asString}.")
                }

                if (!responseObject.has("access_token") ||
                    !responseObject.has("expires_in") ||
                    !responseObject.has("scope") ||
                    !responseObject.has("token_type") ||
                    !responseObject.has("refresh_token")) {
                    throw Exception("Google returned an invalid response.")
                }

                return GSON.fromJson(
                    responseObject, GoogleAPIAuthorizationPollingResponse::class.java)
            }
        }
    }

    /**
     * Requests a new access token if the current one is expired.
     *
     * @throws Exception Throws an exception if the response is not as expected.
     *
     * @param clientId The client ID obtained from the API Console.
     * @param clientSecret The client secret obtained from the API Console.
     * @param refreshToken The refresh token returned from the authorization code exchange.
     * @param grantType As defined in the OAuth 2.0 specification, this field's value must be set to
     * refresh_token.
     *
     * https://developers.google.com/identity/protocols/oauth2/limited-input-device#offline
     */
    @Throws(Exception::class)
    public fun refreshToken(
        clientId: String,
        clientSecret: String,
        refreshToken: String,
        grantType: String = "refresh_token"
    ): GoogleAPITokenRefreshResponse {
        val (_, response, result) =
            addHeaders(
                    "https://oauth2.googleapis.com/token"
                        .httpPost()
                        .body(
                            GSON.toJson(
                                listOf(
                                    "client_id" to clientId,
                                    "client_secret" to clientSecret,
                                    "grant_type" to grantType,
                                    "refresh_token" to refreshToken))))
                .responseString()

        when (result) {
            is Result.Failure -> throw Exception("Failed to refresh access token.")
            is Result.Success -> {
                val responseObject =
                    (JsonParser()).parse(response.body().asString(null)).asJsonObject

                if (responseObject.has("error") && responseObject.has("error_description")) {
                    throw Exception(
                        "Google API returned an error (${responseObject.get("error").asString}): ${responseObject.get("error_description").asString}.")
                }

                if (responseObject.has("error_code")) {
                    throw Exception(
                        "Google API returned an error: ${responseObject.get("error_code").asString}.")
                }

                if (!responseObject.has("access_token") ||
                    !responseObject.has("expires_in") ||
                    !responseObject.has("scope") ||
                    !responseObject.has("token_type")) {
                    throw Exception("Google returned an invalid response.")
                }

                return GSON.fromJson(responseObject, GoogleAPITokenRefreshResponse::class.java)
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

/**
 * The response provided by the Google API when polling authorization.
 * https://developers.google.com/identity/protocols/oauth2/limited-input-device#step-6:-handle-responses-to-polling-requests
 */
public class GoogleAPIAuthorizationPollingResponse(
    /** The token that your application sends to authorize a Google API request. */
    public val accessToken: String,

    /** The remaining lifetime of the access token in seconds. */
    public val expiresIn: Long,

    /**
     * A token that you can use to obtain a new access token. Refresh tokens are valid until the
     * user revokes access. Note that refresh tokens are always returned for devices.
     */
    public val refreshToken: String,

    /**
     * The scopes of access granted by the access_token expressed as a list of space-delimited,
     * case-sensitive strings.
     */
    public val scope: String,

    /** The type of token returned. At this time, this field's value is always set to Bearer. */
    public val tokenType: String
)

/**
 * The response provided by the Google API when refreshing a token.
 * https://developers.google.com/identity/protocols/oauth2/limited-input-device#offline
 */
public class GoogleAPITokenRefreshResponse(
    /** The token that your application sends to authorize a Google API request. */
    public val accessToken: String,

    /** The remaining lifetime of the access token in seconds. */
    public val expiresIn: Long,

    /**
     * The scopes of access granted by the access_token expressed as a list of space-delimited,
     * case-sensitive strings.
     */
    public val scope: String,

    /** The type of token returned. At this time, this field's value is always set to Bearer. */
    public val tokenType: String
)
