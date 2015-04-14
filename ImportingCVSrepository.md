## Introduction ##

I've imported the CVS repository from sourceforge.


## Details ##


### 1. First change to empty dir and load CVS repo from sourceforge ###

```
cd /root/logfile
rsync -av logfiletools.cvs.sourceforge.net::cvsroot/logfiletools/*
```

### 2. Download and install cvs2svn ###

```
wget http://cvs2svn.tigris.org/files/documents/1462/46528/cvs2svn-2.3.0.tar.gz
tar -xzvf cvs2svn-2.3.0.tar.gz
cd cvs2svn-2.3.0
make install
```

### 3. Convert from CVS to Subversion ###

```
cvs2svn --encoding=utf8 --default-eol=native --trunk-only -s logfile-svn logfile
```

### 4. Reset google repository ###

Browse to the ‘Source’ page, click the ‘reset this repository’ link and choose the option "Did you just start this project and do you want to ’svnsync’ content from an existing repository into this project?"


### 5. Import local SVN repo to google ###

```
svnsync init --username username https://eclipselogviewer.googlecode.com/svn file:///root/logfile-svn/
svnsync sync --username username https://eclipselogviewer.googlecode.com/svn
```

### 6. svnsync reported errors ###

For [revision 10](https://code.google.com/p/logviewer/source/detail?r=10) & 11 some files are bad formated (http://code.google.com/p/support/issues/detail?id=3270):

```
svnsync: At least one property change failed; repository is unchanged
svnsync: Error setting property 'log':
Could not execute PROPPATCH.
```

I reformated the file "./db/revprops/0/10" and "../11" manually and restarted

```
svnsync sync
```

Instead of do it manually one should use the interface ;)

```
svn propedit --revprop -r10 svn:log
```