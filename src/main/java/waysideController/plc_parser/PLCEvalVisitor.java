package waysideController.plc_parser;

public class PLCEvalVisitor extends PLCBaseVisitor<Value> {

    @Override public Value visitProgram(PLCParser.ProgramContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitStatement(PLCParser.StatementContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitSet_list_value(PLCParser.Set_list_valueContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitIf_statement(PLCParser.If_statementContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitIf_else_statement(PLCParser.If_else_statementContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitEquality_check(PLCParser.Equality_checkContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitEquals_statement(PLCParser.Equals_statementContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitNot_equals_statement(PLCParser.Not_equals_statementContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitCompound_value(PLCParser.Compound_valueContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitOr_operator(PLCParser.Or_operatorContext ctx) {
        return visitChildren(ctx);
    }

    @Override public Value visitAnd_operator(PLCParser.And_operatorContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public Value visitSingle_val(PLCParser.Single_valContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public Value visitNot_operator(PLCParser.Not_operatorContext ctx) {
        Boolean right = visit(ctx.list_value()).asBoolean();
        return new Value(!right);
    }
    
    @Override public Value visitList_value(PLCParser.List_valueContext ctx) {
        return visitChildren(ctx);
    }
    
    @Override public Value visitValue_false(PLCParser.Value_falseContext ctx) {
        return new Value(false);
    }
    
    @Override public Value visitValue_true(PLCParser.Value_trueContext ctx) {
        return new Value(true);
    }
}
