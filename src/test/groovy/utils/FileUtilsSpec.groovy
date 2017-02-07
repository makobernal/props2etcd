package utils

import exception.UnsupportedFileTypeException
import spock.lang.Specification

class FileUtilsSpec extends Specification {

    def "loading a file that doesn't exist throws FileNotFoundException"(){
        when:
            FileUtils.loadFileFromFilePath('rubbish')

        then:
            thrown(FileNotFoundException)
    }

    def 'file can be loaded from relative filepath'() {
        given:
            def filePath = 'src/test/resources/test-file.properties'

        expect:
            FileUtils.loadFileFromFilePath(filePath).exists()
    }

    def 'file can be loaded from absolute filepath'() {
        given:
            def filePath = "${System.getProperty('user.dir')}/src/test/resources/test-file.properties"

        expect:
            FileUtils.loadFileFromFilePath(filePath).exists()
    }

    def 'properties file can be loaded from file'(){
        given:
            File file = FileUtils.loadFileFromFilePath('src/test/resources/test-file.properties')

        when:
            Properties properties = FileUtils.loadPropertiesFromFile(file)

        then:
            properties.prop1 == 'val1'
            properties.prop2 == 'val2'
    }

    def 'exception is throw when attempting to load properties from a non properties file'(){
        given:
            File file = FileUtils.loadFileFromFilePath('src/test/resources/test-file.yml')

        when:
            FileUtils.loadPropertiesFromFile(file)

        then:
            thrown(UnsupportedFileTypeException)
    }

}
