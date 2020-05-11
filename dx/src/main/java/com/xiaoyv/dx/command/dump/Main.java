

package com.xiaoyv.dx.command.dump;

import com.xiaoyv.dex.util.FileUtils;
import com.xiaoyv.dx.cf.iface.ParseException;
import com.xiaoyv.dx.util.HexParser;

import java.nio.charset.StandardCharsets;

/**
 * Main class for the class file dumper.
 */
public class Main {

    static Args parsedArgs = new Args();

    /**
     * This class is uninstantiable.
     */
    private Main() {
        // This space intentionally left blank.
    }

    /**
     * Run!
     */
    public static void main(String[] args) {
        int at = 0;

        for (/*at*/; at < args.length; at++) {
            String arg = args[at];
            if (arg.equals("--") || !arg.startsWith("--")) {
                break;
            } else if (arg.equals("--bytes")) {
                parsedArgs.rawBytes = true;
            } else if (arg.equals("--basic-blocks")) {
                parsedArgs.basicBlocks = true;
            } else if (arg.equals("--rop-blocks")) {
                parsedArgs.ropBlocks = true;
            } else if (arg.equals("--optimize")) {
                parsedArgs.optimize = true;
            } else if (arg.equals("--ssa-blocks")) {
                parsedArgs.ssaBlocks = true;
            } else if (arg.startsWith("--ssa-step=")) {
                parsedArgs.ssaStep = arg.substring(arg.indexOf('=') + 1);
            } else if (arg.equals("--debug")) {
                parsedArgs.debug = true;
            } else if (arg.equals("--dot")) {
                parsedArgs.dotDump = true;
            } else if (arg.equals("--strict")) {
                parsedArgs.strictParse = true;
            } else if (arg.startsWith("--width=")) {
                arg = arg.substring(arg.indexOf('=') + 1);
                parsedArgs.width = Integer.parseInt(arg);
            } else if (arg.startsWith("--method=")) {
                arg = arg.substring(arg.indexOf('=') + 1);
                parsedArgs.method = arg;
            } else {
                System.err.println("unknown option: " + arg);
                throw new RuntimeException("usage");
            }
        }

        if (at == args.length) {
            System.err.println("no input files specified");
            throw new RuntimeException("usage");
        }

        for (/*at*/; at < args.length; at++) {
            try {
                String name = args[at];
                System.out.println("reading " + name + "...");
                byte[] bytes = FileUtils.readFile(name);
                if (!name.endsWith(".class")) {
                    String src;
                    src = new String(bytes, StandardCharsets.UTF_8);
                    bytes = HexParser.parse(src);
                }
                processOne(name, bytes);
            } catch (ParseException ex) {
                System.err.println("\ntrouble parsing:");
                if (parsedArgs.debug) {
                    ex.printStackTrace();
                } else {
                    ex.printContext(System.err);
                }
            }
        }
    }

    /**
     * Processes one file.
     *
     * @param name  {@code non-null;} name of the file
     * @param bytes {@code non-null;} contents of the file
     */
    private static void processOne(String name, byte[] bytes) {
        if (parsedArgs.dotDump) {
            DotDumper.dump(bytes, name, parsedArgs);
        } else if (parsedArgs.basicBlocks) {
            BlockDumper.dump(bytes, System.out, name, false, parsedArgs);
        } else if (parsedArgs.ropBlocks) {
            BlockDumper.dump(bytes, System.out, name, true, parsedArgs);
        } else if (parsedArgs.ssaBlocks) {
            // --optimize ignored with --ssa-blocks
            parsedArgs.optimize = false;
            SsaDumper.dump(bytes, System.out, name, parsedArgs);
        } else {
            ClassDumper.dump(bytes, System.out, name, parsedArgs);
        }
    }
}