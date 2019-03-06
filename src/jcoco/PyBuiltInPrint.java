/**
 * PybuiltInPrint.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The built-in print function converts each of its arguments to strings by
 * calling the __str__ attribute (i.e. method) on each of its arguments. In
 * this way, the type of each argument controls how the argument is converted
 * to a string. There is one PyBuiltInPrint object created and "print" is
 * mapped to it in the globals.
 */
package jcoco;

import java.util.ArrayList;
import jcoco.PyType.PyTypeId;

public class PyBuiltInPrint extends PyCallableAdapter {

    public PyBuiltInPrint() {
        super();
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyBuiltInType);
    }

    @Override
    public String str() {
        return "<built-in function print>";
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        String output = "";
        PyObject x;
        PyObject w;

        for (int i = 0; i < args.size(); i++) {
            ArrayList<PyObject> strargs = new ArrayList<PyObject>();
            x = args.get(i);
            w = x.callMethod(callStack,"__str__", strargs);
            output = w.str() + output;

            if (i < args.size() - 1) {
                output = " " + output;
            }
        }
        System.out.println(output);

        return new PyNone();
    }
}
