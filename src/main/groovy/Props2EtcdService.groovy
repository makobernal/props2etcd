import mousio.etcd4j.EtcdClient

class Props2EtcdService {

    EtcdClient etcd
	
	Props2EtcdService(String etcdEndpoint) {
		if (etcdEndpoint) {
			etcd = new EtcdClient(URI.create(etcdEndpoint))
		} else {
			etcd = new EtcdClient()
		}
	}

	def deleteRoot(String root) {
		etcd.deleteDir(root).recursive().send()
	}
}