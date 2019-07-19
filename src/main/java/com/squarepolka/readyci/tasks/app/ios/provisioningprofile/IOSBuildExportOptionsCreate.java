package com.squarepolka.readyci.tasks.app.ios.provisioningprofile;

import com.dd.plist.NSDictionary;
import com.dd.plist.PropertyListParser;
import com.squarepolka.readyci.taskrunner.BuildEnvironment;
import com.squarepolka.readyci.taskrunner.TaskFailedException;
import com.squarepolka.readyci.tasks.Task;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class IOSBuildExportOptionsCreate extends Task {

    public static final String TASK_IOS_EXPORT_OPTIONS_CREATE = "ios_export_options_create";

    @Override
    public String taskIdentifier() {
        return TASK_IOS_EXPORT_OPTIONS_CREATE;
    }

    @Override
    public void performTask(BuildEnvironment buildEnvironment) throws TaskFailedException {
        String exportOptionsPath = String.format("%s/exportOptions.plist", buildEnvironment.getScratchPath());
        String devTeam = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_DEV_TEAM);
        String provisioningMethod = buildEnvironment.getProperty(IOSProvisioningProfileRead.BUILD_PROP_PROVISIONING_METHOD);
        List<Object> provisioningProfiles = buildEnvironment.getObjects(IOSProvisioningProfileRead.BUILD_PROP_IOS_PROFILES);
        try {
            createExportOptionsFile(devTeam, exportOptionsPath, provisioningMethod, provisioningProfiles);
        } catch (IOException e) {
            throw new TaskFailedException(e.getMessage());
        }
    }

    private void createExportOptionsFile(String devTeam, String exportOptionsPath, String provisioningMethod, List<Object> provisioningProfiles) throws IOException {
        NSDictionary rootDict = new NSDictionary();
        rootDict.put("compileBitcode", true);
        rootDict.put("stripSwiftSymbols", true);
        rootDict.put("method", provisioningMethod);
        rootDict.put("signingCertificate","iPhone Distribution");
        rootDict.put("signingStyle", "manual");
        rootDict.put("thinning","<none>");
        rootDict.put("teamID", devTeam);

        NSDictionary provisioningProfilesDict = new NSDictionary();
        for (Object profileObject : provisioningProfiles) {
            ProvisioningProfile profile = (ProvisioningProfile) profileObject;
            provisioningProfilesDict.put(profile.getBundleId(), profile.getName());
        }
        rootDict.put("provisioningProfiles", provisioningProfilesDict);

        File exportOptionsFile = new File(exportOptionsPath);
        PropertyListParser.saveAsXML(rootDict, exportOptionsFile);
    }
}

