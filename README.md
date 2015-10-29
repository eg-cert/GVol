<H1>GVol</H1>

GVol is a lightweight GUI application built in Java designed to automate the usage of volatility toolkit for the purpose of malware analysis. The application includes various volatility plugins with their predefined options. In addition to that, users can create batch files to run multiple plugins at once to scan a memory image. Furthermore, GVol includes pre-configured batch files to simplify the usage of volatility for malware analysis process. Furthermore, user can compare the output of Volatility for two images.

<H1>GVol Features</H1>
<ul>
<li>GVol automates the use of Volatility using a graphical user interface. </li>
<li>It works with any Volatility version. </li>
<li>GVol includes a set of predefined profiles for windows operating system; also the user
can add new profiles of other operating systems. </li>
<li>User can select plugins and related options from the existing database or add new plugins
or options. </li>
<li>GVol has batch file feature to run multiple plugins. In addition to that, user can set options
for each plugin at batch file through a graphical wizard. </li>
<li>GVol contains a plugin description and malware analysis hints gathered from “The Art of
Memory Forensics” book and “Volatility Command Reference” which can downloaded
from this link: https://code.google.com/p/wiki/CommandReference23 </li>
<li>GVol has a console output section which shows the command running at background and
also the output generated. The user can chose to write this output to a file. The output file
name will be a concatenation of the following image name, batch file name (if it was
used) and plugin name. </li>
<li>GVol now has a comparison feature, user can compare between the outputs of a plugin or
batch files for two images and detect added or deleted lines. </li>
</ul>
<H2>Download</H2>
You can get a copy of the latest release from
<br />
https://github.com/eg-cert/GVol/releases
<H2>Building</H2>
Building should be very simple

```
cd GVol
ant
```
<br />
the target jar file shall be under the dist directory

<H2>Running</H2>
```
java -jar GVol.jar
```


<H2>Configuration</H2>

Download the latest version from releases. You need the Java runtime environment to run GVol. Run the file GVol.jar.
<br />
The first time you run GVol, you should tell it how to run Volatility. <br />
1- Menu bar > Configuration > Cmd & profiles <br />
2- Enter the command to run volatility in your system like "python vol.py" or the path of the standalone executable if you use it. <br />

For more details about the tool and how to use it, read the user guide.
