!!! ATTENTION do not change other versions manually !!!
!!! all versions must be same BEFORE executiong set-version !!!
!!! e.g. in all pom.xml files: 0.9.8-SNAPSHOT and all eclipse files [MANIFEST.MF, feature.xml, category.xml]: 0.9.8.qualifier !!!

1. update the version in pom.xml: e.g. <newVersion>0.9.9-SNAPSHOT</newVersion>
2. run: LogViewer-set-new-version.launch or execute: mvn 'clean tycho-versions:set-version' all versions are updated now
3. build with 'mvn verify' or 'mvn install' or just launch config: LogViewer-Testing-Build.launch

sources:
http://codeandme.blogspot.co.at/2012/12/tycho-build-9-updating-version-numbers.html
https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-plugin
https://eclipse.org/tycho/sitedocs/tycho-release/tycho-versions-plugin/plugin-info.html

target defintions
https://wiki.eclipse.org/Eclipse_Project_Update_Sites
http://codeandme.blogspot.de/2012/12/tycho-build-8-using-target-platform.html
https://wiki.eclipse.org/Tycho/Packaging_Types#eclipse-target-definition
https://bugs.eclipse.org/bugs/show_bug.cgi?id=383865
http://blog.vogella.com/2013/01/03/tycho-advanced/

24.09.2017 anb0s

license: https://eclipse.org/org/documents/epl-v10.html