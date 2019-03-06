/**
 * PyBuiltInConcat.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * PyBuiltinConcat is a built-in function (i.e. PyCallable) for the 
 * JCoCo concat functionality. This built-in function takes an argument and 
 * calls the "concat" attribute that is associated with the type of the 
 * argument. Currently, only PyFunList objects support concatenation. List
 * elements must be concatable. Likely the elements of the PyFunList are 
 * strings which can be concatenated.
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;


public class PyBuiltInConcat extends PyCallableAdapter {

    public PyBuiltInConcat() {
        super();
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override 
    public String str() {
        return "<built-in function concat>";
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        if (args.size() != 1) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                    "TypeError: expected 1 argument, got "+args.size());
        }
        
        PyObject x = args.get(0);
        
        ArrayList<PyObject> callArgs = new ArrayList<PyObject>();
        
        return x.callMethod(callStack,"concat", callArgs);
    }
}
