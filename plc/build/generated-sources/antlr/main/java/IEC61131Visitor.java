// Generated from IEC61131.g4 by ANTLR 4.9.3
package parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link IEC61131Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface IEC61131Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#pou}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPou(IEC61131Parser.PouContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(IEC61131Parser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#function_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_block(IEC61131Parser.Function_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#var_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_block(IEC61131Parser.Var_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#type_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_rule(IEC61131Parser.Type_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRange(IEC61131Parser.RangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#variable_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariable_declaration(IEC61131Parser.Variable_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(IEC61131Parser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#boolean_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_literal(IEC61131Parser.Boolean_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#numeric_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumeric_literal(IEC61131Parser.Numeric_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#integer_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInteger_literal(IEC61131Parser.Integer_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#floating_point_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloating_point_literal(IEC61131Parser.Floating_point_literalContext ctx);
	/**
	 * Visit a parse tree produced by {@link IEC61131Parser#string_literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitString_literal(IEC61131Parser.String_literalContext ctx);
}