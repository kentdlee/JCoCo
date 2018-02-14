/**
 * PyParser.java
 * Author: Kent D. Lee (c) 2017 Created on Jan 10, 2017.
 * License:
 * Please read the LICENSE file in this distribution for details regarding
 * the licensing of this code. This code is freely available for educational
 * use. THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 *
 * Description:
 * The format of programs for the CoCo virtual machine is given by the following
 * grammar. This grammar is LL(1) and therefore can be implemented as a top
 * down parser. The methods of the PyParser class implement this top-down
 * parser. Each non-terminal of the grammar below becomes a function in the
 * top-down parser. Non-terminals that appear on the rhs (right-hand side) of a
 * rule correspond to function calls. Tokens that appear on the rhs of a rule
 * can be retrieved from the scanner by calling getToken on the scanner. Because
 * the grammar is LL(1), once in a while a token must be read to look ahead (needed
 * in deciding which rule to apply in some cases). In those cases, the token may
 * be put back using the putBackToken method. Only one token may be put back
 * on the scanner. Calling putBackToken multiple times will only put the last token
 * back once (until it is retrieved with getToken again).
 *
 * Here is the grammar.
 * ===========================================================================
 * CoCoAssemblyProg ::= ClassFunctionListPart EOF
 * ClassFunctionListPart ::= ClassFunDef ClassFunctionList
 * ClassFunctionList ::= ClassFunDef ClassFunctionList | <null>
 * ClassFunDef ::= ClassDef | FunDef 
 * ClassDef ::= Class colon Identifier [ ( Identifier ) ] BEGIN ClassFunctionList END 
 * FunDef ::= Function colon Identifier slash Integer ClassFunctionList ConstPart LocalsPart FreeVarsPart CellVarsPart GlobalsPart BodyPart 
 * ConstPart ::= <null> | Constants colon ValueList 
 * ValueList ::= Value ValueRest 
 * ValueRest ::= comma ValueList | <null>
 * Value ::= None | True | False | Integer | Float | String | code(Identifier) | TupleVal
 * TupleVal ::= ( Value ) | ( Value ValueRest )
 * (* the Scanner sees None, True, False, as Identifiers. *) 
 * LocalsPart ::= <null> | Locals colon IdList 
 * FeeVarsPart ::= <null> | FreeVars colon IdList
 * CellVarsPart ::= <null> | CellVars colon IdList 
 * IdList ::= Identifier IdRest
 * IdRest ::= comma IdList | <null>
 * GlobalsPart ::= <null> | Globals colon IdList 
 * BodyPart ::= BEGIN InstructionList END 
 * InstructionList ::= <null> | LabeledInstruction InstructionList 
 * LabeledInstruction ::= Identifier colon LabeledInstruction | Instruction | OpInstruction 
 * Instruction ::= STOP_CODE | NOP | POP_TOP | ROT_TWO | ROT_THREE | ... 
 * OpInstruction ::= OpMnemonic Integer | OpMnemonic Identifier 
 * OpMnemonic ::= LOAD_CONST | STORE_FAST | SETUP_LOOP | COMPARE_OP | POP_JUMP_IF_FALSE | ...
 * ============================================================================
 * The implementation below cheats a little bit and combines the
 * LabeledInstruction, UnaryInstruction, BinaryInstruction, and BinaryMneomic
 * non-terminals into one function (the LabeledInstruction function).
 *
 */
package jcoco;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jcoco.PyException.ExceptionType;
import jcoco.PyToken.TokenType;

public class PyParser {

    private final PyScanner in;
    private int index;
    private HashMap<String, Integer> target = new HashMap<String, Integer>();
    private HashMap<String, PyObject> globals = null;

    /**
     * Constructor for PyParser class.
     *
     * @param filename name of the casm file to parse
     */
    public PyParser(String filename, HashMap<String, PyObject> globals) throws FileNotFoundException {
        this.globals = globals;
        InputStream fin = new FileInputStream(filename);
        this.in = new PyScanner(new PushbackInputStream(fin));
    }

    /**
     * Alternate constructor of the PyParser class. Used to make a copy of
     * another PyParser.
     *
     * @param copy
     */
    public PyParser(PyParser copy) {
        this.in = new PyScanner(new PushbackInputStream(copy.in.getInputStream()));
    }

    /**
     * Main function for the PyParser class. This is called to parse the file
     * used to initialize the input stream.
     *
     * @return ArrayList of pycode objects
     */
    public ArrayList<PyObject> parse() {
        try {
            return PyAssemblyProg();
        } catch (PyException e) {
            this.in.putBackToken();
            PyToken tok = this.in.getToken();
            System.err.println("*********************************************************");
            System.err.println("              A Parse Exception Occurred");
            System.err.println("*********************************************************");
            System.err.println("The exception occured at line " + tok.getLine());
            System.err.println("The exception was: " + e.getMessage());
            System.exit(0);
        }

        /* We will never get to this code. This is here to please the almighty
            java compiler. */
        return null;
    }

    private void badToken(PyToken tok, String message) {
        System.err.println("*********************************************************");
        System.err.println("              A Parse Exception Occurred");
        System.err.println("*********************************************************");
        System.err.println("Bad token '" + tok.getLex() + "' found at line " + tok.getLine()
                + " and column " + tok.getCol() + ".");
        System.err.println("The token type was : " + tok.getType());
        System.err.println(message);
        System.exit(0);
    }

    private ArrayList<PyObject> PyAssemblyProg() {
        ArrayList<PyObject> code = ClassFunctionListPart();

        PyToken tok = this.in.getToken();

        if (tok.getType() != TokenType.PYEOFTOKEN) {
            badToken(tok, "Excpected End Of File (EOF)");
        }

        return code;

    }

    private ArrayList<PyObject> ClassFunctionListPart() {
        PyObject obj = ClassFunDef();

        ArrayList<PyObject> codeList = new ArrayList<PyObject>();

        codeList.add(obj);

        codeList = ClassFunctionList(codeList);

        return codeList;

    }

    private PyObject ClassFunDef() {
        PyToken tok = this.in.getToken();
        this.in.putBackToken();

        PyObject obj = null;
        if (tok.getLex().equals("Function")) {
            obj = FunDef();
        } else if (tok.getLex().equals("Class")) {
            obj = ClassDef();
        } else {
            throw new PyException(ExceptionType.PYILLEGALOPERATIONEXCEPTION, "Unexpected " + tok.getType() + " token: "
                    + "expected 'Class' or 'Function' but got " + tok.getLex());
        }

        return obj;
    }

    private ArrayList<PyObject> ClassFunctionList(ArrayList<PyObject> codeList) {
        PyToken tok = this.in.getToken();
        this.in.putBackToken();

        PyObject obj = null;
        String lexeme = tok.getLex();
        if (lexeme.equals("Function") || lexeme.equals("Class")) {
            obj = ClassFunDef();
            codeList.add(obj);
            codeList = ClassFunctionList(codeList);
        }

        return codeList;
    }

    private PyClass ClassDef() {
        String baseClass = "";
        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("Class")) {
            badToken(tok, "Expected Class keyword");
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Expected a ':'.");
        }

        PyToken className = this.in.getToken();

        if (className.getType() != TokenType.PYIDENTIFIERTOKEN) {
            badToken(className, "Expected an identifier.");
        }

        tok = this.in.getToken();

        if (tok.getLex().equals("(")) {
            baseClass = this.in.getToken().getLex();
            tok = this.in.getToken();
            if (!tok.getLex().equals(")")) {
                badToken(tok, "Expected a ')'.");
            }

            tok = this.in.getToken();
        }

        if (!tok.getLex().equals("BEGIN")) {
            badToken(tok, "Expected a BEGIN keyword.");
        }

        ArrayList<PyObject> nestedClassFunctionList = new ArrayList<PyObject>();

        nestedClassFunctionList = ClassFunctionList(nestedClassFunctionList);

        tok = this.in.getToken();

        if (!tok.getLex().equals("END")) {
            badToken(tok, "Expected a END keyword.");
        }

        PyClass cls = new PyClass(className.getLex(), nestedClassFunctionList, baseClass, globals);
        globals.put(className.getLex(), cls);
        return cls;
    }

    private PyCode FunDef() {
        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("Function")) {
            badToken(tok, "Expected Function keyword.");
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Excpected a ':'.");
        }

        PyToken funName = this.in.getToken();

        if (funName.getType() != TokenType.PYIDENTIFIERTOKEN) {
            badToken(funName, "Expected an identifier");
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals("/")) {
            badToken(tok, "Expected a '/'");
        }

        PyToken numArgsToken = this.in.getToken();
        int numArgs = 0;

        if (numArgsToken.getType() != TokenType.PYINTEGERTOKEN) {
            badToken(numArgsToken, "Expected an integer token");
        }

        /* Here we use some stringstreams to get the number of arguments.
            Can we just parse the int from the lexeme?*/
        try {
            numArgs = Integer.parseInt(numArgsToken.getLex());
        } catch (NumberFormatException e) {
            System.err.println(e);
            System.exit(0);
        }

        ArrayList<PyObject> nestedClassFunctionList = new ArrayList<PyObject>();

        nestedClassFunctionList = ClassFunctionList(nestedClassFunctionList);

        ArrayList<PyObject> constants = ConstPart(nestedClassFunctionList);

        ArrayList<String> locals = LocalsPart();

        ArrayList<String> freevars = FreeVarsPart();

        ArrayList<String> cellvars = CellVarsPart();

        ArrayList<String> globals = GlobalsPart();

        ArrayList<PyByteCode> instructions = BodyPart();

        return new PyCode(funName.getLex(), nestedClassFunctionList, constants,
                locals, freevars, cellvars, globals, instructions, numArgs);
    }

    private ArrayList<PyObject> ConstPart(ArrayList<PyObject> nestedClassFunctionList) {
        ArrayList<PyObject> constants = new ArrayList<PyObject>();

        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("Constants")) {
            this.in.putBackToken();
            return constants;
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Expected a ':'.");
        }

        constants = ValueList(constants, nestedClassFunctionList);

        return constants;
    }

    private ArrayList<PyObject> ValueList(ArrayList<PyObject> constants, ArrayList<PyObject> nestedClassFunctionList) {
        PyObject value = Value(nestedClassFunctionList);
        constants.add(value);
        constants = ValueRest(constants, nestedClassFunctionList);
        return constants;
    }

    private ArrayList<PyObject> ValueRest(ArrayList<PyObject> constants, ArrayList<PyObject> nestedClassFunctionList) {
        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals(",")) {
            this.in.putBackToken();
            return constants;
        }

        return ValueList(constants, nestedClassFunctionList);
    }

    private PyObject Value(ArrayList<PyObject> nestedClassFunctionList) {
        int iVal;
        float fVal;
        String sVal;
        PyToken codeId;

        PyToken tok = this.in.getToken();

        switch (tok.getType()) {
            case PYINTEGERTOKEN:
                try {
                    iVal = Integer.parseInt(tok.getLex());
                    return new PyInt(iVal);
                } catch (NumberFormatException e) {
                    System.err.println(e);
                    System.exit(0);
                }
                break;
            case PYFLOATTOKEN:
                try {
                    fVal = Float.parseFloat(tok.getLex());
                    return new PyFloat(fVal);
                } catch (NumberFormatException e) {
                    System.err.println(e);
                    System.exit(0);
                }
                break;
            case PYSTRINGTOKEN:
                sVal = tok.getLex();
                return new PyStr(sVal);
            case PYIDENTIFIERTOKEN:
                if (tok.getLex().equals("None")) {
                    return new PyNone();
                } else if (tok.getLex().equals("True")) {
                    return new PyBool(true);
                } else if (tok.getLex().equals("False")) {
                    return new PyBool(false);
                } else if (tok.getLex().equals("code")) {
                    tok = this.in.getToken();

                    if (!tok.getLex().equals("(")) {
                        badToken(tok, "Expected a '('.");
                    }

                    codeId = this.in.getToken();

                    if (codeId.getType() != TokenType.PYIDENTIFIERTOKEN) {
                        badToken(tok, "Expected an identifier.");
                    }

                    tok = this.in.getToken();

                    if (!tok.getLex().equals(")")) {
                        badToken(tok, "Expected a ')'.");
                    }

                    for (int i = 0; i < nestedClassFunctionList.size(); i++) {
                        PyCode code = (PyCode) nestedClassFunctionList.get(i);

                        if (code.getName().equals(codeId.getLex())) {
                            return code;
                        }
                    }

                    System.err.println("Identifier " + codeId.getLex() + " is not a function or class.");
                    badToken(codeId, "Must be name of inner function or class.");
                } else {
                    badToken(tok, "Expected None, int, float, str, True, or False.");
                }
                break;
            case PYLEFTPARENTOKEN:
                ArrayList<PyObject> tupleElements = new ArrayList<PyObject>();

                while (true) {

                    tupleElements.add(Value(nestedClassFunctionList));
                    
                    tok = this.in.getToken();
                    
                    if (tok.getLex().equals(")"))
                        return new PyTuple(tupleElements);
                    
                    if (!tok.getLex().equals(","))
                        badToken(tok,"Expected a , or ) in tuple.");
                }

            default:
                badToken(tok, "Expected None, int, float, str, True, False or tuple.");
                break;
        }
        /*  We should never reach this code, but java compiler requires a return
            statement. If we do reach this, there is an issue.*/
        return null;
    }

    private ArrayList<String> LocalsPart() {
        ArrayList<String> locals = new ArrayList<String>();

        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("Locals")) {
            this.in.putBackToken();
            return locals;
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Expected a ':'.");
        }

        return IdList(locals);
    }

    private ArrayList<String> FreeVarsPart() {
        ArrayList<String> freeVars = new ArrayList<String>();

        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("FreeVars")) {
            this.in.putBackToken();
            return freeVars;
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Expected a ':'.");
        }

        return IdList(freeVars);
    }

    private ArrayList<String> CellVarsPart() {
        ArrayList<String> cellVars = new ArrayList<String>();

        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("CellVars")) {
            this.in.putBackToken();
            return cellVars;
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Expected a ':'.");
        }

        return IdList(cellVars);
    }

    private ArrayList<String> IdList(ArrayList<String> lst) {
        PyToken tok = this.in.getToken();

        if (tok.getType() != TokenType.PYIDENTIFIERTOKEN) {
            badToken(tok, "Expected an identifier.");
        }

        lst.add(tok.getLex());

        return IdRest(lst);
    }

    private ArrayList<String> IdRest(ArrayList<String> lst) {
        PyToken tok = this.in.getToken();

        if (tok.getType() != TokenType.PYCOMMATOKEN) {
            this.in.putBackToken();
            return lst;
        }

        return IdList(lst);
    }

    private ArrayList<String> GlobalsPart() {
        ArrayList<String> globals = new ArrayList<String>();

        PyToken tok = this.in.getToken();

        if (!tok.getLex().equals("Globals")) {
            this.in.putBackToken();
            return globals;
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals(":")) {
            badToken(tok, "Expected a ':'.");
        }

        return IdList(globals);
    }

    private ArrayList<PyByteCode> BodyPart() {
        ArrayList<PyByteCode> instructions = new ArrayList<PyByteCode>();

        PyToken tok = this.in.getToken();

        this.target.clear();
        this.index = 0;

        if (!tok.getLex().equals("BEGIN")) {
            badToken(tok, "Expected a BEGIN keyword.");
        }

        instructions = InstructionList(instructions);

        //find the target of any labels in the byte code instructions
        for (int i = 0; i < instructions.size(); i++) {
            PyByteCode inst = instructions.get(i);
            String label = inst.getLabel();

            if (!label.equals("")) {
                String op = inst.getOpCodeName();
                instructions.remove(instructions.get(i));
                instructions.add(i, new PyByteCode(op, target.get(label)));
            }
        }

        tok = this.in.getToken();

        if (!tok.getLex().equals("END")) {
            badToken(tok, "Expected a END keyword.");
        }

        return instructions;
    }

    private ArrayList<PyByteCode> InstructionList(ArrayList<PyByteCode> instructions) {
        PyToken tok = this.in.getToken();
        this.in.putBackToken();

        if (tok.getLex().equals("END")) {
            return instructions;
        }

        PyByteCode inst = LabeledInstruction();

        instructions.add(inst);
        this.index++;

        return InstructionList(instructions);
    }

    private PyByteCode LabeledInstruction() {
        PyToken tok1 = this.in.getToken();
        String tok1Lex = tok1.getLex();

        PyToken tok2 = this.in.getToken();
        String tok2Lex = tok2.getLex();

        if (tok2Lex.equals(":")) {
            //what does this do?
            if (!this.target.containsKey(tok1Lex)) {
                this.target.put(tok1Lex, this.index);
            } else {
                badToken(tok1, "Duplicate label found.");
            }

            return LabeledInstruction();
        }

        int argCount = 0;

        try {
            argCount = PyByteCode.numArgs(tok1Lex);
        } catch (Exception e) {
            badToken(tok1, "Illegal Opcode.");
        }

        if (argCount == 0) {
            this.in.putBackToken();
            return new PyByteCode(tok1Lex);
        }

        if (tok2.getType() == TokenType.PYIDENTIFIERTOKEN) {
            //found a label to jump to
            try {
                return new PyByteCode(tok1Lex, tok2Lex);
            } catch (Exception e) {
                badToken(tok2, "Illegal Opcode.");
            }
        }

        if (tok2.getType() == TokenType.PYINTEGERTOKEN) {
            try {
                int operand = Integer.parseInt(tok2Lex);
                return new PyByteCode(tok1Lex, operand);
            } catch (NumberFormatException e) {
                System.err.println(e);
                System.exit(0);
            } catch (Exception e) {
                badToken(tok1, "Illegal Opcode.");
            }
        }

        badToken(tok1, "Instruction Syntax Error.");

        /* We should never reach this code, but Java will complain if we don't 
            have it here.*/
        return null;
    }
}
