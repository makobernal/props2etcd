class Props2EtcdMain {

    CliBuilder cliBuilder

    Props2EtcdMain(){
        cliBuilder = new CliBuilder(usage: 'props2etcd -[h] <props-file> <etcd-endpoint>')
        cliBuilder.propsfile(args: 1, '.properties or .yml file to parse and load into Etcd')
        cliBuilder.propsfile(args: 1, '.properties or .yml file to parse and load into Etcd')
        cliBuilder.etcdEndpoint(args: 2, 'Etcd endpoint to work with (optional, default: http://localhost:1234)')
        cliBuilder.h('Print this message')
    }

    static void main(args) {
        def cli = new Props2EtcdMain().cliBuilder
        def options = cli.parse(args)

        if (options.h) {
            println cli.usage()
            return
        } else {
            Props2EtcdMain.props2Etcd(options.arguments()[0], options.arguments()[1])
        }
    }

    static void props2Etcd(String propertiesFile, String etcdEndpoint) {
        println "I want to be a little fishy, yes I do yes I do yes I do"
        println "propertiesFile = $propertiesFile"
        println "etcdEndpoint = $etcdEndpoint"
    }
}
