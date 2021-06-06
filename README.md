# google-drive-upload-cli

This is a command line tool to easily upload a file to a Google Drive account.

I developed it to be able to easily synchronize files from a server to my Google Drive account, therefore this tool isn't able to do much else.

## Running

This project was created with Kotlin and Java 8 in mind. Simply run it like you would any other JAR file:
```
java -jar google-drive-upload-cli-1.0.0.jar
```

When first running this tool, a first-time setup will start that runs you through the process of setting up your Google Drive credentials. They will be stored in a file in the current directory and automatically refresh themselves. As long as you don't manually revoke your credentials, you should not need to manually update these.

After the setup is done, you can start uploading files:

```
java -jar google-drive-upload-cli-1.0.0.jar file.mp4 movies
```

This command will upload the file `file.mp4` into my Google Drive folder called `movies`. The first argument can be any valid file, the second is the path to the **directory** your Google Drive file should be placed in. If you want to upload a file to the root directory, simply use `.` for the second argument.

## Help output

There are some options available to change the behaviour of this tool. You can find a list by using the `--help` flag at the end of the command.

```
usage: [-h] [-v] [-f] [-r] [-m MIME_TYPE] SOURCE DEST


This is a command line tool to easily upload a file to a Google Drive account. A
one-time setup is required by simply running without any arguments. Afterwards,
the Google credentials will be stored and automatically refreshed.


optional arguments:
  -h, --help              show this help message and exit

  -v, --verbose           Prints debug information.

  -f, --force-setup       Forces the first-time setup, even if credentials
                          already exist.

  -r, --refresh-token     Forces the Google API access token to be refreshed.

  -m MIME_TYPE,           The mime type of the file to upload. If not
  --mime-type MIME_TYPE   specified, the mime type will be guessed based on
                          the file's name.


positional arguments:
  SOURCE                  Path to the local file that should be uploaded.

  DEST                    The path of the destination folder on Google Drive.
                          Use "." to upload into the root folder.


(C) 2021 Gigadrive UG, Mehdi Baaboura - Published under the MIT License
```

## Building

This project uses Gradle to support easy building from the source with the following command

```
./gradlew build
```

You will find the output jar at `/build/libs/google-drive-upload-cli-VERSION-all.jar`. This is a fat jar that will contain all necessary dependencies and is ready to be run.

## Copyright and License

This program was developed by [Mehdi Baaboura](https://github.com/Zeryther) and published by [Gigadrive UG](https://gigadrivegroup.com) under the MIT License. For more information click [here](https://github.com/Gigadrive/google-drive-upload-cli/blob/master/LICENSE).