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
 * The built-in fprint function operates like print except in two ways. The 
 * argument to fprint must be a tuple (which may contain only one item). The
 * second difference is that fprint returns an instance of itself to be used 
 * in further printing if so desired. Only one of these objects is created when
 * JCoCo starts and "fprint" is mapped to it in the globals.
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;


public class PyBuiltInFPrint extends PyCallableAdapter {

    public PyBuiltInFPrint() {
        super();
    }
    
    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }
    
    @Override
    public String str() {
        return "<built-in function fprint>";
    }
    
    public String processit(String s) {
        StringBuffer ss = new StringBuffer();
        boolean escape = false;
        
        for (int i=0; i < s.length(); i++) {
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
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args)  {
        String output = "";
        PyObject x;
        PyObject w;
        ArrayList<PyObject> strargs = new ArrayList<PyObject>();
        
        if (args.size() != 1) {
            throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, 
                                    "TypeError: expected 1 argument, got "+args.size());
        }
        
        PyObject arg = args.get(0);
        output = arg.str();
        
        if (arg.getType().typeId() == PyTypeId.PyStrType) {
            output = processit(output);
        }
        
        System.out.print(output);
        
        return this;
    }
    
}
