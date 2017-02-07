package utils

import exception.UnsupportedFileTypeException

class FileUtils {

    static File loadFileFromFilePath(String filepath) {
        String userDir = System.getProperty('user.dir')
        String absoluteFilePath = filepath.startsWith('/') ? filepath : "$userDir/$filepath"
        File file = new File(absoluteFilePath)
        if (!file.exists()) {throw new FileNotFoundException("Cannot find file at $absoluteFilePath")}
        file
    }

    static Properties loadPropertiesFromFile(File file) {

        if(!file.name.endsWith('.properties')){
            throw new UnsupportedFileTypeException('File extension must be of type .properties')
        }
        Properties properties = new Properties()
        file.withInputStream {
            properties.load(it)
        }
        properties
    }
}
