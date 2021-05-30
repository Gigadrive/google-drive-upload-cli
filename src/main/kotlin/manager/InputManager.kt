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

import com.gigadrivegroup.kotlincommons.feature.CommonsManager
import com.gigadrivegroup.kotlincommons.feature.inject

/** Manages processes for asking the user questions in input. */
public class InputManager : CommonsManager() {
    private val credentialsManager: CredentialsManager by inject()
    private val apiManager: GoogleAPIManager by inject()

    /**
     * Starts the process of asking the user about their Google project credentials.
     * @param force If true, the process will be started, even if not necessary.
     */
    public fun startSetupProcess(force: Boolean = false) {
        if (!force && credentialsManager.credentialsFile.exists()) {
            return
        }

        // introduction
        println("Hello, this is the first time setup.")
        println("Please create a Google Cloud project here: ")
        println("Then create your OAuth credentials:")
        println(
            "  Create credentials > Configure OAuth consent screen > Application type > TV and Limited Input Devices")
        println(" ")
        println("Afterwards enter your project's credentials.")
        println(" ")

        println("Enter your project's Client ID:")

        val clientId = readLine()!!

        println("Enter your project's Client Secret:")

        val clientSecret = readLine()!!

        println("Verifying...")

        val verifyResponse = apiManager.verifyDevice(clientId)

        println(" ")
        println(
            "Next you must authenticate with the Google account that should be used for uploading files.")
        println("Please visit ${verifyResponse.verificationUrl}")
        println("and enter the following code:")
        println(" ")
        println("   ${verifyResponse.userCode}")
        println("")
        println("Waiting for Google to respond...")

        var pollingResponse: GoogleAPIAuthorizationPollingResponse? = null

        while (pollingResponse == null) {
            pollingResponse =
                apiManager.pollAuthorization(clientId, clientSecret, verifyResponse.deviceCode)

            if (pollingResponse == null) Thread.sleep((verifyResponse.interval * 1000).toLong())
        }

        credentialsManager.setCredentials(
            Credentials(
                pollingResponse.accessToken,
                pollingResponse.refreshToken,
                System.currentTimeMillis() + (pollingResponse.expiresIn * 1000)))

        println(
            "Success! Your credentials have been stored here: ${credentialsManager.credentialsFile.absolutePath}")
        println("You can now start using this command line tool.")
    }
}
