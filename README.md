# props2etcd
Props2Etcd is a CLI tool that parses .yml or .properties files and pushes the values to Etcd keeping their hierarchical
structure.

proposed usage:

Load everything in myprops.properties under the mydirectory directory: 
$ props2etcd --dir mydirectory --file myprops.properties
$ props2etcd -d mydirectory -f myprops.properties

Force delete previous contents in directory:
$ props2etcd --force-remove -d mydirectory -f myprops.properties
$ props2etcd -r -d mydirectory -f myprops.properties


To Build:
./gradlew clean installDist