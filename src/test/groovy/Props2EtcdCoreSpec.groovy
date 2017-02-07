import spock.lang.Specification
import spock.lang.Unroll
import utils.FileUtils

class Props2EtcdCoreSpec extends Specification {

    public static final String TEST_PROPERTIES_FILEPATH = 'src/test/resources/test-file.properties'

    Props2EtcdCore testObj = new Props2EtcdCore()

    def 'doIt calls putProperties on EtcdService with properties loaded from given file with the directory'() {
        given:
            EtcdService etcdServiceMock = Mock()
            Properties properties = new Properties()
            testObj.etcdService = etcdServiceMock
            GroovyMock(FileUtils, global: true)
            FileUtils.loadPropertiesFromFile(_) >> properties

        when:
            testObj.doIt('dir', TEST_PROPERTIES_FILEPATH, false, false)

        then:
            1 * etcdServiceMock.assertEndpointIsValid()
            1 * etcdServiceMock.putProperties(properties, 'dir')
    }

    def 'doIt calls deleteBaseDirectory on EtcdService before putProperties'() {
        given:
            EtcdService etcdServiceMock = Mock()
            Properties properties = new Properties()
            testObj.etcdService = etcdServiceMock
            GroovyMock(FileUtils, global: true)
            FileUtils.loadPropertiesFromFile(_) >> properties

        when:
            testObj.doIt('dir', TEST_PROPERTIES_FILEPATH, false, true)

        then:
            1 * etcdServiceMock.assertEndpointIsValid()
            1 * etcdServiceMock.deleteBaseDirectory('dir')
            1 * etcdServiceMock.putProperties(properties, 'dir')
    }

    @Unroll
    def 'doIt throws IllegalArgumentException if baseDir is etcd root (#baseDir)'(){
        when:
            testObj.doIt(baseDir, TEST_PROPERTIES_FILEPATH, false, true)

        then:
            thrown(IllegalArgumentException)

        where:
            baseDir << ['','/']
    }

}
