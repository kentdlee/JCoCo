/**
 * PyFrame.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * PyFrame objects are the JCoCo equivalent of an activation record on the
 * run-time stack. A PyFunction or PyMethod object is created for each function defined
 * in the casm file. When a function is called, a PyFrame object is created
 * with the information for the function or method, including the code, arguments,
 * and the globals, constants, and cellvars. The execute method of this class
 * is where the virtual machine instructions are executed.
 *
 * The opStack instance variable is a stack of PyObjects. It is where the
 * virtual machine pushes operands that are used by the instructions. The
 * CoCo is a stack machine (i.e. no registers). All operands are pushed on the
 * opStack prior to an operation being performed.
 *
 * The blockStack is a stack used to record blocks. A block is an integer
 * which is pushed on a stack for a loop or a try except block. When in a loop
 * the break statement will exit the loop by popping the block stack to find
 * the exit point for the program counter. For try except blocks the value
 * pushed on the blockStack is -1*PC where PC is the exit point of the exception
 * handler. In this way we can distinguish between loop blocks and exception
 * blocks.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import jcoco.PyByteCode.PyOpCode;
import jcoco.PyException.ExceptionType;
import jcoco.PyType.PyTypeId;
import java.util.Stack;

class PyFrame extends PyObjectAdapter {

    private final PyCode code;
    private int PC;
    private HashMap<String, PyObject> locals;
    private HashMap<String, PyObject> globals;
    private HashMap<String, PyCell> cellvars;
    private boolean stepOnReturn = false;
    private boolean printDebuggerPrompt = true;
    private final ArrayList<PyObject> consts;
    private PyStack<PyObject> opStack;
    private PyStack<Integer> blockStack;
    private final String[] cmp_op = {"__lt__", "__le__", "__eq__", "__ne__", "__gt__", "__ge__",
        "__contains__", "__notin__", "is__", "is_not", "__excmatch",
        "BAD"};
    private PyCallStack callStack;

    public PyFrame(PyCallStack callStack, PyCode code, ArrayList<PyObject> args, HashMap<String, PyObject> globals,
            ArrayList<PyObject> consts, HashMap<String, PyCell> cellvars) {
        super();
        this.callStack = callStack;
        this.globals = globals;
        this.code = code;
        this.consts = consts;
        this.cellvars = cellvars;
        this.locals = new HashMap<String, PyObject>();
        ArrayList<String> varnames = code.getLocals();

        this.opStack = new PyStack<PyObject>();
        this.blockStack = new PyStack<Integer>();

        int j = args.size() - 1;

        for (int i = 0; i < args.size(); i++) {
            locals.put(varnames.get(i), args.get(j));
            j--;
        }

        for (int i = 0; i < code.getCellVars().size(); i++) {
            String name = code.getCellVars().get(i);
            cellvars.put(name, new PyCell(null));

            //Here we have a special case where the parmeter is used in a nested
            //funciton ans so must be a cell instead of the normal local Value
            if (locals.containsKey(name)) {
                cellvars.get(name).set(locals.get(name));
            }
        }
    }

    public void terminate(int exceptionVal, String name) {
        throw new PyException(ExceptionType.valueOf(exceptionVal),
                "Terminating with exception in function " + name);
    }

    public PyObject safetyPop() {
        if (this.opStack.isEmpty()) {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                    "Attempt to pop empty operand stack in " + this.code.getInstructions().get(this.PC - 1).getOpCodeName());
        }

        PyObject obj = opStack.pop();

        // If we were to come across a PyMarker it was left on the stack by 
        // code that had an exception. This should not happen, but if so, throw it away.
        while (obj.getType().typeId() == PyType.PyTypeId.PyMarkerType) {
            obj = opStack.pop();
        }

        return obj;
    }

    public String getCellName(int index) {
        String name;

        if (index < code.getCellVars().size()) {
            name = code.getCellVars().get(index);
        } else {
            if (index - code.getCellVars().size() >= code.getFreeVars().size()) {
                throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        "Index value out of range on instruction");
            }
            name = code.getFreeVars().get(index - code.getCellVars().size());
        }
        return name;
    }

    public PyCode getCode() {
        return this.code;
    }

    public HashMap<String, PyObject> getLocals() {
        return locals;
    }

    public int getPC() {
        return this.PC;
    }

   public String hashMapToString(HashMap theMap) {
        // temporarily turn off stepping if it is on.
        boolean debugging = JCoCo.stepOverInstructions;
        JCoCo.stepOverInstructions = false;

        String s = "{";

        HashMap<String, PyObject> map = (HashMap<String, PyObject>) theMap;
        PyStr t = null;
        String t_str = null;

        for (String key : map.keySet()) {
            try {
                t = (PyStr) map.get(key).callMethod(new PyCallStack(), "__repr__", new ArrayList<PyObject>());
                t_str = t.str();
            } catch (PyException ex) {
                try {
                    t_str = ((PyObject) map.get(key)).str();
                } catch (PyException ex2) {
                    t_str = "<" + ((PyObject) map.get(key)).getType() + " object at 0x" + Integer.toHexString(System.identityHashCode(this)) + ">";
                }
            }
            s += key + "=" + t_str + ",";
        }

        if (s.length() > 1) {
            s = s.substring(0, s.length() - 1);
        }

        s += "}";

        JCoCo.stepOverInstructions = debugging;

        return s;
    }

    public void break_point_command_loop() {
        boolean exited = false;
        if (printDebuggerPrompt) {
            System.out.println("Entering Interactive Debugger in function " + this.code.getName() + " ...");
        }

        while (!exited) {

            System.out.print("Enter command (h for help): ");
            String cmd = JCoCo.scanner.nextLine();

            printDebuggerPrompt = true; // for next time we get in here

            if (cmd.equals("h") || cmd.equals("help")) {
                System.out.println("  (h)elp:      print this help message\n  (c)allstack: print the current run-time stack\n  (t)ype:      print the code\n  (a)rgs:      print the contents of the operand stack\n  (l)ocals:    print the contents of the locals\n  (v)ars:      print the contents of the cellvars\n  (s)tep:      execute one instruction\n  (o)ut:       Step out of the current function call\n  (r)un:       continue execution\n  (q)uit:      quit execution");
            } else if (cmd.equals("run") || cmd.equals("r")) {
                exited = true;
                JCoCo.stepOverInstructions = false;
            } else if (cmd.equals("quit") || cmd.equals("q")) {
                System.exit(0);
            } else if (cmd.equals("step") || cmd.equals("s")) {
                printDebuggerPrompt = false; // Don't keep printing it if we are stepping.
                exited = true;
                JCoCo.stepOverInstructions = true;
                System.out.println(PC + ": " + code.getInstructions().get(PC));
            } else if (cmd.equals("out") || cmd.equals("o")) {
                stepOnReturn = true;
                JCoCo.stepOverInstructions = false;
                exited = true;
            } else if (cmd.equals("callstack") || cmd.equals("c")) {
                callStack.printCallStack();
            } else if (cmd.equals("args") || cmd.equals("a")) {
                if (JCoCo.verbose) {
                    System.out.println(opStack.toStringNoMarkers());
                } else {
                    System.out.println(opStack.toString());
                }

            } else if (cmd.equals("type") || cmd.equals("t")) {
                System.out.println("\n***********************************\nPC=" + PC + "\n***********************************");
                System.out.println(this.code.prettyString("", true));
            } else if (cmd.equals("locals") || cmd.equals("l")) {
                System.out.println(hashMapToString(locals));
            } else if (cmd.equals("vars") || cmd.equals("v")) {
                System.out.println(hashMapToString(cellvars));
            } else {
                System.out.println("Unknown Command");
            }
        }
    }

    public PyObject execute() {

        this.PC = 0;
        PyByteCode inst = null;
        PyOpCode opcode;
        int operand;
        int i;
        PyType selfType;
        PyCell cell;
        PyObject u;
        PyObject v;
        PyObject w;
        PyFunList funlist;
        PyTuple tuple;
        PyObject x;
        PyObject y;
        PyObject z;
        PyBool bu;
        boolean handled = false;
        PyCallable fun;
        ArrayList<PyObject> args;
        Iterator it;
        String name;

        callStack.pushFrame(this);

        while (true) {
            try {
                if (PC == this.code.getInstructions().size()) {
                    throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                            "ValueError: Function must return a value. Missing RETURN_VALUE instruction");
                }

                inst = this.code.getInstructions().get(this.PC);

                if (JCoCo.stepOverInstructions) {
                    break_point_command_loop();
                }

                this.PC++;
                opcode = inst.getOpCode();
                operand = inst.getOperand();

                switch (opcode) {
                    case LOAD_FAST:
                        u = this.locals.get(this.code.getLocals().get(operand));
                        if (u == null) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "NameError: name '" + this.code.getLocals().get(operand) + "' is not defined");
                        }
                        this.opStack.push(u);
                        break;
                    case LOAD_CONST:
                        u = this.consts.get(operand);
                        this.opStack.push(u);
                        break;
                    case LOAD_GLOBAL:
                        u = this.globals.get(this.code.getGlobals().get(operand));
                        if (u == null) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "NameError: name '" + this.code.getGlobals().get(operand) + "' is not defined");
                        }
                        this.opStack.push(u);
                        break;
                    case STORE_FAST:
                        u = this.safetyPop();
                        this.locals.put(this.code.getLocals().get(operand), u);
                        break;

                    case STORE_LOCALS:
                        u = this.safetyPop();
                        this.locals = ((PyMap) u).getMap();
                        break;

                    case STORE_NAME:
                        u = this.safetyPop();
                        this.locals.put(this.code.getGlobals().get(operand), u);
                        break;

                    case LOAD_NAME:
                        u = this.locals.get(this.code.getGlobals().get(operand));
                        if (u == null) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "NameError: name '" + this.code.getGlobals().get(operand) + "' is not defined");
                        }
                        this.opStack.push(u);
                        break;

                    case POP_TOP:
                        u = this.safetyPop();
                        break;
                    case COMPARE_OP:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);

                        //Please note that the line below depends on the cmp_op
                        //arrat and it should be intialized to all comparison
                        //operators. This list will need to expand at some point.
                        //the cmp_op array is at the top of this module.
                        w = u.callMethod(callStack,cmp_op[operand], args);
                        //do not need to delete args, garbage collection will handle it
                        this.opStack.push(w);

                        //The following must be done for exceptions because the 
                        //END_FINALLY needs to know whether the exception was handled 
                        //or not.
                        if (operand == 10) {
                            handled = ((PyBool) w).getVal();
                        }
                        break;
                    case SETUP_LOOP:
                        this.blockStack.push(operand);
                        break;
                    case BREAK_LOOP:
                        this.PC = this.blockStack.pop();
                        break;
                    case POP_BLOCK:
                        if (this.blockStack.isEmpty()) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Attempt to pop empty block stack");
                        }

                        this.blockStack.pop();
                        break;
                    case JUMP_FORWARD:
                        //This instruction is not relative in this virtual machine
                        // because the assembler calculates the target address
                        this.PC = operand;
                        break;
                    case JUMP_ABSOLUTE:
                        this.PC = operand;
                        break;
                    case POP_JUMP_IF_TRUE:
                        if (this.opStack.isEmpty()) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Attempt to pop empty operand stack in POP_JUMP_IF_FALSE");
                        }
                        u = this.safetyPop();
                        if (u.getType().str() != "bool") {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Illegal operand type for POP_JUMP_IF_FALSE: expected bool, got " + u.getType().str());
                        }

                        bu = (PyBool) u;

                        if (bu.getVal() == true) {
                            this.PC = operand;
                        }
                        break;
                    case POP_JUMP_IF_FALSE:
                        if (this.opStack.isEmpty()) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Attempt to pop empty operand stack in POP_JUMP_IF_FALSE");
                        }

                        u = this.safetyPop();
                        if (u.getType().str() != "bool") {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Illegal Operand Type for POP_JUMP_IF_FALSE");
                        }

                        bu = (PyBool) u;

                        if (bu.getVal() == false) {
                            this.PC = operand;
                        }
                        break;
                    case BINARY_ADD:
                    case INPLACE_ADD:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);

                        w = u.callMethod(callStack,"__add__", args);

                        this.opStack.push(w);
                        break;
                    case BINARY_SUBTRACT:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);
                        w = u.callMethod(callStack,"__sub__", args);
                        this.opStack.push(w);
                        break;
                    case BINARY_MULTIPLY:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);
                        w = u.callMethod(callStack,"__mul__", args);
                        this.opStack.push(w);
                        break;
                    case BINARY_FLOOR_DIVIDE:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);
                        w = u.callMethod(callStack,"__floordiv__", args);
                        this.opStack.push(w);
                        break;
                    case BINARY_TRUE_DIVIDE:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);
                        w = u.callMethod(callStack,"__truediv__", args);
                        this.opStack.push(w);
                        break;
                    case BINARY_MODULO:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);
                        w = u.callMethod(callStack,"__mod__", args);
                        this.opStack.push(w);
                        break;
                    case BINARY_POWER:
                        v = this.safetyPop();
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(v);
                        w = u.callMethod(callStack,"__pow__", args);
                        this.opStack.push(w);
                        break;
                    case GET_ITER:
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        v = u.callMethod(callStack,"__iter__", args);
                        this.opStack.push(v);
                        break;
                    case ROT_TWO:
                        u = this.safetyPop();
                        v = this.safetyPop();
                        this.opStack.push(u);
                        this.opStack.push(v);
                        break;
                    case FOR_ITER:
                        u = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        try {
                            v = u.callMethod(callStack,"__next__", args);
                            this.opStack.push(u);
                            this.opStack.push(v);
                        } catch (PyException ex) {
                            if (ex.getExceptionType() == ExceptionType.PYSTOPITERATIONEXCEPTION) {
                                this.PC = operand;
                            } else {
                                throw ex;
                            }
                        }
                        break;
                    case CALL_FUNCTION:
                        args = new ArrayList<PyObject>();
                        //NOTE: Arguments are added backwards because they are popped 
                        //off the stack in reverse order. So, the called function 
                        //gets the arguments backwards
                        for (i = 0; i < operand; i++) {
                            u = this.safetyPop();
                            args.add(u);
                        }
                        u = this.safetyPop();
                        v = u.callMethod(callStack,"__call__", args);
                        this.opStack.push(v);
                        //don't need to delete args; garbage collection will handle it
                        break;
                    case RETURN_VALUE:
                        if (this.opStack.isEmpty()) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Attempt to pop empty stack in RETURN_VALUE");
                        }
                        if (stepOnReturn) {
                            JCoCo.stepOverInstructions = true;
                        }
                        u = safetyPop();
                        callStack.popFrame();
                        
                        if (JCoCo.stepOverInstructions) {
                            System.out.println("Interactive Debugger returning from function " + this.code.getName() + " ...");
                        }   

                        return u;
                    case LOAD_ATTR:
                        u = this.safetyPop();
                        v = u.get(this.code.getGlobals().get(operand));
                        this.opStack.push(v);
                        break;
                    // what is store attr supposed to do?
                    case STORE_ATTR:
                        u = this.safetyPop();
                        v = this.safetyPop();
                        u.set(this.code.getGlobals().get(operand), v);
                        break;
                    case BINARY_SUBSCR:
                        u = this.safetyPop();
                        v = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(u);

                        w = v.callMethod(callStack,"__getitem__", args);
                        this.opStack.push(w);
                        break;
                    case STORE_SUBSCR:
                        u = this.safetyPop();
                        v = this.safetyPop();
                        w = this.safetyPop();
                        args = new ArrayList<PyObject>();
                        args.add(w);
                        args.add(u);

                        w = v.callMethod(callStack,"__setitem__", args);
                        break;
                    case LOAD_CLOSURE:
                        //the free or cell vars in the code object give us the name of the value
                        name = getCellName(operand);

                        //use the name to lookup the cell in the cellvar storage and push
                        this.opStack.push(this.cellvars.get(name));
                        break;

                    case BUILD_FUNLIST:
                        args = new ArrayList<PyObject>();
                        for (i = 0; i < operand; i++) {
                            u = safetyPop();
                            args.add(0, u);
                        }

                        opStack.push(new PyFunList(args));
                        break;

                    case SELECT_FUNLIST:
                        u = safetyPop();
                        if (u.getType().typeId() != PyType.PyTypeId.PyFunListType) {
                            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Attempt to select elements of a funlist from non-funlist object.");
                        }
                        funlist = (PyFunList) u;
                        opStack.push(funlist.getTail());
                        opStack.push(funlist.getHead());
                        break;

                    case CONS_FUNLIST:
                        u = safetyPop();
                        v = safetyPop();
                        if (u.getType().typeId() != PyType.PyTypeId.PyFunListType) {
                            throw new PyException(PyException.ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Attempt to construct a funlist without a funlist tail.");
                        }
                        funlist = (PyFunList) u;
                        opStack.push(new PyFunList(v, funlist));
                        break;

                    case LOAD_BUILD_CLASS:
                        this.opStack.push(new PyBuildClass(globals));
                        break;

                    case BUILD_TUPLE:
                        args = new ArrayList<PyObject>();
                        for (i = 0; i < operand; i++) {
                            u = this.safetyPop();
                            args.add(0, u);
                        }
                        this.opStack.push(new PyTuple(args));
                        break;
                    case SELECT_TUPLE:
                        u = this.safetyPop();
                        if (u.getType().typeId() != PyTypeId.PyTupleType) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Attempt to select elements of a tuple from a non-tuple object");
                        }

                        tuple = (PyTuple) u;

                        if (operand != tuple.size()) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Attempt to select elements of a atile with incorrect size");
                        }

                        for (i = tuple.size() - 1; i >= 0; i--) {
                            this.opStack.push(tuple.getVal(i));
                        }
                        break;
                    case BUILD_LIST:
                        args = new ArrayList<PyObject>();
                        for (i = 0; i < operand; i++) {
                            u = this.safetyPop();
                            args.add(0, u);
                        }

                        this.opStack.push(new PyList(args));
                        break;
                    case MAKE_CLOSURE:
                        u = this.safetyPop();
                        v = this.safetyPop();
                        w = new PyFunction((PyCode) u, this.globals, v);
                        this.opStack.push(w);
                        break;
                    case MAKE_FUNCTION:
                        u = this.safetyPop();
                        w = new PyFunction((PyCode) u, this.globals, null);
                        this.opStack.push(w);
                        break;
                    case STORE_DEREF:
                        u = this.safetyPop();
                        cell = this.cellvars.get(getCellName(operand));
                        cell.set(u);
                        break;
                    case LOAD_DEREF:
                        cell = this.cellvars.get(getCellName(operand));
                        this.opStack.push(cell.deref());
                        break;
                    case SETUP_EXCEPT:
                        //multiplying by -1 is because any value less than 0 is 
                        // for a try except
                        this.blockStack.push(-1 * operand);

                        // We put a marker on the operand stack in case an exception occurs. If
                        // a marker is popped (by safetyPop) it is thrown away so that the machine 
                        // does not see the marker. If an exception occurs we'll look for the marker.
                        opStack.push(new PyMarker());
                        break;
                    case RAISE_VARARGS:
                        // This is not currently implemented according to the 
                        // byte code documentation. The documentation says this:
                        // RAISE_VARARGS(argc)
                        //    Raises an exception. argc indicates the number of 
                        //    parameters to the raise statement, ranging from 0 to 3. 
                        //    The handler will find the traceback as TOS2, the 
                        //    parameter as TOS1, and the exception as TOS.
                        // In this interpreter, currently exceptions contain the traceback
                        // and there is always one argument to the RAISE_VARARGS 
                        // instruction, which is the value stored in the exception
                        u = this.safetyPop();
                        throw ((PyException) u);
                    case DUP_TOP:
                        if (this.opStack.isEmpty()) {
                            throw new PyException(ExceptionType.PYEMPTYSTACKEXCEPTION,
                                    "Attempt to duplicate top of empty operand stack");
                        }
                        this.opStack.push(this.opStack.top());
                        break;
                   case SETUP_FINALLY:
                        this.blockStack.push(-1 * operand);
                        // We put a marker on the operand stack in case an exception occurs. If
                        // a marker is popped (by safetyPop) it is thrown away so that the machine 
                        // does not see the marker. When we get to the END_FINALLY we'll clean up 
                        // the operand stack of anything left.
                        opStack.push(new PyMarker());
                        break;
                    case END_FINALLY:
                        if (!handled) {
                            //The Stack contains:
                            //TOS: Exception
                            //TOS1: Value (which is just our exception again)
                            //TOS2: Traceback - but this is in our exception
                            //And the block stack contains a block for the handler
                            //So we pop three values and throw our exception again.
                            u = this.safetyPop();
                            v = this.safetyPop();
                            w = this.safetyPop();
                            this.blockStack.pop();

                            throw ((PyException) u);
                        }
                        // when the SETUP_FINALLY was executed, a marker was added to the operand stack
                        // in case an exception occurrred. Now that we are done processing the finally, 
                        // we clean up the operand stack to this point. 
                        if (!opStack.isEmpty()) {
                            PyObject obj = opStack.pop();
                            while (!obj.str().equals("Marker") && !opStack.isEmpty()) {
                                obj = opStack.pop();
                            }
                        }
                        break;
                    case POP_EXCEPT:
                        operand = this.blockStack.pop();
                        if (operand > 0) {
                            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                                    "Pop of block stack was for non-exception handling block.");
                        }
                        break;
                    case DELETE_FAST:
                        //The purpose of this instruction is not well understood.
                        //According to the definition, it deletes the local variable
                        //found at the index operand.
                        this.locals.remove(this.code.getLocals().get(operand));
                        break;
                    case BREAK_POINT:
                        this.printDebuggerPrompt = true;
                        JCoCo.stepOverInstructions = true;
                        break;
                    default:
                        throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unimplemented instruction: " + inst.getOpCodeName());
                }
            } catch (PyException ex) {
                int exitAddress;
                boolean found = false;
                while (!found && !this.blockStack.isEmpty()) {
                    exitAddress = this.blockStack.pop();
                    if (exitAddress < 0) {
                        found = true;
                        if (JCoCo.verbose) {
                            System.err.println("******************Handling Exception*********************");
                            System.err.println("The exception was: " + ex.str());
                            System.err.println("---------------------------------------------------------");
                            System.err.println("                The Exception's Traceback");
                            System.err.println("---------------------------------------------------------");
                            ex.printTraceBack();
                            System.err.println("******************End Handling Exception*****************");
                        }

                        // when the SETUP_EXCEPT was executed, a marker was added to the operand stack
                        // in case an exception occurrred. Now that we are processing the exception, 
                        // we clean up the operand stack to this point. 
                        if (!opStack.isEmpty()) {
                            PyObject obj = opStack.pop();
                            while (!obj.str().equals("Marker") && !opStack.isEmpty()) {
                                obj = opStack.pop();
                            }
                        }

                        // The exception is pushed onto the operand stack for processing
                        this.opStack.push(ex.getTraceBack()); //The tracebakc at TOS2
                        this.opStack.push(ex); //the parameter (in our case the exception) at TOS1
                        this.opStack.push(ex); //the exception at TOS
                        //the location to resume execution was found on the block stack
                        this.PC = -1 * exitAddress;

                        //An implicitly pushed exception handling block is pushed for the handler
                        this.blockStack.push(0);
                    }
                }

                if (!found) {
                    ex.tracebackAppend(this);
                    throw ex;
                }
            } catch (Exception e) {
                PyException ex = new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION,
                        e.getMessage() + " while executing instruction " + inst.getOpCodeName());
                if (JCoCo.verbose) {
                  System.err.println("*********************Exception***************************");
                  e.printStackTrace();
                }
                ex.tracebackAppend(this);
                throw ex;
            }

        }

    }

}
