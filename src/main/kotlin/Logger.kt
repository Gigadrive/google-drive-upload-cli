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

/** Provides simple logging options. */
public object Logger {
  /** Prints an INFO-level [message] to the console. */
  public fun info(message: String): Unit = println("[INFO] $message")

  /** Prints a WARNING-level [message] to the console. */
  public fun warning(message: String): Unit = println("[WARNING] $message")

  /** Prints an ERROR-level [message] to the console. */
  public fun error(message: String): Unit = System.err.println("[ERROR] $message")
}
