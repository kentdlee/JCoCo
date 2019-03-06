/**
 * <fileName>
 * Author: Kent D. Lee (c) 2017 Created on Jan 1, 2017.
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

public class PyBuiltInLen extends PyCallableAdapter {

    public PyBuiltInLen() {
        super();
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyType.PyTypeId.PyBuiltInType);
    }

    @Override
    public String str() {
        return "<built-in function len>";
    }

    @Override
    public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
        ArrayList<PyObject> iterArgs = new ArrayList<PyObject>();
        PyObject x;

        if (args.size() != 1) {
            throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                    "TypeError: expected 1 arguments, got " + args.size());
        }

        x = args.get(0);

        return x.callMethod(callStack,"__len__", iterArgs);
    }
}
