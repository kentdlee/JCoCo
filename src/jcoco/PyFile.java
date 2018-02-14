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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.BufferedInputStream;
import java.io.File;

/**
 *
 * @author leekentd
 */
public class PyFile extends PyPrimitiveTypeAdapter {

    protected Scanner scanin = null;
    protected PrintWriter scanout = null;
    protected String filename;
    protected boolean open = true;

    public PyFile(String filename, String mode) {
        super("file",PyType.PyTypeId.PyFileType);
        initMethods(funs());
        this.filename = filename;

        try {
            if (mode.equals("r")) {
                scanin = new Scanner(new File(filename));
            } else {
                scanout = new PrintWriter(filename);
            }
        } catch (Exception ex) {
            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                    "FileNotFoundError: No such file or directory: '" + filename + "'.\n" + ex.getMessage());
        }
    }

    public PyFile(Scanner in) {
        super("file",PyType.PyTypeId.PyFileType);
        initMethods(funs());
        this.filename = "<stdin>";
        scanin = in;
    }

    public PyFile(PrintWriter out) {
        super("file",PyType.PyTypeId.PyFileType);
        initMethods(funs());
        this.filename = "<stdout>";
        scanout = out;
    }

    @Override
    public String str() {
        String result = "";
        if (scanout == null) {
            result = "<_io.TextIOWrapper  name ='" + filename + "' mode = 'r' encoding ='UTF-8'>";
        } else {
            result = "<_io.TextIOWrapper  name ='" + filename + "' mode = 'w' encoding ='UTF-8'>";
        }

        return result;
    }

    public PyObject readline() {
        if (scanin == null) {
            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                    "io.UnsupportedOperation: not readable");
        }

        String s = "";

        try {
            s = scanin.nextLine() + "\n";
            
        } catch (java.util.NoSuchElementException ex) {
            s = "";
        }

        return new PyStr(s);

    }
    
    public static HashMap<String, PyCallable> funs() {
        HashMap<String, PyCallable> funs = new HashMap<String, PyCallable>();

        funs.put("readline", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument got " + args.size());
                }

                PyFile self = (PyFile) args.get(args.size() - 1);

                if (!self.open) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "ValueError: I/O operation on closed file.");
                }

                return self.readline();

            }

        });

        funs.put("write", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 2) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument got " + args.size());
                }

                PyFile self = (PyFile) args.get(args.size() - 1);

                if (!self.open) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "ValueError: I/O operation on closed file.");
                }

                if (self.scanout == null) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "io.UnsupportedOperation: not writable");
                }

                PyStr str = (PyStr) args.get(0);

                self.scanout.write(str.str());
                self.scanout.flush();

                return new PyInt(str.str().length());
            }

        });

        funs.put("__iter__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFile self = (PyFile) args.get(args.size() - 1);

                return self;
            }
        });

        funs.put("__next__", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFile self = (PyFile) args.get(args.size() - 1);

                if (self.scanin == null) {
                    throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "io.UnsupportedOperation: not readable");
                }

                if (!self.scanin.hasNextLine()) {
                    throw new PyException(PyException.ExceptionType.PYSTOPITERATIONEXCEPTION, "stop it");
                }

                return self.readline();
            }
        });

        funs.put("close", new PyCallableAdapter() {
            @Override
            public PyObject __call__(PyCallStack callStack, ArrayList<PyObject> args) {

                if (args.size() != 1) {
                    throw new PyException(PyException.ExceptionType.PYWRONGARGCOUNTEXCEPTION,
                            "TypeError: expected 1 argument, got " + args.size());
                }

                PyFile self = (PyFile) args.get(args.size() - 1);

                if (!self.open) {
                    return new PyNone();
                }

                self.open = false;

                if (self.scanin != null) {
                    self.scanin.close();
                } else {
                    self.scanout.close();
                }

                return new PyNone();
            }
        });

        return funs;
    }

}
