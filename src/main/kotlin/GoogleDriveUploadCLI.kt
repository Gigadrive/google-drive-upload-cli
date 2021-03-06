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

package com.gigadrivegroup.googledriveuploadcli

import com.gigadrivegroup.googledriveuploadcli.manager.CredentialsManager
import com.gigadrivegroup.googledriveuploadcli.manager.GoogleAPIManager
import com.gigadrivegroup.googledriveuploadcli.manager.InputManager
import com.gigadrivegroup.googledriveuploadcli.manager.UploadManager
import com.gigadrivegroup.kotlincommons.feature.CommonsManager
import com.gigadrivegroup.kotlincommons.feature.bind
import kotlin.system.exitProcess
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/** The main class for the upload cli. */
public class GoogleDriveUploadCLI(public val args: GoogleDriveUploadCLIArgs) {
    public fun start() {
        startKoin {
            printLogger(Level.NONE)
            modules()
        }

        bind(this)

        val logger = Logger(args.verbose)
        bind(logger)

        bind(GoogleAPIManager())
        bind(CredentialsManager(args.refreshToken))

        val uploadManager = UploadManager()
        bind(uploadManager)

        val inputManager = InputManager()
        bind(inputManager)

        Runtime.getRuntime()
            .addShutdownHook(
                object : Thread() {
                    override fun run() {
                        logger.debug("Shutting down.")
                        shutdown()
                    }
                })

        // start setup process if necessary
        inputManager.startSetupProcess(args.forceSetup)

        // start uploading
        if (!args.source.exists()) {
            logger.error("Source file could not be found.")
            exitProcess(1)
        }

        uploadManager.startUpload(args.source, args.destination, args.mimeType)
    }

    public fun shutdown() {
        CommonsManager.shutdown()
    }
}
