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
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/** Loads and manages the [Credentials] used for interacting with the Google API. */
public class CredentialsManager : CommonsManager() {
    /** The currently loaded [Credentials]. */
    private lateinit var credentials: Credentials

    /** The [File] that stores the [Credentials] used for interacting with the Google API. */
    private val credentialsFile: File =
        File(System.getProperty("user.dir") + File.separator + "/gdriveupload.credentials")

    init {
        loadCredentials()
    }

    /** The currently loaded [Credentials]. */
    public fun getCredentials(): Credentials = credentials

    /** Sets the currently loaded [Credentials] to the passed [credentials] instance. */
    public fun setCredentials(credentials: Credentials) {
        this.credentials = credentials
        saveCredentials()
    }

    /**
     * Loads the [Credentials] from the [credentialsFile].
     * @throws Exception Throws an exception if loading the [credentialsFile] fails.
     */
    @Throws(Exception::class)
    public fun loadCredentials() {
        this.credentials =
            GSON.fromJson(JsonReader(FileReader(credentialsFile)), Credentials::class.java)
    }

    /**
     * Saves the [Credentials] to the [credentialsFile].
     * @throws Exception Throws an exception if saving the [credentialsFile] fails.
     */
    @Throws(Exception::class)
    public fun saveCredentials() {
        val writer = FileWriter(credentialsFile)

        GSON.toJson(this.credentials, writer)

        writer.flush()
        writer.close()
    }
}

/** Represents the Google API credentials stored in a file. */
public class Credentials(
    /** The OAuth access token used for interacting with the Google API. */
    public val accessToken: String,

    /** The OAuth refresh token used for refreshing the access token. */
    public val refreshToken: String,

    /** The UNIX timestamp when the access token expires and needs refreshing. */
    public val expiresAt: Long
)
