package me.jkowalc.zephyr;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.exception.LexicalException;
import me.jkowalc.zephyr.lexer.Lexer;

import java.io.*;

public class Main {
    @Parameter(names = {"--parse-only"}, description = "Parse only, do not run the program")
    private boolean parseOnly = false;

    @Parameter(names = {"--input", "-i"}, description = "Input file", converter = FileConverter.class, required = true)
    private File inputFile;

    public static void main(String ... argv) {
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(argv);
        main.run();
    }

    public void run() {
        Lexer lexer = null;
        try{
            lexer = new Lexer(new InputStreamReader(new FileInputStream(inputFile)));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + inputFile);
            System.exit(1);
        }
        Token token;
        try {
            do {
                token = lexer.nextToken();
                System.out.println(token);
            } while (token != null);
        } catch (LexicalException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            System.out.println("IO Error");
        }

    }
}