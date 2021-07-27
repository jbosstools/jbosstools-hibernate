# Contribution guide

## Install JBoss Tools
Using our own dogfood, we use JBoss Tools as our development environment. JBoss Tools is a set of plugins for the Eclipse platform, so we'll start by obtaining that.

### Get the Eclipse Platform
Download the latest Eclipse platform for your OS from the Eclipse website. We will use the Eclipse IDE for Enterprise Java and Web Developers. You will find the most current download URL for this on the [Eclipse packages page](https://www.eclipse.org/downloads/packages/).

<img src="images/eclipse-packages-page.png" width="1000" />

Install the downloaded artifact according to the instructions for your OS and launch Eclipse using the workspace of your choice.

<img src="images/eclipse-ide-launcher.png" width="600" />

After closing the welcome screen your Eclipse window should look more or less (depending on the OS on which you are working) like the screenshot below.

<img src="images/initial-eclipse-window.png" width="1000" />

Now we are ready to add the JBoss Tools plugins.

### Add the JBoss Tools Plugins

<img src="images/add-new-software.png" width="250" />

Start by selecting the 'Add New Software...' menu item from the 'Help' menu as shown above. In the 'Install' wizard that opens, use 'http://download.jboss.org/jbosstools/photon/stable/updates/' as the URL in the 'Work with' field and press 'Enter' for the JBoss Tools plugin categories to appear. Push the 'Select All' button to include all the JBoss Tools plugins.

<img src="images/available-software-plugins.png" width="600" />

Press the 'Next >' button. The dependencies will be calculated and the details of what will be installed are shown on the second wizard page. 

<img src="images/install-details-plugins.png" width="600" />

You press the 'Next >' button one more time to arrive at the final wizard page where you can review the different licenses.

<img src="images/review-licenses-plugins.png" width="600" />

Choose to accept the licenses for the 'Finish' button to become enabled. Press this button to launch the installation. Be patient, the installation can take a while.
When a popup with a security warning appears informing you about the installation of unsigned software, choose 'Install anyway' to continue the installation.

<img src="images/security-warning.png" width="400" />

The next popup invites you to restart the Eclipse IDE to apply the newly installed plugins. Choose 'Restart Now' and wait for the Eclipse window to reopen.
The JBoss Tools plugins are now installed. However, to be able to develop tests for the Hibernate plugins, we also need to add the JBoss Tools test plugins.

### Add the JBoss Tools Test Plugins

The process to add the JBoss Tools test plugins is completely similar to the previous step. Again, you start by selecting 'Add New Software...' from the 'Help' menu. This time however, in the 'Install' wizard, you need to use 'https://download.jboss.org/jbosstools/photon/development/updates/coretests/' as the URL in the 'Work with' field.

<img src="images/available-software-core-tests.png" width="600" />

Press the 'Next >' button for the installation details to appear.

<img src="images/install-details-core-tests.png" width="600" />

Press 'Next >' again to review and accept the licenses.

<img src="images/review-licenses-core-tests.png" width="600" />

Press 'Finish' to launch the installation and 'Install anyway' in the security warning popup. After restarting, the JBoss Tools test plugins are installed as well and we are ready to import the JBoss Tools Hibernate code base.
