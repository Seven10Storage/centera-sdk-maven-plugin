# centera-sdk-maven-plugin
A maven plugin to allow quick deployment of the dependency files used by EMC's Centera SDK

This plugin will pull the correct platform dependencies from the configured repo and 

The easiest usage is to run it from the command line in a project folder that is configured to
access a maven repository for the necessary dependencies.

%> mvn com.seven10:centera-sdk-maven-plugin:3.3.718:instdep -Dinstdep.path=<some_directory> -Dplatform=<platformTag>

Of course, the account running maven should have write privilages to the parent of the destination directory.

**Supported parameters**

instdep.path: This is the destination path for the centera sdk libraries

instdep.platform: This is platform for determining which libraries to install.
The default value is "win64", but any of the following can be specified
  win64 - Windows 64bit Operating System
  win32 - Windows 32bit Operating System
  linux-gcc33-64bit - Linux with gcc3.3 version compilers (CentOS/Redhat?)
  linux-gcc33-32bit - Linux with gcc3.3 version compilers (CentOS/Redhat?)
  linux-gcc4-64bit - Linux with gcc4 version compilers (Debian?)
  linux-gcc4-32bit - Linux with gcc4 version compilers (Debian?)
  solaris8-64bit  - Solaris 8 64bit
  solaris8-32bit  - Solaris 8 32bit

instdep.centera-version: Version of the Centera SDK. Currently only 3.3.718 is supported

instdep.dependency-name: The artifact ID of the centera lib files installed on your repo. The default
  value is "centera-sdk"
  
instdep.dependency-group: The group ID of the centera lib files installed on your repo. The default
  value is "com.seven10"
  
**Creating the dependency packages**

There is currently a project to automate the creation process of the lib files, however the legal status 
of the re-distribution of the dependency files is unknown at this time. Fortunately, the lib files can also be 
created manually as follows.

For each platform you want to support:
  grab the centera sdk archive for that platform and extract the files.
  find the folder that contains the following files. For windows, the following are needed
    FPLibrary.dll 
    FPCore.dll 
    fpos<register size>.dll (register size is 32 or 64)
    fpparser.dll
    FPStreams.dll
    FPUtils.dll
    FPXML.dll
    pai_module.dll

    Unix and Linix systems are bit more complicated as each file must be renamed. They usually come in the format
    "lib<Library Name><register size>.so.<some version>" eg "libFPLibrary64.so.3.3.719". 
    The proper name format for each file is "lib<Library Name>.so" eg "libLibFPLibrary.so".
    These files can either be renamed by hand or by using the install script included with the respective archive.

  in the "lib" folder of the archive there should be a FPLibrary.jar file. Copy that into the folder with the platform specific librarys.
  from a command prompt, in the folder with the libraries, enter the command:
    jar cvf <some file name>.jar <files> 
  where "files" is either "*.dll" for windows or "*.so" for *nix.

  After the package has been created, deploy it to your maven repo with the following command:
    mvn deploy:deploy-file -Dpackaging=jar \
                           -DgroupId=<instdep.dependency-group> \
                           -Dversion=<instdep.centera-version> \
                           -Durl=<your maven repo url> \
                           -DrepositoryId=<your maven repo id> \
                           -Dclassifier=<instdep.platform> \
                           -DartifactId=<instdep.dependency-name> \
                           -Dfile=<jar file name>

  The package should now be available through the plugin.



