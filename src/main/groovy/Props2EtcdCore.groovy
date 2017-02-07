import utils.FileUtils

class Props2EtcdCore {

    EtcdService etcdService

    void doIt(String baseDir, String filePath, def etcdEndpoint, boolean forceRemoveBaseDir) {

        validateBaseDir(baseDir)
        Properties props = loadProperties(filePath)

        initEtcdService(etcdEndpoint)
        etcdService.withCloseable {
            etcdService.assertEndpointIsValid()

            if (forceRemoveBaseDir) {
                println "Deleting contents on /$baseDir"
                etcdService.deleteBaseDirectory(baseDir)
            }
            etcdService.putProperties(props, baseDir)
        }

    }

    private Properties loadProperties(String configFilePath) {
        File file = FileUtils.loadFileFromFilePath(configFilePath)
        FileUtils.loadPropertiesFromFile(file)
    }

    private void validateBaseDir(String baseDir) {
        if (baseDir in ['/','']) {
            throw new IllegalArgumentException('Cannot load properties into etcd root directory')
        }
    }

    private void initEtcdService(def etcdEndpoint) {
        if (!etcdService) {
            etcdService = new EtcdService(etcdEndpoint)
        }
    }

}
