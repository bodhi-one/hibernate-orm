/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.dialect.function.array;

import java.util.List;

import org.hibernate.metamodel.mapping.JdbcMapping;
import org.hibernate.metamodel.mapping.JdbcMappingContainer;
import org.hibernate.query.ReturnableType;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.sql.ast.tree.SqlAstNode;
import org.hibernate.sql.ast.tree.expression.Expression;
import org.hibernate.type.BasicPluralType;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Special array contains function that also applies a cast to the element argument. PostgreSQL needs this,
 * because by default it assumes a {@code text[]}, which is not compatible with {@code varchar[]}.
 */
public class ArrayContainsOperatorFunction extends ArrayContainsUnnestFunction {

	public ArrayContainsOperatorFunction(boolean nullable, TypeConfiguration typeConfiguration) {
		super( nullable, typeConfiguration );
	}

	@Override
	public void render(
			SqlAppender sqlAppender,
			List<? extends SqlAstNode> sqlAstArguments,
			ReturnableType<?> returnType,
			SqlAstTranslator<?> walker) {
		final Expression haystackExpression = (Expression) sqlAstArguments.get( 0 );
		final Expression needleExpression = (Expression) sqlAstArguments.get( 1 );
		final JdbcMappingContainer needleTypeContainer = needleExpression.getExpressionType();
		final JdbcMapping needleType = needleTypeContainer == null ? null : needleTypeContainer.getSingleJdbcMapping();
		if ( needleType == null || needleType instanceof BasicPluralType<?, ?> ) {
			if ( nullable ) {
				super.render( sqlAppender, sqlAstArguments, returnType, walker );
			}
			else {
				haystackExpression.accept( walker );
				sqlAppender.append( "@>" );
				needleExpression.accept( walker );
			}
		}
		else {
			if ( nullable ) {
				sqlAppender.append( "array_position(" );
				haystackExpression.accept( walker );
				sqlAppender.append( ',' );
				needleExpression.accept( walker );
				sqlAppender.append( ") is not null" );
			}
			else {
				haystackExpression.accept( walker );
				sqlAppender.append( "@>" );
				if ( needsArrayCasting( needleExpression ) ) {
					sqlAppender.append( "cast(array[" );
					needleExpression.accept( walker );
					sqlAppender.append( "] as " );
					sqlAppender.append( DdlTypeHelper.getCastTypeName(
							haystackExpression.getExpressionType(),
							walker
					) );
					sqlAppender.append( ')' );
				}
				else {
					sqlAppender.append( "array[" );
					needleExpression.accept( walker );
					sqlAppender.append( ']' );
				}
			}
		}
	}

	private static boolean needsArrayCasting(Expression elementExpression) {
		// PostgreSQL doesn't do implicit conversion between text[] and varchar[], so we need casting
		return elementExpression.getExpressionType().getSingleJdbcMapping().getJdbcType().isString();
	}
}
