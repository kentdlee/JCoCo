/**
 * JCoCo.java
 *
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 * Research Assistant: Jonathan Opdahl
 * Created on Jan. 3, 2017
 *
 * License:
 * Please read the LICENSE file in this distribution for details regarding
 * the licensing of this code. This code is freely available for educational
 * use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 *
 * Description :
 * This project provides an implementation of the CoCo virtual machine in java.
 * The goal of this project is to provide a deployable jar of the JCoCo virtual
 * machine.
 *
 * To run a .casm file, invoke
 *      java JCoCo "<filename>"
 */
package jcoco;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.io.PrintWriter;
import java.util.Set;
import java.util.Stack;
import jcoco.PyException.ExceptionType;
import jcoco.PyToken.TokenType;
import jcoco.PyType.PyTypeId;

public class JCoCo {

    public static HashMap<PyTypeId, PyType> PyTypes = new HashMap<PyTypeId, PyType>();
    public static boolean verbose = false;
    public static Scanner scanner;
    public static boolean stepOverInstructions = false;

    public static PyCallStack mainThreadCallStack = new PyCallStack();
    
    public static void initTypes() {

        PyType typeType = new PyType("type", PyTypeId.PyTypeType);
        PyTypes.put(PyTypeId.PyTypeType, typeType);

        PyType noneType = new PyType("NoneType", PyTypeId.PyNoneType);
        PyTypes.put(PyTypeId.PyNoneType, noneType);
        noneType.setInstanceFuns(PyNone.funs());

        PyType boolType = new PyType("bool", PyTypeId.PyBoolType);
        PyTypes.put(PyTypeId.PyBoolType, boolType);
        boolType.setInstanceFuns(PyBool.funs());

        PyType intType = new PyType("int", PyTypeId.PyIntType);
        PyTypes.put(PyTypeId.PyIntType, intType);
        intType.setInstanceFuns(PyInt.funs());

        PyType floatType = new PyType("float", PyTypeId.PyFloatType);
        PyTypes.put(PyTypeId.PyFloatType, floatType);
        floatType.setInstanceFuns(PyFloat.funs());

        PyType strType = new PyType("str", PyTypeId.PyStrType);
        PyTypes.put(PyTypeId.PyStrType, strType);
        strType.setInstanceFuns(PyStr.funs());

        PyType functionType = new PyType("function", PyTypeId.PyFunctionType);
        PyTypes.put(PyTypeId.PyFunctionType, functionType);

        PyType methodType = new PyType("method-wrapper", PyTypeId.PyMethodType);
        PyTypes.put(PyTypeId.PyMethodType, methodType);

        PyType builtInType = new PyType("builtin_function_or_method", PyTypeId.PyBuiltInType);
        PyTypes.put(PyTypeId.PyBuiltInType, builtInType);

        PyType moduleType = new PyType("module", PyTypeId.PyModuleType);
        PyTypes.put(PyTypeId.PyModuleType, moduleType);

        PyType fileType = new PyType("_io.TextIOWrapper", PyTypeId.PyFileType);
        PyTypes.put(PyTypeId.PyFileType, fileType);
        fileType.setInstanceFuns(PyFile.funs());

        PyType rangeType = new PyRangeType();
        PyTypes.put(PyTypeId.PyRangeTypeId, rangeType);
        rangeType.setInstanceFuns(PyRange.funs());

        PyType exceptionType = new PyExceptionType();
        PyTypes.put(PyTypeId.PyExceptionTypeId, exceptionType);
        exceptionType.setInstanceFuns(PyException.funs());

        PyType markerType = new PyType("Marker", PyTypeId.PyMarkerType);
        PyTypes.put(PyTypeId.PyMarkerType, markerType);

        PyType rangeIteratorType = new PyType("range_iterator", PyTypeId.PyRangeIteratorType);
        PyTypes.put(PyTypeId.PyRangeIteratorType, rangeIteratorType);
        rangeIteratorType.setInstanceFuns(PyRangeIterator.funs());

        PyType listType = new PyType("list", PyTypeId.PyListType);
        PyTypes.put(PyTypeId.PyListType, listType);
        listType.setInstanceFuns(PyList.funs());

        PyType funlistType = new PyType("funlist", PyTypeId.PyFunListType);
        PyTypes.put(PyTypeId.PyFunListType, funlistType);
        funlistType.setInstanceFuns(PyFunList.funs());

        PyType tupleType = new PyType("tuple", PyTypeId.PyTupleType);
        PyTypes.put(PyTypeId.PyTupleType, tupleType);
        tupleType.setInstanceFuns(PyTuple.funs());

        PyType listIteratorType = new PyType("list_iterator", PyTypeId.PyListIteratorType);
        PyTypes.put(PyTypeId.PyListIteratorType, listIteratorType);
        listIteratorType.setInstanceFuns(PyListIterator.funs());

        listIteratorType = new PyType("funlist_iterator", PyTypeId.PyFunListIteratorType);
        PyTypes.put(PyTypeId.PyFunListIteratorType, listIteratorType);
        listIteratorType.setInstanceFuns(PyFunList.funs());

        PyType tupleIteratorType = new PyType("tuple_iterator", PyTypeId.PyTupleIteratorType);
        PyTypes.put(PyTypeId.PyTupleIteratorType, tupleIteratorType);
        tupleIteratorType.setInstanceFuns(PyTupleIterator.funs());

        PyType strIteratorType = new PyType("str_iterator", PyTypeId.PyStrIteratorType);
        PyTypes.put(PyTypeId.PyStrIteratorType, strIteratorType);
        strIteratorType.setInstanceFuns(PyStrIterator.funs());

        PyType codeType = new PyType("code", PyTypeId.PyCodeType);
        PyTypes.put(PyTypeId.PyCodeType, codeType);

        PyType cellType = new PyType("cell", PyTypeId.PyCellType);
        PyTypes.put(PyTypeId.PyCellType, cellType);

        PyType baseCallableType = new PyType("wrapper_descriptor", PyTypeId.PyBaseCallable);
        PyTypes.put(PyTypeId.PyBaseCallable, baseCallableType);

        PyType mapType = new PyType("MapType", PyTypeId.PyMapType);
        PyTypes.put(PyTypeId.PyMapType, mapType);

        PyType superType = new PySuperType();
        PyTypes.put(PyTypeId.PySuperTypeId, superType);
        
        PyType turtleType = new PyTurtleType();
        PyTypes.put(PyTypeId.PyTurtleType, turtleType);
        turtleType.setInstanceFuns(PyTurtle.funs());    
        
        PyType screenType = new PyType("turtle._Screen", PyTypeId.PyTurtleScreenType);
        PyTypes.put(PyTypeId.PyTurtleScreenType, screenType);
        screenType.setInstanceFuns(PyTurtleScreen.funs());  
    }

    /**
     * Expecting the command line argument to be a casm file.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        HashMap<String, PyObject> globals = new HashMap<String, PyObject>();
        String fileName;
        int k;

        scanner = new Scanner(System.in);

        initTypes();

        if (args.length == 0 || args.length > 2) {
            System.out.println("usage: coco [OPTIONS] filename");
            System.out.println("   OPTIONS:\n    -v   Run in Verbose Mode\n    -s   Start in Step Mode");
            return;
        }

        fileName = args[args.length - 1];

        for (k = 0; k < args.length - 1; k++) {
            if (args[k].contains("v")) {
                verbose = true;
            }

            if (args[k].contains("s")) {
                JCoCo.stepOverInstructions = true;
            }
        }

        try {
            PyParser parser = new PyParser(fileName, globals);
            ArrayList<PyObject> code = parser.parse();

            if (verbose) {

                String indent = "";

                for (int i = 0; i < code.size(); i++) {
                    if (code.get(i).getType().typeId() == PyTypeId.PyCodeType) {
                        System.out.println(((PyCode) code.get(i)).prettyString(indent, false));
                    } else {
                        System.out.println(((PyClass) code.get(i)).prettyString(indent, false));
                    }
                    System.out.println();
                }
            }

            globals.put("print", new PyBuiltInPrint());
            globals.put("fprint", new PyBuiltInFPrint());
            globals.put("len", new PyBuiltInLen());
            globals.put("tprint", new PyBuiltInTPrint());
            globals.put("input", new PyBuiltInInput());
            globals.put("iter", new PyBuiltInIter());
            globals.put("repr", new PyBuiltInRepr());
            globals.put("concat", new PyBuiltInConcat());
            globals.put("int", PyTypes.get(PyTypeId.PyIntType));
            globals.put("float", PyTypes.get(PyTypeId.PyFloatType));
            globals.put("str", PyTypes.get(PyTypeId.PyStrType));
            globals.put("funlist", PyTypes.get(PyTypeId.PyFunListType));
            globals.put("list", PyTypes.get(PyTypeId.PyListType));
            globals.put("type", PyTypes.get(PyTypeId.PyTypeType));
            globals.put("bool", PyTypes.get(PyTypeId.PyBoolType));
            globals.put("range", PyTypes.get(PyTypeId.PyRangeTypeId));
            globals.put("Exception", PyTypes.get(PyTypeId.PyExceptionTypeId));
            globals.put("super", PyTypes.get(PyTypeId.PySuperTypeId));
            globals.put("open", new PyBuiltInOpen());

            //These are the modules supplied with JCoCo
            PyObject sys = new PyModuleSys();
            globals.put("sys", sys);
            PyObject turtle = new PyModuleTurtle();
            globals.put("turtle", turtle);

            //now add the top-level functions
            boolean foundMain = false;

            for (int i = 0; i < code.size(); i++) {
                if (code.get(i).getType().typeId() == PyTypeId.PyCodeType) {
                    PyCode func = (PyCode) code.get(i);
                    if (func.getName().equals("main")) {
                        foundMain = true;
                    }
                    globals.put(func.getName(), new PyFunction(func, globals, null));
                } else if (code.get(i).getType().typeId() == PyTypeId.PyTypeType) {
                    PyClass cls = (PyClass) code.get(i);
                    cls.setGlobals(globals);
                    globals.put(cls.getName(), cls);
                }
            }

            if (!foundMain) {
                System.err.println("ERROR: No main() function found. A main() is required in CoCo VM programs.");
                System.exit(0);
            }

            ArrayList<PyObject> arguments = new ArrayList<PyObject>();
            PyObject result = globals.get("main").callMethod(mainThreadCallStack,"__call__", arguments);
        } catch (PyException ex) {
            System.err.print("\n\n");
            System.err.println("*********************************************************");
            System.err.println("        An Uncaught Exception Occurred");
            System.err.println("*********************************************************");
            System.err.println(ex.str());
            System.err.println("---------------------------------------------------------");
            System.err.println("              The Exception's Traceback");
            System.err.println("---------------------------------------------------------");
            ex.printTraceBack();
            System.err.println("*********************************************************");
            System.err.println("            An Uncaught Exception Occurred (See Above) ");
            System.err.println("*********************************************************");
            System.err.println(ex.str());
            System.err.println("*********************************************************");
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }

        //System.exit(0);
    }
}
