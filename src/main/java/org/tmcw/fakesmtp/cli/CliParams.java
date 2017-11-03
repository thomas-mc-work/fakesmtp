package org.tmcw.fakesmtp.cli;

import java.net.InetAddress;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "java -jar fakesmtp-jar",
         description = "Fake SMTP server with configurable output.")
public class CliParams {

    private static final int DEFAULT_SMTP_PORT_NUMBER = 25;

    @Option(names = {"-h", "--help"}, description = "Print usage help", usageHelp = true)
    boolean usageHelpRequested;

    @Option(names = {"-o", "--output-path"}, description = "Emails output directory")
    Path outputPath;

    @Option(names = {"-c", "--cli-command"}, description = "Full path to an executable in the file system")
    String cliCommand;

    @Option(names = {"-p", "--port"}, description = "SMTP port number")
    int portNumber = DEFAULT_SMTP_PORT_NUMBER;

    @Option(names = {"-r", "--relay-domains"},
            description = "Comma separated email domain(s) for which relay is accepted. If specified, relays only emails "
            + "matching these domain(s), dropping (not saving) others (default: allow any domain)")
    List<String> relayDomains = new LinkedList<>();

    @Option(names = {"-b", "--bind-address"},
            description = "IP address or hostname to bind to.")
    InetAddress bindAddress = InetAddress.getLoopbackAddress();

    @Option(names = {"-v", "--verbose"}, description = "Enable DEBUG logging")
    boolean verbose;

    @Option(names = {"-V", "--version"}, description = "print version")
    boolean version;

}
