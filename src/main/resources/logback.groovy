import ch.qos.logback.classic.Level
import org.springframework.util.StringUtils
import org.yaml.snakeyaml.Yaml

class LoggingConfiguration {
    Level rootLoggingLevel
    Level applicationLoggingLevel
    String loggingFile
    String rootPackageName
    int maxHistorySize
    String maxFileSize

    final String FILE_APPENDER = "FILE"
    final String CONSOLE_APPENDER = "CONSOLE"

    LoggingConfiguration(String filename) {
        initAsPerExtension(filename)
    }

    private void initAsPerExtension(String filename) {
        def extension = StringUtils.getFilenameExtension(filename)
        switch(extension) {
            case "yaml":
            case "yml":
                this.init(createMap(filename))
                break
            case "properties":
                this.init(createProperties(filename))
                break
            default:
                throw new IllegalArgumentException("File type is not supported for logging configuration")
        }
    }

    private Map<String, Object> createMap(String filename) {
        def yaml = new Yaml()
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)
        return yaml.load(inputStream)
    }

    private Properties createProperties(String filename) {
        def properties = new Properties()
        new File(getClass().getClassLoader().getResource(filename).toURI()).withInputStream {
            stream -> properties.load(stream)
        }
        return properties
    }

    private void init(Properties properties) {
        this.rootPackageName = properties["application.root"]
        this.loggingFile = properties["application.logging.path"] + "/" + properties["application.logging.filename"]
        this.maxHistorySize = properties.getProperty("application.logging.max-history-size").toInteger()
        this.maxFileSize = properties["application.logging.max-file-size"]
        this.rootLoggingLevel = toLevel(properties.getProperty("logging.level.root"))
        this.applicationLoggingLevel = toLevel(properties.getProperty("logging.level.${rootPackageName}"))
    }

    private void init(Map<String, Object> map) {
        def appLogging = map["application"]["logging"]
        def logging = map["logging"]
        this.rootPackageName = map["application"]["root"]
        this.loggingFile = appLogging["path"] + "/" + appLogging["filename"]
        this.maxHistorySize = (int) appLogging["max-history-size"]
        this.maxFileSize = appLogging["max-file-size"]
        this.rootLoggingLevel = toLevel((String) logging["level"])
        this.applicationLoggingLevel = toLevel((String) logging[rootPackageName])
    }
}

def configuration = new LoggingConfiguration("application.yaml")

appender(configuration.FILE_APPENDER, RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${configuration.loggingFile}.%d{dd-MM-yyyy}.%i.log.zip"
        maxHistory = configuration.maxHistorySize
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = configuration.maxFileSize
        }
    }

    encoder(PatternLayoutEncoder) {
        pattern = "%date{dd MMM yyyy; HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
    }
}

appender(configuration.CONSOLE_APPENDER, ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "%date{dd MMM yyyy; HH:mm:ss.SSS} %-5level %logger{36} - %msg%n"
    }
}

logger(
        configuration.rootPackageName,
        configuration.applicationLoggingLevel,
        [configuration.FILE_APPENDER, configuration.CONSOLE_APPENDER],
        false
)

root(
        configuration.rootLoggingLevel,
        [configuration.FILE_APPENDER, configuration.CONSOLE_APPENDER]
)