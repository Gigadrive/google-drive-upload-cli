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
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.mainBody

/** The main entry point for the program. */
public class Bootstrap {
    public companion object {
        @JvmStatic
        public fun main(args: Array<String>) {
            mainBody {
                val prologueText =
                    "This is a command line tool to easily upload a file to a Google Drive account. A one-time setup is required by simply running without any arguments. Afterwards, the Google credentials will be stored and automatically refreshed."
                val epilogueText =
                    "(C) 2021 Gigadrive UG, Mehdi Baaboura - Published under the MIT License"

                ArgParser(
                        args, ArgParser.Mode.GNU, DefaultHelpFormatter(prologueText, epilogueText))
                    .parseInto(::GoogleDriveUploadCLIArgs)
                    .run {
                        val cli = GoogleDriveUploadCLI(this)
                        cli.start()
                    }
            }
        }
    }
}
