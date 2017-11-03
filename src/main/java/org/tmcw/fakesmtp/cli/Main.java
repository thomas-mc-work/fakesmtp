package org.tmcw.fakesmtp.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.tmcw.fakesmtp.SMTPServerBuilder;
import org.tmcw.fakesmtp.SMTPServerHandler;
import org.tmcw.fakesmtp.spi.CommandLineHandler;
import org.tmcw.fakesmtp.spi.MailHandler;
import org.tmcw.fakesmtp.spi.SaveToFilesystem;
import picocli.CommandLine;

public final class Main {

    static SMTPServerBuilder smtpServerBuilder = new SMTPServerBuilder();

    /**
     * Checks command line arguments, sets some specific properties, and runs the main window.
     *
     * @param args a list of command line parameters.
     */
    public static void main(final String... args) {
        final CliParams cliParams = new CliParams();
        final CommandLine commandLine = new CommandLine(cliParams);
        commandLine.registerConverter(java.nio.file.Path.class, new CommandLine.ITypeConverter<Path>() {
            @Override
            public Path convert(String value) throws Exception {
                return Paths.get(value);
            }
        });

        try {
            commandLine.parse(args);

            if (cliParams.verbose) {
                ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
                        .setLevel(Level.DEBUG);
            }

            if (cliParams.usageHelpRequested) {
                CommandLine.usage(new CliParams(), System.out);
            } else if (cliParams.version) {
                System.out.println("fakesmtp " + Main.class.getPackage().getImplementationVersion());
            } else {
                goOn(cliParams);
            }
        } catch (CommandLine.ParameterException ex) {
            System.err.println("CLI error: " + ex.getMessage());
            System.err.println();
            CommandLine.usage(new CliParams(), System.err);
        }
    }

    private static void goOn(final CliParams cliParams) {
        final List<MailHandler> mailHandlerList = new ArrayList<>();

        if (null != cliParams.outputPath) {
            mailHandlerList.add(new SaveToFilesystem(cliParams.outputPath));
        }

        if (null != cliParams.cliCommand) {
            mailHandlerList.add(new CommandLineHandler(cliParams.cliCommand));
        }

        final SMTPServerHandler smtpServerHandler = new SMTPServerHandler(smtpServerBuilder.build(
                mailHandlerList,
                cliParams.relayDomains,
                cliParams.portNumber,
                cliParams.bindAddress
        ));

        // catch CTRL + c
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LoggerFactory.getLogger(getClass()).debug("SIGINT caught, invoking server stop");
                smtpServerHandler.stopServer();
            }
        });

        smtpServerHandler.startServer();
    }
}
