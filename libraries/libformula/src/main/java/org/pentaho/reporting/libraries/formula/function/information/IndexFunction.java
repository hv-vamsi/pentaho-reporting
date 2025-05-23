/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.formula.function.information;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

import java.math.BigDecimal;

public class IndexFunction implements Function {
  public IndexFunction() {
  }

  public String getCanonicalName() {
    return "INDEX";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 || parameterCount > 4 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final ArrayCallback arrayCallback =
      context.getTypeRegistry().convertToArray( parameters.getType( 0 ), parameters.getValue( 0 ) );
    if ( arrayCallback == null ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }

    final Type rowType = parameters.getType( 1 );
    final Object rowValue = parameters.getValue( 1 );
    Number rowNumber;
    if ( rowValue == null ) {
      rowNumber = null;
    } else {
      rowNumber = context.getTypeRegistry().convertToNumber( rowType, rowValue );
    }
    if ( rowNumber == null ) {
      rowNumber = new BigDecimal( 1 );
    } else if ( rowNumber.intValue() <= 0 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }
    if ( parameterCount == 3 ) {
      Number columnNumber;
      final Type columnType = parameters.getType( 2 );
      final Object columnValue = parameters.getValue( 2 );
      if ( columnValue == null ) {
        columnNumber = null;
      } else {
        columnNumber = context.getTypeRegistry().convertToNumber( columnType, columnValue );
      }
      if ( columnNumber == null ) {
        columnNumber = new BigDecimal( 1 );
      } else if ( columnNumber.intValue() <= 0 ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
      }
      return new TypeValuePair( AnyType.TYPE,
        arrayCallback.getValue( rowNumber.intValue() - 1, columnNumber.intValue() - 1 ) );
    } else {
      return new TypeValuePair( AnyType.TYPE, arrayCallback.getValue( rowNumber.intValue() - 1, 0 ) );
    }
  }
}
