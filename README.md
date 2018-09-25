# ReadyCI
A no-fuss CI/CD service and collection of build scripts

##### Table of Contents
- [Why use ReadyCI?](#why-use-readyci)
- [How to use ReadyCI?](#how-to-use-readyci)
  * [Building ReadyCI](#building-readyci)
  * [Running a command-line build](#running-a-command-line-build)
  * [Configure pipelines](#configure-pipelines)
  * [Running ReadyCI](#running-readyci)
  * [Running a Build Service](#running-a-build-service)
- [Configuration Explained](#configuration-explained)
- [Task Types](#task-types)
- [Task Parameters](#task-parameters)
- [Release Notes](#release-notes)
  
  
## Why use ReadyCI?

### :+1: It comes with scripts
ReadyCI comes with build scripts so that you spend less time setting up your automated CI/CD infrastructure, and get to making automated builds faster. ReadyCI scripts currently support:
* iOS apps
* Maven projects


### :+1: Command-line or web-service
You can run ReadyCI on the command-line within another CI environment like Jenkins, or run ReadyCI as a service on it's own and accept web-hook calls from GIT services.

### :+1: Supports GIT web-hooks
ReadyCI supports GIT commit web-hooks when you run it as a service so that your automated builds start as soon as you push to your git repository. ReadyCI supports web-hooks from:
* GitHub
* BitBucket

### :+1: Parses iOS provisioning profiles
Configuring iOS builds is tricky. ReadyCI uses the .mobileprovision file generated by Apple Developer Portal to automatically configure your build and remove some of the guess-work behind making your iOS app build successful.

### :+1: Handles whitespace
Paths, filenames and target names look great when you use whitespace. However, whitespace can be a nightmare to manage in CI scripts so ReadyCI handles whitespace like a pro* so that your builds keep working while your project files are clean and readable.

\* The `pipeline` command-line parameter doesn't handle whitespace well. A work in process :-)

## How to use ReadyCI
### Building ReadyCI
Download ReadyCI to your computer and navigate to the ReadyCI folder in your terminal.
Assuming that you have Maven installed, create a jar: target/readyci-0.3.jar using the following command:
```bash
$ mvn install
```

### Running a command-line build
Run a once off command-line build by specifying the `yml` configuration file and the `pipeline=` parameter. It's only fitting that ReadyCI be able to build itself!   

Try this out with the example found below using the configuration `readyConfigExample.yml` to run a ReadyCI build named `readyci`. 
```bash
$ java -jar target/readyci-0.3.jar readyConfigExample.yml pipeline=readyci

Loaded configuration readyConfigExample.yml with 2 pipelines
...
ReadyCI is in command-line mode
Building pipline readyci
...
FINISHED BUILD 74e404d8-6bae-41fa-8aa1-4d786c797c58 
```  
  

A successful build will deploy `readyci.jar` to your `/tmp/` directory. You can check that it's there like this:
```bash
$ ls -la /tmp/readyci-0.3.jar 
-rw-r--r--  1 bradley  wheel  16612035 Jun 13 12:30 /tmp/readyci.jar
```


### Configure pipelines
Make your own copy of the `readyConfigExample.yml` file found in the ReadyCI folder and edit the file to specify all of your pipelines and associated build tasks.  
Please scroll down to 'Configuration Explained' and 'Task Types' for reference.


### Running ReadyCI
#### Option 1
If you already have your iOS/Android project cloned onto your computer, ReadyCI can directly commence on automated building!
Paste the `readyConfigExample.yml` file into the root of your project repository and **commit** it.
Then, redirect terminal to the root of that project repository and run the following command:
```bash
$ java -jar target/readyci-0.3.jar readyConfigExample.yml pipeline=ready-ci 
```  

#### Option 2
If you don't have a local copy of your project repository and would like ReadyCI to clone it for you and do an automated build.  
Redirect terminal to a directory which has a readyci yml configuration file. Run the following command found below, specifying the gitPath. 
ReadyCI will fetch the repository, load the configuration in `readyConfigExample.yml`, and execute the `readyci` pipeline. 
```bash
$ java -jar target/readyci-0.3.jar readyConfigExample.yml pipeline=ready-ci gitPath=git@github.com:dotb/readyci.git
```
  
***or***   
you can specify the gitPath in the configuration file itself, and just run readyCI as below:
```
$ java -jar target/readyci-0.3.jar readyConfigExample.yml pipeline=readyci 
```

#### Option 3
If your project repository on Git already contains a `readyci.yml` configuration file, ReadyCI can clone the repository for you and load the configuration file before the build commences.
You only need to specify the pipeline to run in the file and the gitPath for which ReadyCI can clone from.
```bash
$ java -jar target/readyci-0.3.jar pipeline=ready-ci gitPath=git@github.com:dotb/readyci.git
```

#### Option 4 - my favourite
If you would like to keep sensitive information(store pass etc) away from your project contributors and if you have a server, you can make the
server store the sensitive parameters in a yml file and ask the server to run the automated builds. Other build parameters can be specified
in a seperate yml file `readyci.yml` in the project repository.

ReadyCI will 1st read the credentials of the yml file. After that, when the repository is cloned, ReadyCI will read the consequent parameters and tasks from `readyci.yml` and combine all the parameters together.
```bash
$ java -jar target/readyci-0.3.jar readyci-credentials.yml pipeline=ready-ci gitPath=git@github.com:dotb/readyci.git
```


 ****Note:***  
If you name your yml configuration file as  `readyci.yml`, you don't need to specify the name of the yml file when running it on command line. 
ReadyCI will read the configuration file and execute the build pipeline that you specify.
```
$ java -jar target/readyci.jar pipeline=readyci
```


### Running a build service
ReadyCI can run as a web-service and listen out for web-hook calls. Configure your GIT repository to post to http://<your address>:8080/webhook and then run ReadyCI with a `yml` configuration time and the `server` parameter.
```bash
java -jar target/readyci.jar readyConfigExample.yml server

Loaded configuration readyConfigExample.yml with 2 pipelines
...
ReadyCI is in server mode
Started ReadyCI in 3.655 seconds (JVM running for 4.612)
```

You can test your web-hook using cURL
```bash
curl -X POST \
  http://localhost:8080/webhook \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/json' \
  -d '{
   "push":{
      "changes":[
         {
            "new":{
               "type":"branch",
               "name":"master",
               "target":{
                  "author":{
                     "raw":"Bradley Clayton <me@cheese.toast.com>"
                  }
               }
            }
         }
      ]
   },
   "repository":{
      "name":"readyci"
   }
}'
```

This will kick off a build of ReadyCI and you'll see output like this:
```bash
Webhook proceeding with build for pipline readyci
RUNNING BUILD c7a56eec-f303-4ad9-8de9-ffe5da68bef5 
STARTING TASK build_path_clean | Finish up by cleaning the build folder
...
STARTING TASK checkout_git |  
Cloning into '/tmp/readyci//c7a56eec-f303-4ad9-8de9-ffe5da68bef5'...
COMPLETED TASK checkout_git
STARTING TASK maven_install | Run maven install
Executing command: mvn install 
...
FINISHED BUILD c7a56eec-f303-4ad9-8de9-ffe5da68bef5 
```
Ready CI currently supports web-hook calls from GitHub and Bitbucket.

## Configuration explained
Ready CI is configured by supplying a simple YML configuration file on the command line, which is contained in the root of your repository. For example, the configuration below builds Ready CI using Maven and the code on GitHub.
```yml
  pipelines:
  - name: readyci # every pipeline needs a name 
    gitPath: git@github.com:dotb/readyci.git
    gitBranch: master
    parameters:
      deploySrcPath: target/readyci.jar
      deployDstPath: /tmp/readyci.jar

    tasks:
    - task: maven_install # Run maven install
 
    - task: deploy_copy # Copy the built binary to a deployment destination

    - task: build_path_clean
```
    
Lets take a look at some of these parameters: (Full list of parameters are found in readyConfigExample.yml)

| Parameter | Description |
| :-------- | :---------- |
| instanceName      | A name for your instance. This name is used to populate git commit messages so that you can identify automated commits. ReadyCI also uses the instanceName to avoid cyclic builds triggered by the web-hook receiving one of it's own commit notifications. 
| pipelines         | An array of as many pipeline configurations as you want |
| - name            | Each pipeline is named, and you use this name to start a command-line build |
|   gitPath         | The path to your code repository |
|   gitBranch       | Use the gitBranch parameter to specify which branch git should clone from. Or, which branch should trigger builds when web-hook requests are received | 
|   parameters      | Parameters are used to customise the build tasks |
|     deploySrcPath & deployDstPath | In this example the deploy_copy task needs to know the source and destination paths for the `readyci.jar` file, so that it can copy it to the right place |
|   tasks:          | The array of tasks is used to configure each build step |
|   -  task         | The task is important and **case sensitive**, it tells ReadyCI which task should be run |

## Task types
ReadyCI includes a collection of task types that currently supports Maven and iOS/Android builds. Below is the full list of tasks available:

| Task                             | Description |
| :---                             | :--- |
| *Maven*                          | |
| maven_install                    | Run maven install |
| *iOS*                            | |
| ios_carthage_update              | Install dependencies using Carthage |
| ios_pod_install                  | Install dependencies using CocoaPods |
| ios_install_provisioning_profile | Install a .mobileprovisioning file onto the build host |
| ios_provisioning_profile_read    | Read build information from a .mobileprovisioning file |
| ios_increment_build_number       | Increments the buld number in Info.plist |
| ios_export                       | Compile your app and export an archive |
| ios_export_options_create        | Creates a populated .plist with export options |
| ios_archive                      | Generate an archived .ipa|
| ios_upload_hockeyapp             | Upload app builds to HockeyApp |
| ios_upload_itunes_connect        | Upload your build .ipa to iTunes connect |
| *Android*                        | |
| android_create_local_properties  | Create local.properties file and writes the sdk path |
| android_create_apk_file          | Creates apk file for the scheme specified |
| android_sign_app                 | Signs the apk file generated.  You should not specify this task if your app gradle file already contains signingConfigs.  If you are ***not*** using the signingConfigs but specifying them in the yml file, please remove them.|
| android_task_increment           | Increments the build number in version.properties |
| android_upload_play_store        | Upload app builds to Google play store  |
| android_upload_hockeyapp         | Upload app builds to HockeyApp |
| *GIT*                            | |
| checkout_git                     | Clone a git repository. This step is automatically run and you don't need to reference this task |
| *Build*                          | |
| build_path_create                | Creates a temporary build folder. This step is automatically run and you don't need to reference this task |
| build_path_clean                 | Cleans the build folder. This step is automatically run and you don't need to reference this task ||
| *Deploy*                         | |
| deploy_copy                      | A simple copy based deployment task |

## Task Parameters
Parameters needed by each task. Parameters do not need to be duplicated in the yml file.

| Task                             | Parameters Used|
| :---                             | :--- |
| *Maven*                          | |
| maven_install                    | - |
| *iOS*                            | Compulsary parameters: projectPath, infoPlistPath|
| ios_carthage_update              | - |
| ios_pod_install                  | - |
| ios_install_provisioning_profile | iosProfiles |
| ios_provisioning_profile_read    | iosProfiles |
| ios_increment_build_number       | - |
| ios_export                       | - |
| ios_export_options_create        | - |
| ios_archive                      | scheme, workspace, configuration |
| ios_upload_hockeyapp             | hockappToken, hockeyappReleaseTags, hockeyappReleaseNotes |
| ios_upload_itunes_connect        | scheme, iTunesUsername, iTunesPassword |
| *Android*                        | |
| android_create_local_properties  | - |
| android_create_apk_file          | scheme |
| android_sign_app                 | javaKeystorePath, keystoreAlias, storepass, scheme |
| android_upload_hockeyapp         | hockappToken, hockeyappReleaseTags, hockeyappReleaseNotes |
| android_upload_play_store        | deployTrack, packageName, playStoreAuthCert, playStoreEmail |
| *GIT*                            |  |
| checkout_git                     | gitPath, gitBranch |
| *Build*                          |  |
| build_path_create                | - |
| build_path_clean                 | - |
| *Deploy*                         | |
| deploy_copy                      | deploySrcPath, deployDstPath |

## Release notes
| Release | Features |
| :---  | :---|
| 0.2   |   0.2 Kicks things off with a whole host of features, like allowing you to build iOS app projects and maven projects. Upload iOS binaries to Hockeyapp and iTunes connect. Increment the iOS build number. Automatically commit modified files back to GIT.  |
| 0.3   |   Added the ability to read configuration from both the ReadyCI host and the repository. Simply add a readyci.yml file to the root of your repository and it'll be included in the build. |
| 0.4   |   Added timing of tasks, output in the console log. Added 'pod repo update' to the Cocoapod task. | 

