package me.jkowalc.zephyr;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.lexer.Lexer;
import me.jkowalc.zephyr.parser.ASTPrinter;
import me.jkowalc.zephyr.parser.Parser;

import java.io.*;

public class Main {
    @Parameter(names = {"--parse-only"}, description = "Parse only, do not run the program")
    private boolean parseOnly = false;

    @Parameter(names = {"--input", "-i"}, description = "Input file", converter = FileConverter.class, required = true)
    private File inputFile;

    public static void main(String ... argv){
        Main main = new Main();
        JCommander.newBuilder()
                .addObject(main)
                .build()
                .parse(argv);
        main.run();
    }

    public void run(){
        Lexer lexer = null;
        try{
            lexer = new Lexer(new InputStreamReader(new FileInputStream(inputFile)));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + inputFile);
            System.exit(1);
        } catch(IOException e) {
            System.err.println("IO Error");
            System.exit(1);
        }
        try {
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();
            ASTPrinter printer = new ASTPrinter(System.out, 4);
            program.accept(printer);
        }
        catch(IOException e){
            System.err.println("IO Error");
            System.exit(1);
        }
        catch(ZephyrException e){
            System.err.println(e.toString());
            System.exit(1);
        }
    }
}