package waysideController;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import waysideController.plc_parser.PLCBaseVisitor;
import waysideController.plc_parser.PLCLexer;
import waysideController.plc_parser.PLCParser;
import waysideController.plc_parser.PLCVisitor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PLCProgram extends PLCBaseVisitor<Boolean> implements PLCVisitor<Boolean> {

    private final Map<Integer, WaysideBlock> blockMap;
    private final PLCRunner controller;
    private ParseTree PLCTree;
    private Map<String, Integer> intVarMap;
    private final Map<Integer, Boolean> dir_assignedMap;
    private final Map<Integer, Boolean> directionMap;

    public PLCProgram(PLCRunner controller) {
        this.controller = controller;
        this.blockMap = controller.getBlockMap();

        dir_assignedMap = new HashMap<>();
        directionMap = new HashMap<>();
    }

    public void loadPLC(String filename) {
        intVarMap = new HashMap<>();
        intVarMap.put("i", 1);
        try {
            PLCLexer lexer = new PLCLexer(CharStreams.fromFileName(filename));
            PLCParser parser = new PLCParser(new CommonTokenStream(lexer));
            PLCTree = parser.program(intVarMap);
//            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if(PLCTree != null) {
            visit(PLCTree);
//            System.out.println("PLC program executed");
        }
    }

    private void setSwitch(int blockID, boolean switchState) {
//        blockMap.get(blockID).setSwitchState(switchState);
        controller.setSwitchPLC(blockID, switchState);
    }

    private void setLight(int blockID, boolean lightState) {
//        blockMap.get(blockID).setLightState(lightState);
        controller.setTrafficLightPLC(blockID, lightState);
    }

    private void setCrossing(int blockID, boolean crossingState) {
//        blockMap.get(blockID).setCrossingState(crossingState);
        controller.setCrossingPLC(blockID, crossingState);
    }

    private void setAuth(int blockID, boolean auth) {
//        blockMap.get(blockID).setBooleanAuth(auth);
        controller.setAuthorityPLC(blockID, auth);
    }

    @Override
    public Boolean visitProgram(PLCParser.ProgramContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Boolean visitStatement_list(PLCParser.Statement_listContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Boolean visitStatement(PLCParser.StatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Boolean visitSet_list_value(PLCParser.Set_list_valueContext ctx) {
        int index = ctx.arith_expression().val;
        String listName = ctx.list_name().name;
        boolean value = visit(ctx.getChild(5));

//        System.out.println(listName + "[" + index + "] = " + value);

        switch (listName) {
            case "crossing":
                setCrossing(index, value);
                break;
            case "light":
                setLight(index, value);
                break;
            case "switch":
                setSwitch(index, value);
                break;
            case "authority":
                setAuth(index, value);
                break;
            case "direction":
                directionMap.put(index, value);
                break;
            case "dir_assigned":
                dir_assignedMap.put(index, value);
                break;
            default:
                throw new RuntimeException("Unknown list name: " + listName);
        }

        return value;
    }

    @Override
    public Boolean visitIf_statement(PLCParser.If_statementContext ctx) {
        boolean conditional = visit(ctx.getChild(1));

        if(conditional) {
            visit(ctx.statement_list());
        }
        return conditional;
    }

    @Override
    public Boolean visitIf_else_statement(PLCParser.If_else_statementContext ctx) {
        boolean conditional = visit(ctx.getChild(1));

        int index = 2;
        for(; index < ctx.getChildCount(); index++) {
            ParseTree statement = ctx.getChild(index);
            if(conditional && statement instanceof PLCParser.StatementContext)
                visit(statement);

            else if(statement.getText().equals("else"))
                break;
        }
        if(!conditional) {
            for(; index < ctx.getChildCount(); index++) {
                ParseTree statement = ctx.getChild(index);
                if(statement instanceof PLCParser.StatementContext)
                    visit(statement);
            }
        }
        return conditional;
    }



    public Boolean visitFor_statement(PLCParser.For_statementContext ctx) {

//        System.out.println("For loop: " + varName + " = " + startIndex + " to " + endIndex);

        for (int i = ctx.start; i <= ctx.end; i++) {
            intVarMap.put(ctx.name, i);
            visit(ctx.statement_list());
        }

        return false;
    }

    @Override
    public Boolean visitEquality_check(PLCParser.Equality_checkContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Boolean visitEquals_statement(PLCParser.Equals_statementContext ctx) {
        boolean left = visit(ctx.compound_value(0));
        boolean right = visit(ctx.getChild(2));
        return left == right;
    }

    @Override
    public Boolean visitNot_equals_statement(PLCParser.Not_equals_statementContext ctx) {
        boolean left = visit(ctx.compound_value(0));
        boolean right = visit(ctx.getChild(2));
        return left != right;
    }

    @Override
    public Boolean visitCompound_value(PLCParser.Compound_valueContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Boolean visitOr_operator(PLCParser.Or_operatorContext ctx) {
        for(int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if(child instanceof PLCParser.Single_valContext | child instanceof PLCParser.Compound_valueContext | child instanceof PLCParser.And_operatorContext) {
                if (visit(child)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Boolean visitAnd_operator(PLCParser.And_operatorContext ctx) {
        for(int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if(child instanceof PLCParser.Single_valContext | child instanceof PLCParser.Compound_valueContext) {
                if (!visit(child)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Boolean visitSingle_val(PLCParser.Single_valContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Boolean visitNot_operator(PLCParser.Not_operatorContext ctx) {
        Boolean right = visit(ctx.list_value());
        return !right;
    }

    @Override
    public Boolean visitList_value(PLCParser.List_valueContext ctx) {
        int index = ctx.arith_expression().val;
        String listName = ctx.list_name().name;

        switch(listName) {
            case "occupied":
                WaysideBlock occupancyBlock = blockMap.get(index);
                if(occupancyBlock != null)
                    return occupancyBlock.isOccupied();
                else {
                    return controller.getOutsideOccupancy(index);
                }
            case "crossing":
                return blockMap.get(index).getCrossingState();
            case "light":
                return blockMap.get(index).getLightState();
            case "switch":
                WaysideBlock switchBlock = blockMap.get(index);
                if(switchBlock != null)
                    return switchBlock.getSwitchState();
                else {
                    return controller.getOutsideSwitch(index);
                }
            case "authority":
                return blockMap.get(index).getBooleanAuth();
            case "direction":
                if(!directionMap.containsKey(index))
                    directionMap.put(index, false);
                return directionMap.get(index);
            case "dir_assigned":
                if(!dir_assignedMap.containsKey(index))
                    dir_assignedMap.put(index, false);
                return dir_assignedMap.get(index);
            default:
                throw new RuntimeException("Unknown list name: " + listName);
        }
    }

    @Override
    public Boolean visitArith_expression(PLCParser.Arith_expressionContext ctx) {
        return null;
    }

    @Override
    public Boolean visitInt_term(PLCParser.Int_termContext ctx) {
        return null;
    }

//    @Override
//    public Boolean visitInt_val(PLCParser.Int_valContext ctx) {
//        return null;
//    }

    @Override
    public Boolean visitInt_variable(PLCParser.Int_variableContext ctx) {
        return null;
    }

    @Override
    public Boolean visitList_name(PLCParser.List_nameContext ctx) {
        return null;
    }

    @Override
    public Boolean visitBool_literal(PLCParser.Bool_literalContext ctx) {
        return null;
    }

    @Override
    public Boolean visitValue_false(PLCParser.Value_falseContext ctx) {
        return null;
    }

    @Override
    public Boolean visitValue_true(PLCParser.Value_trueContext ctx) {
        return null;
    }
}
