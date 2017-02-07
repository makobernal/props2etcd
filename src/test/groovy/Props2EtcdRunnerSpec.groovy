import exception.UnsupportedFileTypeException
import spock.lang.Specification
import spock.lang.Unroll

class Props2EtcdRunnerSpec extends Specification {

    public static final String USAGE = '''usage: props2etcd [options]
 -d,--dir <directory>       REQUIRED. Directory in etcd where the properties will be loaded
 -e,--endpoint <endpoint>   Location of etcd cluster (default: http://localhost:2379)
 -f,--file <file>           REQUIRED. Path to .properties or .yml file containing the properties
 -h,--help                  Print this message
 -r,--force-remove          If used then the original contents of the directory in etcd will be removed'''

    public static final String USAGE_WITH_ERROR = 'error: Missing required options: d, f\n' + USAGE

    PrintStream originalOut
    ByteArrayOutputStream out

    void setup(){
        originalOut = System.out
        out = new ByteArrayOutputStream()
        System.setOut(new PrintStream(out))
    }

    void cleanup() {
        System.setOut(originalOut)
    }

    def 'when required options are not specified, print usage and exit'() {
        given:
            Props2EtcdRunner testObj = new Props2EtcdRunner([] as String[])
        when:
            testObj.run()
        then:
            usageIsPrinted(true)
    }

    @Unroll
    def 'when unrecognized option (#args) is specified, print usage and exit'() {
        given:
            Props2EtcdRunner testObj = new Props2EtcdRunner(args)
        when:
            testObj.run()
        then:
            usageIsPrinted(true)
        where:
            args << ['--unrecognized', '-h somejunk']
    }

    @Unroll
    def 'when help option (#helpArg) is specified with required options, print usage and exit'() {
        given:
            Props2EtcdRunner testObj = new Props2EtcdRunner(['-d', 'dir', '-f', 'file', helpArg] as String[])
        when:
            testObj.run()
        then:
            usageIsPrinted()
        where:
            helpArg << ['-h', '--help']
    }

    @Unroll
    def 'when required options are specified with extra arguments (#bogusArgs), print usage and exit'(){
        given:
            Props2EtcdRunner testObj = new Props2EtcdRunner((['-d', 'dir', '-f', 'file'] + bogusArgs) as String[])
        when:
            testObj.run()
        then:
            usageIsPrinted()
        where:
            bogusArgs << [['bogus_argument'],
                          ['several', 'bogus', 'arguments']]
    }

    def 'Props2Etcd called happily with mandatory args'(){
        given:
            String[] mandatoryArgs = ['-d', 'dir', '-f', 'file']
            Props2EtcdRunner testObj = new Props2EtcdRunner(mandatoryArgs)
            Props2EtcdCore props2EtcdMock = Mock()
            testObj.props2Etcd = props2EtcdMock

        when:
            testObj.run()

        then:
            1 * props2EtcdMock.doIt('dir', 'file', false, false)
    }

    def 'Props2Etcd called happily with all args'(){
        given:
            String[] mandatoryArgs = ['-d', 'dir', '-f', 'file', '-e', 'http://endpoint:port', '-r']
            Props2EtcdRunner testObj = new Props2EtcdRunner(mandatoryArgs)
            Props2EtcdCore props2EtcdMock = Mock()
            testObj.props2Etcd = props2EtcdMock

        when:
            testObj.run()

        then:
            1 * props2EtcdMock.doIt('dir', 'file', 'http://endpoint:port', true)
    }

    def 'Props2Etcd throws unhandled exception'(){
        given:
            String[] mandatoryArgs = ['-d', 'dir', '-f', 'file']
            Props2EtcdRunner testObj = new Props2EtcdRunner(mandatoryArgs)
            Props2EtcdCore props2EtcdMock = Mock()
            testObj.props2Etcd = props2EtcdMock

        and:
            props2EtcdMock.doIt(_, _, _, _) >> {throw new Exception("unhandled exception")}

        when:
            testObj.run()

        then:
            thrown(Exception)
    }

    @Unroll
    def 'Props2Etcd throws handled exception (#theException) and prints message'(){
        given:
            String[] mandatoryArgs = ['-d', 'dir', '-f', 'file']
            Props2EtcdRunner testObj = new Props2EtcdRunner(mandatoryArgs)
            Props2EtcdCore props2EtcdMock = Mock()
            testObj.props2Etcd = props2EtcdMock

        and:
            props2EtcdMock.doIt(_, _, _, _) >> {throw theException}

        when:
            testObj.run()

        then:
            out.toString().trim() == 'message'

        where:
            theException << [
                new FileNotFoundException('message'),
                new IllegalArgumentException('message'),
                new UnsupportedFileTypeException('message')
            ]
    }

    private boolean usageIsPrinted(boolean withError = false) {
        assert out.toString().trim() == (withError ? USAGE_WITH_ERROR : USAGE)
        true
    }
}
