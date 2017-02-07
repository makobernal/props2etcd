import mousio.client.retry.RetryNTimes
import mousio.etcd4j.EtcdClient

class EtcdService implements Closeable {

    public static final String DEFAULT_ETCD_ENDPOINT = 'http://localhost:2379'

    EtcdClient etcd
    private String endpoint

    EtcdService (def etcdEndpoint) {
        endpoint = etcdEndpoint ?: DEFAULT_ETCD_ENDPOINT
        etcd = new EtcdClient(URI.create(endpoint))
        etcd.setRetryHandler(new RetryNTimes(0,0))
    }

    void assertEndpointIsValid() {
        if (!etcd.version()) {throw new IllegalArgumentException("Etcd server not found at $endpoint")}
        String version = etcd.version().server
        println "Etcd server version $version found at $endpoint"
    }

    void deleteBaseDirectory(String baseDir) {
        etcd.deleteDir(baseDir).recursive().send().get()
    }

    void putProperties(Properties props, String directory) {
        props.each { k, v ->
            String key = prefixAndTransform((k as String), directory)
            println "Setting key: $key "
            etcd.put(key,v as String).send().get()
        }
    }

    void close(){
        etcd.close()
    }

    private String prefixAndTransform(String propKey, String prefix){
        "$prefix/${propKey.replace('.','/')}"
    }

}