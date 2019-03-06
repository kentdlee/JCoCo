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
 * The built-in iter function returns an iterator object for its argument
 * which must be iterable. This class implements the built-in function that is
 * called when iter() is called on an iterable of some sort. This function 
 * calls the __iter__ attribute on the type of its argument. In this way
 * the type controls how the built-in iter function behaves. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

/**
 *
 * @author Jonathan Opdahl
 */
public class PyBuiltInIter extends PyCallableAdapter {

    public PyBuiltInIter() {
        super();
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override
    public String str() {
        return "<built-in function iter>";
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        ArrayList<PyObject> iterArgs = new ArrayList<PyObject>();
        PyObject x;
        
        if (args.size() != 1) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                    "TypeError: expected 1 arguments, got "+args.size());
        }
        
        x =args.get(0);
        
        return x.callMethod(callStack,"__iter__", iterArgs);
    }
}

