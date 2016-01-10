import mousio.etcd4j.EtcdClient
import mousio.etcd4j.responses.EtcdException
import mousio.etcd4j.responses.EtcdKeysResponse
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

class Props2EtcdServiceSpec extends Specification {

    @Shared EtcdClient etcdClient = new EtcdClient()

    private Props2EtcdService testObj = new Props2EtcdService()

    private static final def testData =
            [ '/test/folder1/prop1' : 'value1',
              '/test/folder1/folder2/prop2' : 'value2',
              '/test/prop3' : 'value3',
              '/test/folder3/prop4' : 'value4']

    def setup() {
        loadTestData()
    }

    def cleanup() {
        clearEtcd()
    }

    def 'deleteRoot should connect to etcd and recursively remove keys in target folder'(){
        given:
            assert getEtcdNodeValue('/test/folder1/prop1') == 'value1'

        when:
            testObj.deleteRoot('/test/folder1')

        then:
            !getEtcdNodeValue('/test/folder1')
            getEtcdNodeValue('/test/prop3')
            //getEtcdNodeValue('/test/folder3')
    }

    private void clearEtcd() {
       etcdClient.deleteDir('/test').send().get()
    }

    private void loadTestData() {
        testData.each { k,v ->
            etcdClient.put(k,v).send().get()
        }
    }

    private String getEtcdNodeValue(String key){
        try {
            EtcdKeysResponse.EtcdNode node = etcdClient.get(key).send().get().node
            node.dir ? etcdClient.getDir(node.key): node.value
            return etcdClient.get(key).send().get().node.value
        }
        catch (EtcdException e){
            return null
        }
    }

    private def getEtcdDirValue(String key) {
        //etcdClient.getDir(key).
    }

}
