package me.jkowalc.zephyr;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import me.jkowalc.zephyr.analizer.StaticAnalizer;
import me.jkowalc.zephyr.domain.node.program.Program;
import me.jkowalc.zephyr.domain.token.Token;
import me.jkowalc.zephyr.domain.token.TokenType;
import me.jkowalc.zephyr.exception.ZephyrInternalException;
import me.jkowalc.zephyr.exception.ZephyrException;
import me.jkowalc.zephyr.interpreter.Interpreter;
import me.jkowalc.zephyr.lexer.Lexer;
import me.jkowalc.zephyr.parser.ASTPrinter;
import me.jkowalc.zephyr.parser.CommentFilter;
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
            Parser parser = new Parser(new CommentFilter(lexer));
            Program program = parser.parseProgram();
            if(parser.nextNotParsed().getType() != TokenType.EOF){
                Token next = parser.nextNotParsed();
                System.err.println("Unexpected " + next.getClass().getSimpleName() + " at " + next.getStartPosition());
                System.exit(1);
            }
            if(parseOnly) {
                StaticAnalizer staticAnalizer = new StaticAnalizer();
                program.accept(staticAnalizer);
                ASTPrinter printer = new ASTPrinter(System.out, 4);
                program.accept(printer);
            }
            else {
                Interpreter interpreter = new Interpreter(new OutputStreamWriter(System.out));
                program.accept(interpreter);
            }

        }
        catch(IOException e){
            System.err.println("IO Error");
            System.exit(1);
        }
        catch(ZephyrException e){
            System.err.println(e.toString());
            System.exit(1);
        }
        catch(ZephyrInternalException e){
            System.err.println("Zephyr internal error");
            System.exit(1);
        }
    }
}