/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Mar 3, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyType.PyTypeId;

public class PySuperType extends PyType {
    
    public PySuperType() {
        super("super", PyType.PyTypeId.PySuperTypeId); 
    }
       
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PySuperTypeId);
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        
        if (args.size() != 0) {
            throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                    "TypeError: expected 0 argument, got "+args.size());
        }
        
        PyFrame topFrame = callStack.peek();
        if (topFrame.getCode().getLocals().get(0).equals("self")) {
            PyObjectInst obj = (PyObjectInst)topFrame.getLocals().get("self");
            return new PySuper(obj);
        }
        
        throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "No super class defined here.");
    }
}
