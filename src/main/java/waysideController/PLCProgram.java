package waysideController;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import org.antlr.v4.runtime.tree.ParseTree;
import waysideController.plc_parser.*;

import java.io.IOException;
import java.util.Map;

public class PLCProgram extends AbstractParseTreeVisitor<Value> implements PLCVisitor<Value> {

    private final Map<Integer, WaysideBlock> blockMap;
    private final PLCRunner controller;
    private ParseTree PLCTree;

    public PLCProgram(PLCRunner controller) {
        this.controller = controller;
        this.blockMap = controller.getBlockMap();
    }

    public void loadPLC(String filename) {
        try {
            PLCLexer lexer = new PLCLexer(CharStreams.fromFileName(filename));
            PLCParser parser = new PLCParser(new CommonTokenStream(lexer));
            PLCTree = parser.program();
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if(PLCTree != null)
            visit(PLCTree);
    }

    private void setSwitch(int blockID, boolean switchState) {
        blockMap.get(blockID).setSwitchState(switchState);
        controller.setSwitchPLC(blockID, switchState);
    }

    private void setLight(int blockID, boolean lightState) {
        blockMap.get(blockID).setLightState(lightState);
        controller.setTrafficLightPLC(blockID, lightState);
    }

    private void setCrossing(int blockID, boolean crossingState) {
        blockMap.get(blockID).setCrossingState(crossingState);
        controller.setCrossingPLC(blockID, crossingState);
    }

    private void setAuth(int blockID, boolean auth) {
        blockMap.get(blockID).setBooleanAuth(auth);
        controller.setAuthorityPLC(blockID, auth);
    }

    @Override
    public Value visitProgram(PLCParser.ProgramContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitStatement(PLCParser.StatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitSet_list_value(PLCParser.Set_list_valueContext ctx) {
        int index = visit(ctx.index()).asInteger();
        String listName = visit(ctx.list_name()).asString();
        boolean value = visit(ctx.getChild(5)).asBoolean();

        System.out.println(listName + "[" + index + "] = " + value);

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
            default:
                throw new RuntimeException("Unknown list name: " + listName);
        }

        return new Value(value);
    }

    @Override
    public Value visitRequest_direction(PLCParser.Request_directionContext ctx) {
        return null;
    }

    @Override
    public Value visitIf_statement(PLCParser.If_statementContext ctx) {
        boolean conditional = visit(ctx.getChild(1)).asBoolean();

        if(conditional) {
            for(PLCParser.StatementContext statement : ctx.statement()) {
                visit(statement);
            }
        }
        return new Value(conditional);
    }

    @Override
    public Value visitIf_else_statement(PLCParser.If_else_statementContext ctx) {
        boolean conditional = visit(ctx.getChild(1)).asBoolean();

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
        return new Value(conditional);
    }

    @Override
    public Value visitEquality_check(PLCParser.Equality_checkContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitEquals_statement(PLCParser.Equals_statementContext ctx) {
        boolean left = visit(ctx.compound_value(0)).asBoolean();
        boolean right = visit(ctx.getChild(2)).asBoolean();
        return new Value(left == right);
    }

    @Override
    public Value visitNot_equals_statement(PLCParser.Not_equals_statementContext ctx) {
        boolean left = visit(ctx.compound_value(0)).asBoolean();
        boolean right = visit(ctx.getChild(2)).asBoolean();
        return new Value(left != right);
    }

    @Override
    public Value visitCompound_value(PLCParser.Compound_valueContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitOr_operator(PLCParser.Or_operatorContext ctx) {
        for(int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if(child instanceof PLCParser.Single_valContext | child instanceof PLCParser.Compound_valueContext | child instanceof PLCParser.And_operatorContext) {
                if (visit(child).asBoolean()) {
                    return new Value(true);
                }
            }
        }

        return new Value(false);
    }

    @Override
    public Value visitAnd_operator(PLCParser.And_operatorContext ctx) {
        for(int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if(child instanceof PLCParser.Single_valContext | child instanceof PLCParser.Compound_valueContext) {
                if (!visit(child).asBoolean()) {
                    return new Value(false);
                }
            }
        }

        return new Value(true);
    }

    @Override
    public Value visitSingle_val(PLCParser.Single_valContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitNot_operator(PLCParser.Not_operatorContext ctx) {
        Boolean right = visit(ctx.list_value()).asBoolean();
        return new Value(!right);
    }

    @Override
    public Value visitList_value(PLCParser.List_valueContext ctx) {
        int index = visit(ctx.index()).asInteger();
        String listName = visit(ctx.list_name()).asString();

        switch(listName) {
            case "occupied":
                return new Value(blockMap.get(index).isOccupied());
            case "crossing":
                return new Value(blockMap.get(index).getCrossingState());
            case "light":
                return new Value(blockMap.get(index).getLightState());
            case "switch":
                return new Value(blockMap.get(index).getSwitchState());
            case "authority":
                return new Value(blockMap.get(index).getBooleanAuth());
            case "direction":
                return new Value(blockMap.get(index).getDirection());
            case "dir_assigned":
                return new Value(blockMap.get(index).isDir_assigned());
            default:
                throw new RuntimeException("Unknown list name: " + listName);
        }
    }

    @Override
    public Value visitIndex(PLCParser.IndexContext ctx) {
        return new Value(Integer.parseInt(ctx.getText()));
    }

    @Override
    public Value visitList_name(PLCParser.List_nameContext ctx) {
        return new Value(ctx.getText());
    }

    @Override
    public Value visitValue_false(PLCParser.Value_falseContext ctx) {
        return new Value(false);
    }

    @Override
    public Value visitValue_true(PLCParser.Value_trueContext ctx) {
        return new Value(true);
    }
}
