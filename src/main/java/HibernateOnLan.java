import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static spark.Spark.*;

public class HibernateOnLan {
    public static void main(String[] args) {
        Options opts = new Options();

        Option port = new Option("p", "port", true, "Port");
        port.setRequired(false);
        opts.addOption(port);

        Option command = new Option("c", "command", true, "Sleep command");
        command.setRequired(false);
        opts.addOption(command);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String shellCommand;

        try {
            cmd = parser.parse(opts, args);
            if (cmd.getOptionValue("port") != null) {
                port(Integer.valueOf(cmd.getOptionValue("port")));
            }
            if (cmd.getOptionValue("command") != null) {
                shellCommand = cmd.getOptionValue("command");
            } else {
//                shellCommand = "psshutdown -d -t 0";
                shellCommand = "shutdown -h";
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", opts);

            System.exit(1);
            return;
        }

        get("/rest", (req, res) -> {

            System.out.println("Received get request for rest");

            String output = executeShellCommand(shellCommand);

            System.out.println(output);
            return output;
        });

        System.out.println("Started server, listening on port: " + port());

    }

    private static String executeShellCommand(String command) {

        StringBuilder output = new StringBuilder();

        Process p;
        System.out.println("Attempting to run command");
        try {
            p = Runtime.getRuntime().exec(command);
            System.out.println("Waiting for process");
            p.waitFor();
            System.out.println("Running command");
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Putting PC to sleep");

        return output.toString();

    }
}
