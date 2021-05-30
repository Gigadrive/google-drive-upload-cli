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

import com.xenomachina.argparser.ArgParser
import java.io.File

/** Stores the passed command arguments. */
public class GoogleDriveUploadCLIArgs(parser: ArgParser) {
    /** Whether or not to print debug information. */
    public val verbose: Boolean by parser.flagging(
        "-v", "--verbose", help = "Prints debug information.")

    /** Whether or not to force the first-time setup. */
    public val forceSetup: Boolean by parser.flagging(
        "-f",
        "--force-setup",
        help = "Forces the first-time setup, even if credentials already exist.")

    /** Whether or not to force the token refreshing. */
    public val refreshToken: Boolean by parser.flagging(
        "-r", "--refresh-token", help = "Forces the Google API access token to be refreshed.")

    /** Path to the local file that should be uploaded. */
    public val source: File by parser.positional(
        "SOURCE", help = "Path to the local file that should be uploaded.") { File(this) }

    /** The path of the destination folder on Google Drive. */
    public val destination: String by parser.positional(
        "DEST",
        help =
            "The path of the destination folder on Google Drive. Use \".\" to upload into the root folder.")
}
