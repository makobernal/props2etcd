import mousio.etcd4j.EtcdClient
import mousio.etcd4j.responses.EtcdException
import mousio.etcd4j.responses.EtcdKeysResponse.EtcdNode

class EtcdClientWrapper {

    EtcdClient etcd

    EtcdClientWrapper(EtcdClient etcdClient) {
        etcd = etcdClient
    }

    def get(String key) {
        try {
            EtcdNode node = etcd.get(key).send().get().node
            if (node.dir) {
                node.nodes.collectEntries{
                    [ (it.key) : getFromNode(it)]
                }
            } else {
                return node.value
            }
        } catch (EtcdException e){
            return null
        }
    }

    private getFromNode(EtcdNode node){
        if (node.dir) {
            return [(node.key): getFromNode(node.nodes)]
        } else {
            return node.value
        }
    }
}
