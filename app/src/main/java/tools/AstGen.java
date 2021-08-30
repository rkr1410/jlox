package tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AstGen {
    private static String indent = "    ";

    public static void main(String[] args) throws IOException {
//        if (args.length != 1) {
//            System.err.println("Usage: generate_ast <output directory>");
//            System.exit(Sysexits.EX_USAGE);
//        }
//        String outputDir = args[0];
        //String outputDir = "/Users/pafau/IdeaProjects/jlox/app/src/main/java/jlox";
        String outputDir = "C:\\stuff\\java\\jlox\\app\\src\\main\\java\\jlox";

        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign   : Token name, Expr value",
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right",
                "Variable : Token name"
        ));

        defineAst(outputDir, "Stmt", Arrays.asList(
            "Block      : List<Stmt> statements",
            "Expression : Expr expression",
            "Print      : Expr expression",
            "Var        : Token name, Expr initializer"
        ));
    }

    private static void defineAst(String outputDir, String baseName,
                                  List<String> types) throws IOException {
        var path = outputDir + "/" + baseName + ".java";
        var pw = new PrintWriter(path, Charset.defaultCharset());

        pw.println("package jlox;");
        pw.println();
        pw.println("import java.util.List;");
        pw.println();
        pw.println("abstract class " + baseName + " {");

        defineVisitor(pw, baseName, types);
        pw.println();

        pw.println(indent + "abstract <R> R accept(Visitor<R> visitor);");
        pw.println();

        for (var type : types) {
            var defs =
                    Arrays.stream(type.split(":"))
                            .map(String::trim)
                            .toArray(String[]::new);
            defineType(pw, baseName, defs[0], defs[1]);
            pw.println();
        }
        pw.println("}");
        pw.close();
    }

    private static void defineVisitor(PrintWriter pw, String baseName,
                                      List<String> types) {
        pw.println(indent + "interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            pw.println(indent.repeat(2) + "R visit" + typeName + baseName + "("
                    + typeName + " " + baseName.toLowerCase() + ");");
        }

        pw.println(indent + "}");
    }

    private static void defineType(PrintWriter pw, String baseName,
                                   String className, String fieldList) {
        var fields = fieldList.split(", ");

        pw.println(indent + "static class " + className + " extends " + baseName + " {");
        pw.println(indent.repeat(2) + className + "(" + fieldList + ") {");
        for (var field : fields) {
            var fieldName = field.split(" ")[1];
            pw.println(indent.repeat(3) + "this." + fieldName + " = " + fieldName + ";");
        }
        pw.println(indent.repeat(2) + "}");
        pw.println();

        pw.println(indent.repeat(2) + "@Override");
        pw.println(indent.repeat(2) + "<R> R accept(Visitor<R> visitor) {");
        pw.println(indent.repeat(3) + "return visitor.visit" + className + baseName + "(this);");
        pw.println(indent.repeat(2) + "}");
        pw.println();

        for (var field : fields) {
            pw.println(indent.repeat(2) + field + ";");
        }
        pw.println(indent + "}");
    }
}
