import mousio.client.retry.RetryNTimes
import mousio.etcd4j.EtcdClient
import mousio.etcd4j.responses.EtcdException
import mousio.etcd4j.responses.EtcdKeysResponse
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification

import static EtcdServiceSpec.localEtcdAvailable

@Requires({localEtcdAvailable()})
class EtcdServiceSpec extends Specification {


    public static final String rootTestDirectory = '/testProps2Etcd'

    private static boolean localEtcdAvailable(){
        EtcdClient etcdClient = new EtcdClient()
        etcdClient.setRetryHandler(new RetryNTimes(0,0))
        etcdClient.version()
    }

    @Shared EtcdClient testEtcdClient = new EtcdClient()

    private EtcdService testObj = new EtcdService()

    def cleanup() {
        clearEtcdTestDirectory()
    }

    def 'assertEndpointIsValid should throw exception when server not found'(){
        given:
            EtcdClient clientWithInvalidEndpoint = new EtcdClient(URI.create('http://localhost:1234'))
            clientWithInvalidEndpoint.setRetryHandler(new RetryNTimes(0,0))
            testObj.etcd = clientWithInvalidEndpoint

        when:
            testObj.assertEndpointIsValid()

        then:
            thrown(IllegalArgumentException)
    }

    def 'deleteBaseDirectory should connect to etcd and recursively remove keys in target directory'(){
        given:
            String directoryWithSubdirectories = 'directory1/directory2'
            String directoryToDelete = "$rootTestDirectory/$directoryWithSubdirectories"
            testEtcdClient.putDir(directoryToDelete).send().get()

        and:
            assert testEtcdClient.getDir(directoryToDelete).send().get()

        when:
            testObj.deleteBaseDirectory(directoryToDelete)

        and:
            testEtcdClient.getDir(directoryToDelete).send().get()

        then:
            thrown(EtcdException)
    }

    def 'putProperties should load all properties into baseDir treating nested property keys as subdirectories'(){
        given:
            Properties props = new Properties()
            props.put('prop1','val1')
            props.put('dir1.prop2', 'val2')
            props.put('dir1.dir2.prop3', 'val3')

        when:
            testObj.putProperties(props, rootTestDirectory)

        then:
            EtcdKeysResponse.EtcdNode directoryContents = testEtcdClient.get(rootTestDirectory).recursive().send().get().node
            directoryContents.key == rootTestDirectory
            directoryContents.nodes.find{it.key.contains('prop1')}.value == 'val1'
            directoryContents.nodes.find{it.key.contains('dir1')}.nodes.find{it.key.contains('prop2')}.value == 'val2'
            directoryContents.nodes.find{it.key.contains('dir1')}.nodes.find{it.key.contains('dir2')}.
                nodes.find{it.key.contains('prop3')}.value == 'val3'
    }

    private void clearEtcdTestDirectory() {
        try {
            testEtcdClient.deleteDir(rootTestDirectory).recursive().send().get()}
        catch (EtcdException e) {}
    }

}
