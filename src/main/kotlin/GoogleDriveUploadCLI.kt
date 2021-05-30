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
import com.gigadrivegroup.kotlincommons.feature.CommonsManager
import com.gigadrivegroup.kotlincommons.feature.bind
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/** The main class for the upload cli. */
public class GoogleDriveUploadCLI {
    public fun start() {
        startKoin {
            printLogger(Level.NONE)
            modules()
        }

        bind(this)
        bind(GoogleAPIManager())
        bind(CredentialsManager())

        Runtime.getRuntime()
            .addShutdownHook(
                object : Thread() {
                    override fun run() {
                        shutdown()
                    }
                })
    }

    public fun shutdown() {
        CommonsManager.shutdown()
    }
}
