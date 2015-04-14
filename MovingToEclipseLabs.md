# Introduction #

I readed about Eclipse Labs at
  * http://planeteclipse.org/planet/
  * http://sidelab.wordpress.com/2010/05/15/eclipse-labs-let-community-know-about-your-eclipse-projects/
  * http://googlecode.blogspot.com/2010/05/announcing-eclipse-labs.html

# Details #

  1. Create migration request at http://code.google.com/p/support/issues/entry?template=Migration%20request
  1. Fill issue: http://code.google.com/p/support/issues/detail?id=3973 and wait when the project is ready to be migrated.
  1. Sync repository with svnsync: just open Linux shell and type:
```
svnsync init https://svn.codespot.com/a/eclipselabs.org/logviewer/ https://eclipselogviewer.googlecode.com/svn/
svnsync sync https://svn.codespot.com/a/eclipselabs.org/logviewer/
```
  1. Now wait until the last revision is commited ([r117](https://code.google.com/p/logviewer/source/detail?r=117)).
  1. Download all files from old project and upload them to new project
  1. Update Project Home page and Wiki links
  1. Update all links at http://marketplace.eclipse.org/

P.S.: some features are not available at Eclipse Labs -> created request: http://code.google.com/p/support/issues/detail?id=3997