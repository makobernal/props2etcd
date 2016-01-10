import mousio.etcd4j.EtcdClient
import spock.lang.Ignore
import spock.lang.Specification


@Ignore
class EtcdClientWrapperSpec extends Specification {

    private EtcdClientWrapper testObj
    private EtcdClient etcdClient

    private static final def testData =
            ['/test/folder1/prop1'        : 'value1',
             '/test/folder1/folder2/prop2': 'value2',
             '/test/prop3'                : 'value3']

    private static final Map testDataMap = [
            test: [
                    folder1: [
                            prop1  : 'value1',
                            folder2: [prop2: 'value2'],
                    ],
                    prop3  : 'value3'
            ]
    ]

    def setup() {
        etcdClient = new EtcdClient()
        testObj = new EtcdClientWrapper(etcdClient)
        loadTestData()
    }

    def clear() {
        clearTestData()
    }

    def 'get returns a Map'() {
        expect:
            testObj.get('test') == testDataMap.test
    }


    private void clearTestData() {
        etcdClient.deleteDir('/test').send().get()
    }

    private void loadTestData() {
        testData.each { k, v ->
            etcdClient.put(k, v).send().get()
        }
    }
}
