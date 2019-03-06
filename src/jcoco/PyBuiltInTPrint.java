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
 * This built-in function tprint operates similarly to the fprint and 
 * print functions. Like fprint, tprint takes exactly one argument, a tuple, 
 * which can contain as many arguments as desired. Like print, the tprint
 * function returns None. There is one "tprint" function which is created
 * when CoCo starts and "tprint" is mapped to it in the globals. 
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyBuiltInTPrint extends PyCallableAdapter {

    public PyBuiltInTPrint() {
        super();
    }
    
    @Override 
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override
    public String str() {
        return "<built-in function tprint>";
    }
    
    private String process(String s) {
        StringBuffer ss = new StringBuffer();
        boolean escape = false;
        
        for (int i = 0; i < s.length(); i++) {
            if (escape) {
                switch (s.charAt(i)) {
                    case 'n':
                        ss.append("\n");
                        break;
                    case 't':
                        ss.append("\t");
                        break;
                    default:
                        ss.append(s.charAt(i));
                        break;
                }
                escape = false;
            } else {
                escape = (s.charAt(i) == '\\');
                
                if (!escape) {
                    ss.append(s.charAt(i));
                }
            }
        }
        return ss.toString();
    }
    
    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        String output = "";
        PyObject x;
        PyObject w;
        ArrayList<PyObject> strargs = new ArrayList<PyObject>();
        
        if (args.size() != 1) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                                    "TypeError: expected 1 argument, got "+args.size());
        }
        
        PyObject arg = args.get(0);
        
        if (arg.getType().typeId() == PyTypeId.PyTupleType) {
            PyTuple tup = (PyTuple) arg;
                                
            for (int i = tup.size()-1; i >= 0; i--) {
                x = tup.getVal(i);
                w = x.callMethod(callStack,"__str__", strargs);
                if (x.getType().typeId() == PyTypeId.PyStrType) {
                    output = process(w.str()) + output;
                } else {
                    output = w.str() + output;
                }
                
                if (i>0){
                    output = " "+output;
                }
            }
        } else {
            output = arg.str();
        }
        
        System.out.println(output);
        
        return new PyNone();
    }
    
}
