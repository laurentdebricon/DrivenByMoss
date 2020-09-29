# DrivenByMoss
Bitwig Studio extensions to support several controllers

### Building and Installing the extension

Users should download and install the version from the
[main site](http://www.mossgrabers.de/Software/Bitwig/Bitwig.html).
These directions are for developers to test changes prior to release.

1. Install Maven and dependences, either [from here](https://maven.apache.org/install.html)
or if on Linux, using the distro package manager, e.g. `yum install maven` or
`apt-get install maven`.
2. Run `mvn install` in this repo's root.
3. Follow [installation instructions](https://github.com/git-moss/DrivenByMoss/wiki/Installation)
for further steps.



## ubuntu 18.04
need jdk-12
sudo add-apt-repository ppa:openjdk-r/ppa
apt-get install openjdk-12-jdk
apt-get remove --purge openjdk-11-jre
java --version


## build
mvn install  -Dbitwig.extension.directory=/home/$(whoami)/Bitwig\ Studio/Extensions



## How to debug DrivenByMoss bitwig extension in Ubuntu and VScode :

Ubuntu 18.04
sudo apt-get install default-jdk maven
install vscode java-pack extension : https://marketplace.visualstudio.com/it ... -java-pack
vim  ~/.bashrc , add at the end :
export BITWIG_DEBUG_PORT=5005
logout and login 
launch bitwig-studio in a terminal and look for the message : 
BITWIG_DEBUG_PORT environment variable set to 5005 so enabling remote debugging
Listening for transport dt_socket at address: 5005

In VScode, in debug tab (ctrl+shift+D), edit launch.json configuration and add : 

{
            "type": "java",
            "name": "Debug Bitwig (Attach)",
            "projectName": "DrivenByMoss",
            "request": "attach",
            "hostName": "localhost",
            "port": 5005
}
git clone https://github.com/git-moss/DrivenByMoss.git
add the folder to VScode workspace.

Run the debug config "Debug Bitwig (Attach)"
put one breakpoint somewhere in the file you want to debug.
open a terminal :
cd DrivenBymoss
mvn install  -Dbitwig.extension.directory=/home/$(whoami)/Bitwig\ Studio/Extensions
(double check that your bitwig is installed in ~/Bitwig Studio/ The above command will build DrivenByMoss and copy the built .bitwigextension to the Extensions folder of Bitwig)

if you have a bitwig controller DrivenByMoss already setup in Bitwig, Bitwig will see that the file is different, and will reload it. Then vscode should stop at your breakpoint !!!
