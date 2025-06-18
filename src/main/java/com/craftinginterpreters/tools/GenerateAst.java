
package com.craftinginterpreters.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory");
      System.exit(64);
    }
    String outputDir = args[0];

    defineAst(outputDir, "Stmt", Arrays.asList(
        "Expression: Expr expression",
        "Print : Expr expression"));
  }

  private static void defineAst(
      String outputDir, String baseName, List<String> types) throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    System.out.println(path + "this is the -path");
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    writer.println("package com.craftinginterpreters.lox;");
    writer.println();
    writer.print("import java.util.List;");
    writer.println();
    writer.println();
    writer.println("abstract class " + baseName + " {");

    defineVisitor(writer, baseName, types);
    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);
    }
    // define the visitor expression
    writer.println();
    writer.println("  abstract <R> R accept(Visitor<R> v);");
    writer.println("}");
    writer.close();

  }

  private static void defineType(
      PrintWriter writer,
      String baseName,
      String className,
      String fieldList) {
    writer.println("   static class  " + className + " extends " + baseName + " {");

    // write the constructor
    writer.println("   " + className + "(" + fieldList + ") {");

    // store properties in fields.

    String[] fields = fieldList.split(", ");

    for (String field : fields) {

      String name = field.split(" ")[1];
      writer.println("   this." + name + " = " + name + ";");
    }
    writer.println("  }");

    writer.println();
    writer.println("  @Override");
    writer.println("    <R> R accept(Visitor<R> visitor) {");
    writer.println("  return visitor.visit" + className + baseName + "(this);");
    writer.println("}");

    writer.println();
    for (String field : fields) {
      writer.println("   final " + field + ";");
    }
    writer.println("  }");
  }

  private static void defineVisitor(
      PrintWriter writer, String baseName, List<String> types) {
    writer.println("   interface Visitor<R> {");

    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("  R visit" + typeName + baseName + "("
          + typeName + " " + baseName.toLowerCase() + ");");
    }
    writer.println(" }");
  }

}
