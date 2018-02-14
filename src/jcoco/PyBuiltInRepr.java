/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * This is the built-in len function. It is called on a sequence of some sort. 
 * The len function calls the __len__ attribute (i.e. method) of the type of 
 * its argument. In this way the type controls the behavior of the len 
 * built-in function. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyBuiltInRepr extends PyCallableAdapter {

    public PyBuiltInRepr() {
        super();
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override
    public String str() {
        return "<built-in function repr>";
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        PyObject x;
        
        if (args.size() != 1) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                    "TypeError: expected 1 argument, got "+args.size());
        }
        
        x = args.get(0);
        
        ArrayList<PyObject> callArgs = new ArrayList<PyObject>();
        
        return x.callMethod(callStack,"__repr__", callArgs);
    }
    
}
