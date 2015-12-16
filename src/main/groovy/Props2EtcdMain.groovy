class Props2EtcdMain {

    CliBuilder cliBuilder

    Props2EtcdMain(){
        cliBuilder = new CliBuilder(usage: 'props2etcd -[h] <props-file> <etcd-endpoint>')
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
        println "propertiesFile = $propertiesFile"
        println "etcdEndpoint = $etcdEndpoint"

        String userDir = System.getProperty('user.dir')

        File file = new File("$userDir/$propertiesFile")
        println file.absolutePath

        // check file exists
        // slurp as Properties object
        // delete existing content in etcd if option is true
        // feed to etcd
        // check we were succesful


    }
}
