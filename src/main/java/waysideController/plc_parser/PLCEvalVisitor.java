package waysideController.plc_parser;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;
import waysideController.WaysideBlock;

import java.util.Map;

public class PLCEvalVisitor extends AbstractParseTreeVisitor<Value> implements PLCVisitor<Value> {

    private final Map<Integer, WaysideBlock> blockMap;

    public PLCEvalVisitor(Map<Integer, WaysideBlock> blockMap) {
        this.blockMap = blockMap;
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
            case "occupied":
                blockMap.get(index).setOccupied(value);
                break;
            case "crossing":
                blockMap.get(index).setCrossingState(value);
                break;
            case "light":
                blockMap.get(index).setLightState(value);
                break;
            case "switch":
                blockMap.get(index).setSwitchState(value);
                break;
            case "switch_request":
                blockMap.get(index).setSwitchRequest(value);
                break;
            case "authority":
                blockMap.get(index).setAuthority(value);
                break;
            default:
                throw new RuntimeException("Unknown list name: " + listName);
        }

        return new Value(value);
    }

    @Override
    public Value visitIf_statement(PLCParser.If_statementContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Value visitIf_else_statement(PLCParser.If_else_statementContext ctx) {
        return visitChildren(ctx);
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
        for(PLCParser.Single_valContext singleVal : ctx.single_val()) {
            if(visit(singleVal).asBoolean()) {
                return new Value(true);
            }
        }

        return new Value(false);
    }

    @Override
    public Value visitAnd_operator(PLCParser.And_operatorContext ctx) {
        for(PLCParser.Single_valContext singleVal : ctx.single_val()) {
            if(!visit(singleVal).asBoolean()) {
                return new Value(false);
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
            case "switch_request":
                return new Value(blockMap.get(index).getSwitchRequest());
            case "authority":
                return new Value(blockMap.get(index).getAuthority());
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
