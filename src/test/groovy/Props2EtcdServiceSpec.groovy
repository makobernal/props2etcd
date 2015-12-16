import mousio.etcd4j.EtcdClient
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

@Ignore
class Props2EtcdServiceSpec extends Specification {

    @Shared EtcdClient etcdClient = new EtcdClient()

    private Props2EtcdService testObj = new Props2EtcdService()


    private static final def testData = [ fur: 'bar', 'foo/baz': 'bar']

    def setup(){
        loadTestData()
    }

    def loadTestData() {

        testData.each { k,v ->
            etcdClient.put(k,v).send()
        }
    }

    def 'deleteRoot should connect to etcd and recursively remove keys in target folder'(){
        given:
            assert etcdClient.get('fur').send() == testData.fur
            assert etcdClient.get('foo/baz').send() == testData.'foo/baz'

        when:
            testObj.deleteRoot('foo')
    }

}
