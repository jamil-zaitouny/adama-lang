package org.adamalang.clikit.codegen;

import org.adamalang.clikit.model.*;
import org.adamalang.clikit.model.Common;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CliElementGen {

    public static String generate(Map<String, ArgDefinition> argDefinitions, Group[] groups, Command[] mainCommands, String packageName) {
        StringBuilder sb = new StringBuilder();
        sb.append("package ").append(packageName).append(";\n\n");
        sb.append("import org.adamalang.cli.runtime.ArgumentItem;\n");
        sb.append("import java.util.HashMap;\n");
        sb.append("import java.util.Map;\n\n");
        sb.append("public class CliElement {\n");
        sb.append("  public String name;\n");
        sb.append("  public String doc;\n");
        sb.append("  public Map<String, CliElement> Commands = new HashMap<>();\n");
        sb.append("  public Map<String, ArgumentItem> Arguments = new HashMap<>();\n");
        sb.append("  public static Map<String, ArgumentItem> fullArgumentList = populateArgs();\n");
        sb.append("  public static Map<String, CliElement> Groups = populateGroups();\n\n");
        sb.append("  public CliElement(HashMap<String, CliElement> commands, String name, String type, String doc) {\n");
        sb.append("    this.name = name;\n");
        sb.append("    this.doc = doc;\n");
        sb.append("    if (type.equals(\"group\")) {\n");
        sb.append("      Commands = commands;\n");
        sb.append("    }\n");
        sb.append("  }\n\n");
        sb.append("  public CliElement(HashMap<String, ArgumentItem> arguments, String name, String doc) {\n");
        sb.append("    this.doc = doc;\n");
        sb.append("    this.name = name;\n");
        sb.append("    Arguments = arguments;\n");
        sb.append("  }\n");
        sb.append("  public static HashMap<String, CliElement> populateGroups() {\n");
        sb.append("    HashMap<String, CliElement> returnMap = new HashMap<>();\n\n");
        for (Command command : mainCommands) {
            sb.append("    HashMap<String, ArgumentItem> ").append(command.camel).append("Args = new HashMap<>();\n");
            for (Argument argument : command.argList) {
                sb.append("    ").append(command.camel).append("Args.put(\"--").append(argument.name);
                if (argument.optional)
                    sb.append("\", ArgumentItem.setOptionalFromMap(fullArgumentList, \"--").append(argument.name).append("\", \"").append(argument.defaultValue).append("\"));\n");
                else
                    sb.append("\", fullArgumentList.get(\"--").append(argument.name).append("\"));\n");
            }
            sb.append("    returnMap.put(\"").append(command.name).append("\", new CliElement(").append(command.camel).append("Args, \"")
                    .append(command.name).append("\", \"").append(Common.escape(command.documentation)).append("\"));\n\n");

        }
        for (Group group : groups) {
            sb.append("    HashMap<String, CliElement> ").append(group.name).append("Commands = new HashMap<>();\n");
            for (Command command : group.commandList) {
                sb.append("    HashMap<String, ArgumentItem> ").append(command.camel).append(group.capName).append("Args = new HashMap<>();\n");
                for (Argument argument : command.argList) {
                    sb.append("    ").append(command.camel).append(group.capName).append("Args.put(\"--").append(argument.name);
                    if (argument.optional)
                        sb.append("\", ArgumentItem.setOptionalFromMap(fullArgumentList, \"--").append(argument.name).append("\", \"").append(argument.defaultValue).append("\"));\n");
                    else
                        sb.append("\", fullArgumentList.get(\"--").append(argument.name).append("\"));\n");
                }
                sb.append("    ").append(group.name).append( "Commands.put(\"").append(command.name).append("\", ");
                sb.append("new CliElement(").append(command.camel).append(group.capName).append("Args, \"").append(command.name).append("\", \"").append(Common.escape(command.documentation)).append("\"));\n\n");

            }
            sb.append("    returnMap.put(\"").append(group.name).append("\", new CliElement(").append(group.name).append("Commands, \"")
                    .append(group.name).append("\", \"group\", \"").append(Common.escape(group.documentation)).append("\"));\n\n");
        }

        sb.append("    return returnMap;\n");
        sb.append("  }\n\n");
        sb.append("  public static HashMap<String, ArgumentItem> populateArgs() {\n");
        sb.append("    HashMap<String, ArgumentItem> argList = new HashMap<>();\n\n");
        for (Map.Entry<String, ArgDefinition> entry : argDefinitions.entrySet()) {
            ArgDefinition argDef = entry.getValue();
            String argName = entry.getKey();
            sb.append("    argList.put(\"--").append(argName).append("\",new ArgumentItem(\"--").append(argName).append("\", \"-").append(argDef.shortField);
            sb.append("\", \"").append(Common.escape(argDef.documentation)).append("\"));\n");
        }
        sb.append("    return argList;\n");
        sb.append("  }\n");
        sb.append("}");

        return sb.toString();
    }
}
