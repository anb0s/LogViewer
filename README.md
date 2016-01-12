#Eclipse Log Viewer [![Build Status](https://travis-ci.org/anb0s/LogViewer.svg)](https://travis-ci.org/anb0s/LogViewer) [![Build Status](https://buildhive.cloudbees.com/job/anb0s/job/LogViewer/badge/icon)](https://buildhive.cloudbees.com/job/anb0s/job/LogViewer/)
Eclipse Plug-in for tailing log files and eclipse consoles (e.g. SVN, Java Stack Trace, CDT), including syntax coloring with either a regular expression or a word match. It allows you to have multiple logs open concurrently.

<a href="https://eclipse.org/donate/" target="_blank"> <img src="http://www.eclipse.org/donate/images/friendslogo200.jpg" alt="Eclipse Friends" title="Eclipse Friends" border="0" /></a>
<a href="http://flattr.com/thing/62009/logviewer" target="_blank"> <img src="http://api.flattr.com/button/button-static-50x60.png" alt="Flattr this" title="Flattr this" border="0" /></a>

<a href="https://github.com/anb0s/logviewer" target="_blank"> <img src="https://raw.githubusercontent.com/anb0s/logviewer/master/de.anbos.eclipse.logviewer.plugin/screens/LogViewer_view_File_0.9.8.jpg" alt="Eclipse Friends" title="Eclipse Friends" border="0" /></a>

##Install##

Use the Update Manager or extract zip file to eclipse/dropin folder.

The update site URL is: http://anb0s.github.io/LogViewer

Install descriptions: https://github.com/anb0s/logviewer/wiki/Install

Eclipse Markeplace: http://marketplace.eclipse.org/content/logviewer

##Similar software##
Otros log viewer/parser: https://github.com/otros-systems/otroslogviewer

##Changelog##

###[1.0 (2016-03-xy) planned](https://github.com/anb0s/LogViewer/issues?q=milestone%3Av1.0)###
* TBD

###[0.9.9 (2016-01-31) working](https://github.com/anb0s/LogViewer/issues?q=milestone%3Av0.9.9)###
* [#34: Truncate logs](https://github.com/anb0s/LogViewer/issues/34)
* [#46: An idea for Logviewer(Clear logviewer Console)](https://github.com/anb0s/LogViewer/issues/46)
* ~~[#61: Logviewer does not remove it's preference store PropertyChangeListener](https://github.com/anb0s/LogViewer/issues/61)~~
* [#66: change default font fixed width](https://github.com/anb0s/LogViewer/issues/66)
* ~~[#80: Server returned lastModified <= 0](https://github.com/anb0s/LogViewer/issues/80)~~
* [#83: Eclipse Mars: cannot locate the resource](https://github.com/anb0s/LogViewer/issues/83)
* [#85: migration to GitHub](https://github.com/anb0s/LogViewer/issues/85)

##old history: from source forge:##
###0.9.9 (2012-11-20) broken, code lost###
* Issue 7: Log-Filter
* Issue 21: Support for Log Rollover
* Issue 22: A '#' character in logfile name crashes
* Issue 23: Change preference store format
* Issue 37: Locks log file, causing issues for external use
* Issue 45: Rules table is not displayed after import
* Issue 46: An idea for Logviewer(Clear logviewer Console)
* Issue 51:	Provide possibility to open log file from the beginning on
* Issue 54: No way to enable scroll lock
* Issue 58: Log Viewer is corrupting the window rendering on popup
* Issue 65: Unable to Open Console in LogViewer
* Issue 68: Add bold and font-size options to rules

###0.9.8.8 (2011-08-07)###
* switched from SVN to GIT, please use GIT from now
* New update site URL: http://logviewer.eclipselabs.org.codespot.com/git/de.anbos.eclipse.logviewer.update/
* Issue 53:	Input of invalid regular expressions is possible in the rules preferences
* Issue 55:	Improve FileTail's exception handling
* Issue 56:	Error on Tail all action with already tailing document

###0.9.8.7 (2011-03-07)###
* Issue 32: Translation (German translation added)
* Issue 35: Tail option's auto-refresh/scrolling is very slow - is unusable (added some small fixes)
* Issue 38: Cannot enter colors using Mac OS X
* Issue 52: Using java5 jre on ubuntu throws error

###0.9.8.6 (2010-08-15)###
* Issue 43: Null Pointer error in ResourceUtils.getResource()
* Issue 44: Exception if opening binary file

###0.9.8.5 (2010-08-10)###
* Issue 42: LogViewer will not start up (isEmpty method is not available on Java5)
* should be compatible with Java 5 (JRE 1.5) again

###0.9.8.4 (2010-05-25)###
* Project moved to Eclipse Labs and renamed to logviewer: http://code.google.com/a/eclipselabs.org/p/logviewer/wiki/MovingToEclipseLabs
* New update site URL: http://svn.codespot.com/a/eclipselabs.org/logviewer/trunk/de.anbos.eclipse.logviewer.update/
* Issue 40: move project to Eclipse Labs

###0.9.8.3 (2010-02-22)###
* Issue 8: Word-wrap
* Default line color fixed
* Issue 27: Add a "Show When Updated" option
* Issue 36: customizable logfile extensions

###0.9.8.1 (2010-02-17)###
* Issue 29: Switching between previously viewed log files resets viewing position.
* Issue 30: Preferences: 'read wait' can not be changed

###0.9.8 (2010-01-17)###
* Issue 16: show logs from a stream / console
* It is possible to open consoles like Java Stack Trace, CDT, SVN, CVS and compatible
* Log Viewer Console can be used for rule testing: one can paste text and check output in Log Viewer
* Issue 20: Annoying icon and menu items
* Context menu items appears only for resources
* New "Clear History" menu entry
* Menu and buttons reorganized for better usability

###0.9.7 (2009-12-26)###
* Issue 18: could not create a view
* Issue 19: load old preferences
* New shortcut button for rule preferences
* Rules dialog improved
* New "Export all" & "Export selected" buttons
* Export and Import improved

###0.9.6 (2009-12-08)###
* Issue 11: Tailing start/stop status
* Issue 14: Encoding is not saved
* Issue 15: Make LogViewer class public to other plugins
* Issue 3: Regular expressions do not work
* Issue 4: Improve regular expression for highlighting
* Coloring rules redesigned and splitted to line selection rules and filters. WORD, JakartaRegExp and JavaRegExp rules are available. Actually only one filter (coloring) is implemented. I will add exclude, include and replace filters for version 1.0
* Export / Import extended
* ATTENTION: the preferences and import / export formats are not compabile to previous versions. You have to recreate the rules! I will add backward compatibility for next release.

###0.9.5 (2009-11-21)###
* First official release
* Issue 10 : Renamed Tabs are not saved
* Issue 11 : Tailing start/stop status

###0.9.3 (2009-11-10)###
* Issue 13 : Refresh current logfile when not tailing activates tailing
* Issue 5 : Drap and drop
* Issue 9 : Find Shortcut

###0.9.1 (2009-11-08)###
* Issue 1 : Open more than file at a time
* Issue 2 : Open With Log Viewer

###0.9.0 (2009-11-07)###
* CVS repo imported from Eclipse Logfile Viewer (http://code.google.com/a/eclipselabs.org/p/logviewer/wiki/ImportingCVSrepository)
* Copied all bugs and enhancement requests from sourceforge project
* Source refactoring started


##It's a fork from Eclipse Logfile Viewer: http://sourceforge.net/projects/logfiletools/##

##Thank you jmimo!##
