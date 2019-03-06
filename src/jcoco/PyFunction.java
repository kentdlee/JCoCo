/**
 * PyFunction.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * In addition, when a nested function appears in 
 * a casm file, there is a PyFunction object created before the outer function
 * calls the inner function. If variables in the outer function are referenced
 * by the inner function, a closure is created which is also a PyFunction 
 * object. Read more about closures in the text. 
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyFunction extends PyCallableAdapter {
    protected PyCode code;
    protected HashMap<String, PyObject> globals;
    protected HashMap<String, PyCell> cellvars;
    
    public PyFunction(PyCode theCode, HashMap<String, PyObject> theGlobals, PyObject env) {
        PyTuple tuple = (PyTuple)env;
        this.cellvars = new HashMap<String, PyCell>();
        this.code = theCode;
        this.globals = theGlobals;
        
        for (int i = 0; i < theCode.getFreeVars().size(); i++) {
            this.cellvars.put(theCode.getFreeVars().get(i), (PyCell)tuple.getVal(i));
        }
        
        PyFunction self = this;
        this.dict.put("__call__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
                return self.__call__(callStack, args);
            }
        });
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        if (args.size() != this.code.getArgCount()) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, 
                                    "Type Error: expected "+this.code.getArgCount() + " arguments, got "+args.size());
        }
        
        PyFrame frame = new PyFrame(callStack, this.code, args, this.globals, this.code.getConsts(), this.cellvars);
        
        PyObject result = frame.execute();
        
        return result;
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyFunctionType);
    }
    
    public String callName() {
        return this.code.getName();
    }
    
    @Override
    public String str() {
        return "<function "+this.callName()+">";
    }
    
}
