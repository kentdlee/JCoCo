/**
 * PyStr.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The JCoCo implementation of str objects.
 *
 * Not fully implemented.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;

public class PyStr extends PyPrimitiveTypeAdapter {

    private String val;

    public PyStr(String sVal) {
        super("str",PyTypeId.PyStrType);
        initMethods(funs());
        this.val = sVal;

    }

    @Override
    public String str() {
        return this.val;
    }

    public PyStr charAt(int index) {
        if (index >= this.val.length()) {
            throw new PyException(ExceptionType.PYSTOPITERATIONEXCEPTION, "Stop iteration");
        }

        return new PyStr(((Character) this.val.charAt(index)).toString());
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__add__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);
                PyStr other = null;
                try {
                    other = (PyStr) args.get(0);

                } catch (Exception ex) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "TypeError:operand'" + args.get(0).str() + " must be string in + expression.");
                }

                return new PyStr(self.str() + other.str());
            }
        });

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                return new PyInt(self.val.hashCode());
            }
        });

        funs.put("__float__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);
                Double x;

                try {
                    return new PyFloat(new Double(self.str()));
                } catch (Exception ex) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "could not convert string to float: '" + self.str() + "'");
                }
            }
        });

        funs.put("__int__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);
                Integer x;

                try {
                    return new PyInt(new Integer(self.str()));
                } catch (Exception ex) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "could not convert string to int: '" + self.str() + "'");
                }
            }
        });

        funs.put("__bool__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                try {
                    if (self.val.equals("")) {
                        return new PyBool(false);
                    }

                    return new PyBool(true);

                } catch (Exception ex) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "could not convert string to bool: '" + self.str() + "'");
                }
            }
        });

        funs.put("__funlist__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);
                PyFunList result = new PyFunList();
                int k;

                try {
                    for (k = self.val.length() - 1; k >= 0; k--) {
                        result = new PyFunList(new PyStr((new Character(self.val.charAt(k))).toString()), result);

                    }

                } catch (Exception ex) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "could not convert string to funlist: '" + self.str() + "'");
                }

                return result;
            }
        });

        funs.put("__list__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);
                ArrayList<PyObject> result = new ArrayList<>();
                int k;

                try {
                    for (k=0; k<self.val.length(); k++) {
                        result.add(new PyStr((new Character(self.val.charAt(k))).toString()));
                    }

                } catch (Exception ex) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "could not convert string to funlist: '" + self.str() + "'");
                }

                return new PyList(result);
            }
        });

        funs.put("__str__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                return self;
            }
        });

        funs.put("__repr__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                if (self.val.contains("'")) {
                    return new PyStr("\"" + self.val + "\"");
                } else {
                    return new PyStr("'" + self.val + "'");
                }
            }
        });

        funs.put("split", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                String delim = " \t\n";
                if (args.size() == 2) {
                    PyStr sepObj = (PyStr) args.get(0);
                    delim = sepObj.str();
                }

                ArrayList<PyObject> strs = new ArrayList<PyObject>();

                String ss = "";

                for (int i = 0; i < self.val.length(); i++) {
                    if (delim.indexOf(self.val.charAt(i)) >= 0) {
                        strs.add(new PyStr(ss));
                        ss = "";
                    } else {
                        ss += self.val.charAt(i);
                    }
                }

                if (ss.length() > 0) {
                    strs.add(new PyStr(ss));
                }

                return new PyList(strs);
            }
        });

        funs.put("strip", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() > 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 or 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                String delim = " \t\n";
                if (args.size() == 2) {
                    PyStr sepObj = (PyStr) args.get(0);
                    delim = sepObj.str();
                }

                ArrayList<PyObject> strs = new ArrayList<PyObject>();

                String ss = self.val;

                // strip white space from beginning of string
                while (ss.length() > 0 && delim.contains((new Character(ss.charAt(0))).toString())) {
                    ss = ss.substring(1);
                }

                // strip white space from end of string
                while (ss.length() > 0 && delim.contains((new Character(ss.charAt(ss.length() - 1))).toString())) {
                    ss = ss.substring(0, ss.length() - 2);
                }

                return new PyList(strs);
            }
        });

        funs.put("__getitem__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);
                PyInt intObj = (PyInt) args.get(0);
                int index = intObj.getVal();

                if (index >= self.val.length()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Index out of range");
                }

                return new PyStr((new Character(self.val.charAt((index))).toString()));
            }

        });

        funs.put("__len__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                return new PyInt(self.val.length());
            }

        });

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 1 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                return new PyStrIterator(self);
            }

        });

        funs.put("__eq__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    return new PyBool(false);
                }

                PyStr arg = (PyStr) args.get(0);

                if (self.val.equals(arg.val)) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }

        });

        funs.put("__ne__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    return new PyBool(true);
                }

                PyStr arg = (PyStr) args.get(0);

                if (self.val.equals(arg.val)) {
                    return new PyBool(false);
                }

                return new PyBool(true);
            }

        });

        funs.put("__lt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unorderable items str and " + args.get(0).getType().str());
                }

                PyStr arg = (PyStr) args.get(0);

                if (self.val.compareTo(arg.val) < 0) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }

        });

        funs.put("__gt__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unorderable items str and " + args.get(0).getType().str());
                }

                PyStr arg = (PyStr) args.get(0);

                if (self.val.compareTo(arg.val) > 0) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }

        });

        funs.put("__le__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unorderable items str and " + args.get(0).getType().str());
                }

                PyStr arg = (PyStr) args.get(0);

                if (self.val.compareTo(arg.val) <= 0) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }

        });

        funs.put("__ge__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION, "TypeError: expected 2 arguments, got " + args.size());
                }

                PyStr self = (PyStr) args.get(args.size() - 1);

                //We should check the type of args[0] before casting it. 
                if (self.getType().typeId() != args.get(0).getType().typeId()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unorderable items str and " + args.get(0).getType().str());
                }

                PyStr arg = (PyStr) args.get(0);

                if (self.val.compareTo(arg.val) >= 0) {
                    return new PyBool(true);
                }

                return new PyBool(false);
            }

        });

        return funs;
    }
}
