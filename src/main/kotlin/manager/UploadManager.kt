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

import com.gigadrivegroup.googledriveuploadcli.Logger
import com.gigadrivegroup.googledriveuploadcli.ResumableUpload
import com.gigadrivegroup.kotlincommons.feature.CommonsManager
import com.gigadrivegroup.kotlincommons.feature.inject
import java.io.File
import java.net.URLConnection

/** Manages the uploading of files to Google Drive. */
public class UploadManager : CommonsManager() {
    private val credentialsManager: CredentialsManager by inject()
    private val logger: Logger by inject()

    /**
     * Uploads a [source] file to the [destination] folder.
     * @throws Exception Throws an exception when something goes wrong with the upload.
     */
    public fun startUpload(source: File, destination: String) {
        val credentials = credentialsManager.getCredentials() ?: return

        logger.info("Starting upload. This may take a while.")

        val destinationPrefix =
            if (destination == ".") {
                ""
            } else {
                destination + (if (destination.endsWith("/")) "" else "/")
            }

        val googleDriveFile = com.google.api.services.drive.model.File()
        googleDriveFile.name = destinationPrefix + source.name
        googleDriveFile.mimeType =
            URLConnection.guessContentTypeFromName(source.name) ?: "text/plain"
        googleDriveFile.setSize(source.length())

        ResumableUpload.uploadFile(credentials.toGoogleCredentials(), googleDriveFile, source)

        logger.info("Done.")
    }
}
