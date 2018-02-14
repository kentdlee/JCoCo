/**
 * PyException.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License:
 * Please read the LICENSE file in this distribution for details regarding
 * the licensing of this code. This code is freely available for educational
 * use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 *
 * Description:
 * The PyException Class.
 * PyExceptions are thrown for error conditions and when raised by source
 * programs in CoCo. An exception contains a constant indicating the type
 * of the exception and either a message string or a Python object. If a
 * message string is passed, it is stored in the exception as a PyStr object.
 *
 * The exception types are listed below the class declaration.
 *
 * When an exception is raised in CoCo a real Java exception is also raised.
 * There can be one of two outcomes: the CoCo may intend to catch the exception
 * or handle the exception. If the latter is the case, an exception handler will
 * have been installed via the SETUP_EXCEPT instruction. This pushes an exception
 * block onto the block stack.
 *
 * Any exception is first caught by the virtual machine in its fetch/execute
 * loop in PyFrame. If an exception handler block is found on the block stack,
 * then control is passed to the exception handler (the PC is set to the
 * handler).
 *
 * If no exception handling block is found, the current PyFrame object is
 * added to the traceback of this exception. The traceback is a vector of
 * PyFrame pointers. Each PyFrame corresponds to one function call. The
 * traceback forms a copy of the run-time stack at the time the exception
 * first occurred back to where it was caught. If no exception handler, in
 * any activation record, catches the exception, then the main code prints
 * the traceback along with exception information so the user can see where
 * the exception occurred.
 *
 * The exception enum below defines the different types of reported
 * exceptions. The PYSTOPITERATIONEXCEPTION is used to end iteration (see
 * the PyListIterator.java description for more detail.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import jcoco.PyType.PyTypeId;

public class PyException extends RuntimeException implements PyObject {

    @Override
    public void set(String key, PyObject value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PyObject get(String key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public enum ExceptionType {
        PYEXCEPTION(1),
        PYEMPTYSTACKEXCEPTION(2),
        PYPARSEEXCEPTION(3),
        PYILLEGALOPERATIONEXCEPTION(4),
        PYWRONGARGCOUNTEXCEPTION(5),
        PYSTOPITERATIONEXCEPTION(6),
        PYMATCHEXCEPTION(7),
        PYATTRERROR(8);

        private final int code;

        ExceptionType(int code) {
            this.code = code;
        }

        public int getVal() {
            return this.code;
        }

        public static ExceptionType valueOf(int code) throws IllegalArgumentException {
            for (ExceptionType e : values()) {
                if (e.getVal() == code) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Unrecognized Exception " + code);
        }
    }

    protected ExceptionType type;
    protected ArrayList<PyFrame> traceback = new ArrayList<PyFrame>();
    protected PyStr val;
    protected HashMap<String, PyObject> dict = new HashMap<String, PyObject>();

    public PyException(ExceptionType type, String msg) {
        super();
        this.type = type;
        this.val = new PyStr(msg);
        initMethods(funs());
    }

    public PyException(int typeCode, String msg) {
        this(ExceptionType.valueOf(typeCode), msg);
    }

    public String getMessage(PyCallStack callStack) {
        return this.val.str();
    }

    public ExceptionType getExceptionType() {
        return this.type;
    }

    public void tracebackAppend(PyFrame frame) {
        this.traceback.add(frame);
    }

    public void printTraceBack() {
        for (int k = traceback.size()-1;k>=0;k--) {
            System.err.println("=========> PC=" + (traceback.get(k).getPC()) + " in this function. ");
            if (traceback.get(k).getCode().getType().typeId() == PyType.PyTypeId.PyCodeType) {
                try {
                    System.err.println(traceback.get(k).getCode().prettyString("", true));
                } catch (PyException e) {
                    System.err.println("Unable to print traceback : " + e.getMessage());
                }
            }
        }
    }

    public PyObject getTraceBack() {
        ArrayList<PyObject> lst = new ArrayList<PyObject>(this.traceback);
        return new PyList(lst);
    }

    @Override
    public String str() {
        return this.val.str();
    }

    @Override
    public PyType getType() {
        return JCoCo.PyTypes.get(PyTypeId.PyExceptionTypeId);
    }

    @Override
    public PyObject callMethod(PyCallStack callStack, String name, ArrayList<PyObject> args) {
        if (!this.dict.containsKey(name)) {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "TypeError: '" + this.getType().str() + "' object has no attribute '" + name + "'");
        }

        PyCallable mbr = (PyCallable) this.dict.get(name);

        return mbr.__call__(callStack, args);
    }

    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("__str__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyException self = (PyException) args.get(args.size() - 1);

                return new PyStr(self.str());
            }
        });

        funs.put("__hash__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                PyException self = (PyException) args.get(args.size() - 1);

                throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "TypeError: unhashable type: '" + self.getType().str() + "'");
            }
        });

        funs.put("__repr__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }
                PyException self = (PyException) args.get(args.size() - 1);

                return self.callMethod(callStack,"__str__", newargs());
            }
        });

        funs.put("__type__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 1) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyException self = (PyException) args.get(args.size() - 1);

                return (PyObject) self.getType();
            }
        });

        funs.put("__excmatch", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {
                if (args.size() != 2) {
                    throw new PyException(ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 2 arguments, got " + args.size());
                }

                PyException self = (PyException) args.get(args.size() - 1);

                PyObject arg = args.get(0);

                //if the arg was the Exception Type, then its a match because every
                //exception object should match the exception type
                if (self.getType() == arg) {
                    return new PyBool(true);
                }

                //Otherwise, the object passed was an Exception Object. Match the 
                //exception values in that case.
                if (self.getType() != arg.getType()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "TypeError: Exception match type mismatch. Expected Exception Object, but got " + arg.str());
                }

                PyException other = (PyException) arg;

                return new PyBool(self.getExceptionType() == other.getExceptionType());
            }
        });

        return funs;
    }

    public void initMethods(HashMap<String, PyCallable> funs) {
        for (String key : funs.keySet()) {
            this.dict.put(key, new PyMethod(key, this, funs.get(key)));
        }
    }
}
