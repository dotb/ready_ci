
package com.squarepolka.readyci.tasks.app.android;


import com.squarepolka.readyci.configuration.ReadyCIConfiguration;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.tasks.Task;
import com.squarepolka.readyci.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collection;

@Component
public class AndroidUploadHockeyapp extends Task {


    public static final String TASK_UPLOAD_HOCKEYAPP = "android_upload_hockeyapp";
    public static final String BUILD_PROP_HOCKEYAPP_TOKEN = "hockappToken";
    public static final String BUILD_PROP_HOCKEYAPP_RELEASE_TAGS = "hockeyappReleaseTags";
    public static final String BUILD_PROP_HOCKEYAPP_RELEASE_NOTES = "hockeyappReleaseNotes";

    private static final String COMMAND_GIT = "/usr/bin/git";

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadyCIConfiguration.class);

    @Override
    public String taskIdentifier() {
        return TASK_UPLOAD_HOCKEYAPP;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) {
      
        String hockappToken = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_TOKEN);
        String releaseTags = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_RELEASE_TAGS, "");
        String releaseNotes = buildEnvironment.getProperty(BUILD_PROP_HOCKEYAPP_RELEASE_NOTES, "");

        if(releaseNotes.isEmpty()) {
            InputStream inputStream = executeCommand(new String[]{COMMAND_GIT, "log", "-1", "--pretty=%B"}, buildEnvironment.getProjectPath());
            try {
                releaseNotes = Util.readInputStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // upload all the apk builds that it finds
        Collection<File> files = Util.findAllByExtension(new File(buildEnvironment.getProjectPath()), ".apk");
        for (File apk : files) {
            if(apk.getAbsolutePath().contains("build")) {
                LOGGER.warn("uploading "+ apk.getAbsolutePath());
                // Upload to HockeyApp
                executeCommand(new String[]{"/usr/bin/curl",
                        "https://rink.hockeyapp.net/api/2/apps/upload",
                        "-H", "X-HockeyAppToken: " + hockappToken,
                        "-F", "ipa=@" + apk.getAbsolutePath(),
                        "-F", "notes=" + releaseNotes,
                        "-F", "tags=" + releaseTags,
                        "-F", "notes_type=0",               // Textual release notes
                        "-F", "status=2",                   // Make this version available for download
                        "-F", "notify=1",                   // Notify users who can install the app
                        "-F", "strategy=add",               // Add the build if one with the same build number exists
                        "-F", "mandatory=1"                 // Download is mandatory
                }, buildEnvironment.getProjectPath());
            }
        }
    }

}