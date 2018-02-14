/**
 * PyByteCode.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 *
 * License: Please read the LICENSE file in this distribution for details
 * regarding the licensing of this code. This code is freely available for
 * educational use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND.
 *
 * Description:
 * The PyByteCode class is used for byte codes of compiled functions in the 
 * virtual machine. Each byte code read from the source file is parsed along 
 * with its operand and a corresponding object is created to hold the
 * byte codes information. While parsing the assembly language file, branch
 * and jump instructions will likely have a label as their target. This label
 * is stored here in the bytecode object. A second pass, once all instructions 
 * are read and parsed from the input file, sets the necessary operand for the 
 * target of the label. 
 * 
 * IMPORTANT NOTE: 
 * In the original Python Virtual Machine the operand (i.e. target) of a branch
 * or jump instruction was either relative to the current Program Counter (PC) 
 * absolute. In JCoCo all operands are absolute regardless of instruction type.
 */
package jcoco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jcoco.PyException.ExceptionType;

class PyByteCode {
    enum PyOpCode {
        STOP_CODE (0),
        NOP (0),
        POP_TOP (0),
        ROT_TWO (0),
        ROT_THREE (0),
        DUP_TOP (0),
        DUP_TOP_TWO (0),
        UNARY_POSITIVE (0),
        UNARY_NEGATIVE (0),
        UNARY_NOT (0),
        UNARY_INVERT (0),
        GET_ITER (0),
        BINARY_POWER (0),
        BINARY_MULTIPLY (0),
        BINARY_FLOOR_DIVIDE (0),
        BINARY_TRUE_DIVIDE (0),
        BINARY_MODULO (0),
        BINARY_ADD (0),
        BINARY_SUBTRACT (0),
        BINARY_SUBSCR (0),
        BINARY_LSHIFT (0),
        BINARY_RSHIFT (0),
        BINARY_AND (0),
        BINARY_XOR (0),
        BINARY_OR (0),
        INPLACE_POWER (0),
        INPLACE_MULTIPLY (0),
        INPLACE_FLOOR_DIVIDE (0),
        INPLACE_TRUE_DIVIDE (0),
        INPLACE_MODULO (0),
        INPLACE_ADD (0),
        INPLACE_SUBTRACT (0),
        INPLACE_LSHIFT (0),
        INPLACE_RSHIFT (0),
        INPLACE_AND (0),
        INPLACE_XOR (0),
        INPLACE_OR (0),
        STORE_SUBSCR (0),
        DELETE_SUBSCR (0),
        PRINT_EXPR (0),
        BREAK_LOOP (0),
        CONTINUE_LOOP (1),
        SET_ADD (1),
        LIST_APPEND (1),
        MAP_ADD (1),
        RETURN_VALUE (0),
        YIELD_VALUE (0),
        IMPORT_STAR (0),
        POP_BLOCK (0),
        POP_EXCEPT (0),
        END_FINALLY (0),
        LOAD_BUILD_CLASS (0),
        SETUP_WITH (1),
        WITH_CLEANUP (0),
        STORE_LOCALS (0),
        STORE_NAME (1),
        DELETE_NAME (1),
        UNPACK_SEQUENCE (1),
        UNPACK_EX (1),
        STORE_ATTR (1),
        DELETE_ATTR (1),
        STORE_GLOBAL (1),
        DELETE_GLOBAL (1),
        LOAD_CONST (1),
        LOAD_NAME (1),
        BUILD_TUPLE (1),
        BUILD_LIST (1),
        BUILD_SET (1),
        BUILD_MAP (1),
        LOAD_ATTR (1),
        COMPARE_OP (1),
        IMPORT_NAME (1),  
        IMPORT_FROM (1),
        JUMP_FORWARD (1),
        POP_JUMP_IF_TRUE (1),
        POP_JUMP_IF_FALSE (1),
        JUMP_IF_TRUE_OR_POP (1),
        JUMP_IF_FALSE_OR_POP (1),
        JUMP_ABSOLUTE (1),
        FOR_ITER (1),
        LOAD_GLOBAL (1),
        SETUP_LOOP (1),
        SETUP_EXCEPT (1),
        SETUP_FINALLY (1),
        STORE_MAP (0),
        LOAD_FAST (1),
        STORE_FAST (1),
        DELETE_FAST (1),
        LOAD_CLOSURE (1),
        LOAD_DEREF (1),
        STORE_DEREF (1),
        DELETE_DEREF (1),
        RAISE_VARARGS (1),
        CALL_FUNCTION (1),
        MAKE_FUNCTION (1),
        MAKE_CLOSURE (1),
        BUILD_SLICE (1),
        EXTENDED_ARG (1),
        CALL_FUNCTION_VAR (1),
        CALL_FUNCTION_KW (1),
        CALL_FUNCTION_VAR_KW (1),
        HAVE_ARGUMENT (0),
        //Here are some new opcodes that Kent Lee has defined.
        BUILD_FUNLIST (1),
        SELECT_FUNLIST (0), // TOS1 is PyFunList of tail of list, TOS is head of list
        CONS_FUNLIST (0),
        SELECT_TUPLE  (1), //TOS to TOSn where n is the length of tuple. TOS is left-most element.
        
        // For more information on the BREAK_POINT instruction, see the JCoCo web page.
        BREAK_POINT (0); //This is an additional instruction for inspection of the virtual machine state.
        
        private int args;
        
        PyOpCode(int args) {
            this.args = args;
        }
        
        public int args() {
            return this.args;
        }
    };
    
    private static HashMap<String, PyOpCode> OpCodeMap = createOpCodeMap();
    private static HashMap<String, Integer> ArgMap = createArgMap();
    
    private static HashMap<String, PyOpCode> createOpCodeMap() {
        HashMap<String, PyOpCode> map = new HashMap<String, PyOpCode>();
        for (PyOpCode opcode : PyOpCode.values()) {
            map.put(opcode.name(), opcode);
        }
        return map;
    }
    
    private static HashMap<String, Integer> createArgMap() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (PyOpCode opcode : PyOpCode.values()) {
            map.put(opcode.name(), opcode.args());
        }
        return map;
    }
    
    private PyOpCode opcode;
    private int operand;
    private String label;

    public static int numArgs(String opcode) {
        return ArgMap.get(opcode);
    }

    public PyByteCode(String opcode)  {
        if (!OpCodeMap.containsKey(opcode)) {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, 
                                    "Unknown opcode "+opcode);
        }
        
        this.opcode = OpCodeMap.get(opcode);
        this.operand = -1;
        this.label = "";
    }

    public PyByteCode(String opcode, int operand)  {
        if (!OpCodeMap.containsKey(opcode)) {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, 
                                    "Unknown opcode "+opcode);
        }
        
        this.opcode = OpCodeMap.get(opcode);
        this.operand = operand;
        this.label = "";
    }
    
    public PyByteCode(String opcode, String label)  {
        if (!OpCodeMap.containsKey(opcode)) {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, 
                                    "Unknown opcode "+opcode);
        }
        this.opcode = OpCodeMap.get(opcode);
        this.operand = -1;
        this.label = label;
    }
    
    public PyByteCode(PyByteCode orig)  {
        this.opcode = orig.getOpCode();
        this.operand = orig.getOperand();
        this.label = orig.getLabel();
    }

    public String getLabel() {
        return this.label;
    }

    public PyOpCode getOpCode() {
        return this.opcode;
    }
    
    public int getOperand() {
        return this.operand;
    }
    
    public String getOpCodeName() {
        return this.opcode.name();
    }
    
    public String toString() {
        String result = "        ";
        String opName = this.getOpCodeName();
        result += opName;
       
        if (this.numArgs(opName) > 0) {
            Integer operandObj = this.operand;
            for (int i = 0; i < 32 - opName.length() - operandObj.toString().length(); i++) {
                result += " ";
            }
            result += operandObj.toString();
        }
        return result;
    }
    
}
