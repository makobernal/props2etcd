import exception.UnsupportedFileTypeException
import mousio.etcd4j.responses.EtcdException

class Props2EtcdMain {

    static void main(String[] args){
        new Props2EtcdRunner(args).run()
    }

}

class Props2EtcdRunner {

    private OptionAccessor options
    CliBuilder cliBuilder
    Props2EtcdCore props2Etcd = new Props2EtcdCore()

    Props2EtcdRunner(String[] args) {
        cliBuilder = new CliBuilder(usage: 'props2etcd [options]', width: 120)
        cliBuilder.with {
            d longOpt:'dir', args:1, argName:'directory', required: true, 'REQUIRED. Directory in etcd where the properties will be loaded'
            e longOpt:'endpoint', args:1, argName: 'endpoint', 'Location of etcd cluster (default: http://localhost:2379)'
            f longOpt:'file', args:1, argName:'file', required: true, 'REQUIRED. Path to .properties or .yml file containing the properties'
            h longOpt:'help', 'Print this message'
            r longOpt:'force-remove', 'If used then the original contents of the directory in etcd will be removed'
        }
        options = cliBuilder.parse(args)
    }

    void run() {

        if (!options){
            return
        }

        if (options.h || !options.arguments().isEmpty()){
            cliBuilder.usage()
            return
        }
        try {
            props2Etcd.doIt(options.d, options.f, options.e, options.r)

        } catch (FileNotFoundException | IllegalArgumentException | UnsupportedFileTypeException e) {
            println e.getMessage()
        } catch (EtcdException e) {
            println "Etcd error code ${e.getMessage()}"
        }
    }

}

