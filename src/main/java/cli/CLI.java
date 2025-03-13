package cli;

import picocli.CommandLine;

@CommandLine.Command(name = "miniGit", mixinStandardHelpOptions = true,
        subcommands = {
                InitCommand.class,
                HashObjectCommand.class,
                CatFileCommand.class,
                WriteTreeCommand.class,
                ReadTreeCommand.class,
                CommitCommand.class,
                LogCommand.class
        })
public class CLI {
    public static void run() {
        System.out.println("Hello world!");
    }
}
