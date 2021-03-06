/* Parser.java */
/* Generated By:JavaCC: Do not edit this line. Parser.java */
package com.himamis.retex.editor.share.io.latex;

import com.himamis.retex.editor.share.model.*;
import com.himamis.retex.editor.share.meta.*;
import com.himamis.retex.editor.share.controller.*;

public class Parser implements ParserConstants {

    private MetaModel metaModel;
    private EditorState editorState;
    private InputController inputController;


    public Parser(MetaModel metaModel) {
                this(new StringProvider(""));
        this.metaModel = metaModel;
        this.editorState = new EditorState(metaModel);
        this.inputController = new InputController(metaModel);
    }

    private static MathComponent convertSuperscriptToNormal(MetaModel metaModel, char sup) {

            if (sup >= '\u2070' && sup <= '\u2079' && sup != '\u2071' && sup != '\u2072' && sup != '\u2073') {
                return createCharacter(Character.toString((char) (sup - '\u2070' + '0')), metaModel);
            } else if (sup == '\u00b9') {
                return createCharacter("1", metaModel);
            } else if (sup == '\u00b2') {
                return createCharacter("2", metaModel);
            } else if (sup == '\u00b3') {
                return createCharacter("3", metaModel);
            } else if (sup == '\u207a') {
                return createOperator("+", metaModel);
            } else if (sup == '\u207b') {
                return createOperator("-", metaModel);
            } else {
                throw new UnsupportedOperationException("Not a supported superscript");
            }
        }

    private static MathComponent createCharacter(String character, MetaModel metaModel) {
        return new MathCharacter(metaModel.getCharacter(character));
    }

    private static MathComponent createOperator(String operator, MetaModel metaModel) {
        return new MathCharacter(metaModel.getOperator(operator));
    }

    private static MetaCharacter createMetaCharacter(String character, MetaModel metaModel) {
        MetaCharacter metaCharacter = null;
        if (metaModel.isOperator(character)) {
            metaCharacter = metaModel.getOperator(character);
        } else if (metaModel.isSymbol(character)) {
            metaCharacter = metaModel.getSymbol(character);
        } else {
            metaCharacter = metaModel.getCharacter(character);
        }
        return metaCharacter;
    }

    public MathFormula parse(String text) throws ParseException {
        ReInit(new StringProvider(text));
        MathFormula mathFormula = new MathFormula(metaModel);
        MathSequence mathSequence = new MathSequence();
        mathFormula.setRootComponent(mathSequence);
        editorState.setRootComponent(mathSequence);
        mathContainer(mathSequence);
        return mathFormula;
    }

  final public void mathContainer(MathContainer currentField) throws ParseException {
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case POWER:
      case POWERN:
      case SUBSCRIPT:
      case FRACTION:
      case PARANTHESES_OPEN:
      case SQUARE_BRACKETS_OPEN:
      case CURLY_BRACKETS_OPEN:
      case LCEIL:
      case LFLOOR:
      case QUOTE_START:
      case SQRT:
      case NROOT:
      case LOG:
      case SPACE:
      case OPERATOR:
      case ELSE:{
        ;
        break;
        }
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case POWER:{
        power(currentField);
        break;
        }
      case POWERN:{
        superscript(currentField);
        break;
        }
      case SUBSCRIPT:{
        subscript(currentField);
        break;
        }
      case FRACTION:{
        fraction(currentField);
        break;
        }
      case SQRT:{
        sqrt(currentField);
        break;
        }
      case NROOT:{
        nroot(currentField);
        break;
        }
      case LOG:{
        log(currentField);
        break;
        }
      case PARANTHESES_OPEN:
      case SQUARE_BRACKETS_OPEN:{
        arrayParantheses(currentField);
        break;
        }
      case LCEIL:
      case LFLOOR:{
        arrayFloorCeil(currentField);
        break;
        }
      case CURLY_BRACKETS_OPEN:{
        arrayCurly(currentField);
        break;
        }
      case QUOTE_START:{
        quotes(currentField);
        break;
        }
      case SPACE:
      case OPERATOR:
      case ELSE:{
        mathCharacter(currentField);
        break;
        }
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

/**
* Adds an atomic expression to the current field. Atomic expression does not have operator on top level,
* i.e. must be wrapped in brackets (1+1) or be a simple number 12 or special function eg. -sqrt(5)
* @param currentField parent field 
*/
  final public void mathAtomOrPower(MathContainer currentField) throws ParseException {Token t = null;
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case SPACE:{
        ;
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        break label_2;
      }
      jj_consume_token(SPACE);
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case OPERATOR:{
      t = jj_consume_token(OPERATOR);
      break;
      }
    default:
      jj_la1[3] = jj_gen;
      ;
    }
if(t!=null)
    {
            currentField.addArgument(new MathCharacter(createMetaCharacter(t.image, metaModel)));
      }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case POWER:{
      power(currentField);
      break;
      }
    case POWERN:{
      superscript(currentField);
      break;
      }
    case SUBSCRIPT:{
      subscript(currentField);
      break;
      }
    case FRACTION:{
      fraction(currentField);
      break;
      }
    case SQRT:{
      sqrt(currentField);
      break;
      }
    case NROOT:{
      nroot(currentField);
      break;
      }
    case LOG:{
      log(currentField);
      break;
      }
    case PARANTHESES_OPEN:
    case SQUARE_BRACKETS_OPEN:{
      arrayParantheses(currentField);
      break;
      }
    case LCEIL:
    case LFLOOR:{
      arrayFloorCeil(currentField);
      break;
      }
    case CURLY_BRACKETS_OPEN:{
      arrayCurly(currentField);
      break;
      }
    case QUOTE_START:{
      quotes(currentField);
      break;
      }
    case ELSE:{
      label_3:
      while (true) {
        mathAtomCharacter(currentField);
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case ELSE:{
          ;
          break;
          }
        default:
          jj_la1[4] = jj_gen;
          break label_3;
        }
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PARANTHESES_OPEN:
      case SQUARE_BRACKETS_OPEN:{
        arrayParantheses(currentField);
        break;
        }
      default:
        jj_la1[5] = jj_gen;
        ;
      }
      break;
      }
    default:
      jj_la1[6] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case POWER:
    case POWERN:
    case SUBSCRIPT:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case POWER:{
        power(currentField);
        break;
        }
      case POWERN:{
        superscript(currentField);
        break;
        }
      case SUBSCRIPT:{
        subscript(currentField);
        break;
        }
      default:
        jj_la1[7] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
      }
    default:
      jj_la1[8] = jj_gen;
      ;
    }
  }

  final public void superscript(MathContainer currentField) throws ParseException {Token t = null;
    MathSequence mathSequence = null;
    t = jj_consume_token(POWERN);
MathFunction power = new MathFunction(metaModel.getGeneral(Tag.SUPERSCRIPT));
        mathSequence = new MathSequence();
        power.setArgument(0, mathSequence);
        currentField.addArgument(power);
        for(int i=0;i<t.image.length();i++)
        {

                MathComponent mathCharacter = convertSuperscriptToNormal(metaModel, t.image.charAt(i));
                mathSequence.addArgument(mathCharacter);
        }
  }

  final public void power(MathContainer currentField) throws ParseException {
    jj_consume_token(POWER);
MathFunction power = new MathFunction(metaModel.getGeneral(Tag.SUPERSCRIPT));
        MathSequence mathSequence = new MathSequence();
        mathAtomOrPower(mathSequence);
        mathSequence.removeBrackets();
        power.setArgument(0, mathSequence);
        currentField.addArgument(power);
  }

  final public void fraction(MathContainer currentField) throws ParseException {MathFunction fraction;
    jj_consume_token(FRACTION);
fraction = new MathFunction(metaModel.getGeneral(Tag.FRAC));
        //handle numerator
        MathSequence passArgs = new MathSequence();
        editorState.setCurrentField((MathSequence) currentField);
        editorState.setCurrentOffset(currentField.size());
        fraction.setArgument(0, passArgs);
        ArgumentHelper.passArgument(editorState, fraction);
        //handle denominator
        MathSequence mathSequence = new MathSequence();
        mathAtomOrPower(mathSequence);
        mathSequence.removeBrackets();
        fraction.setArgument(1, mathSequence);
        currentField.addArgument(fraction);
  }

  final public void subscript(MathContainer currentField) throws ParseException {MathSequence subscript = null;
    jj_consume_token(SUBSCRIPT);
MathFunction subscriptFunction = new MathFunction(metaModel.getGeneral(Tag.SUBSCRIPT));
        subscript = new MathSequence();
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case CURLY_BRACKETS_OPEN:{
      jj_consume_token(CURLY_BRACKETS_OPEN);
      mathContainer(subscript);
      jj_consume_token(CURLY_BRACKETS_CLOSE);
      break;
      }
    case SPACE:
    case OPERATOR:
    case ELSE:{
      mathCharacter(subscript);
      break;
      }
    default:
      jj_la1[9] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
subscriptFunction.setArgument(0, subscript);
        currentField.addArgument(subscriptFunction);
  }

  final public void sqrt(MathContainer currentField) throws ParseException {
    jj_consume_token(SQRT);
    jj_consume_token(PARANTHESES_OPEN);
MathFunction sqrt = new MathFunction(metaModel.getGeneral(Tag.SQRT));
        MathSequence mathSequence = new MathSequence();
        mathContainer(mathSequence);
        sqrt.setArgument(0, mathSequence);
        currentField.addArgument(sqrt);
    jj_consume_token(PARANTHESES_CLOSE);
  }

  final public void nroot(MathContainer currentField) throws ParseException {
    jj_consume_token(NROOT);
    jj_consume_token(PARANTHESES_OPEN);
MathFunction nroot = new MathFunction(metaModel.getGeneral(Tag.NROOT));
        MathSequence param1 = new MathSequence();
        mathContainer(param1);
    jj_consume_token(COMMA);
MathSequence param2 = new MathSequence();
        mathContainer(param2);
        nroot.setArgument(1, param1);
        nroot.setArgument(0, param2);
        currentField.addArgument(nroot);
    jj_consume_token(PARANTHESES_CLOSE);
  }

  final public void log(MathContainer currentField) throws ParseException {
    jj_consume_token(LOG);
    jj_consume_token(PARANTHESES_OPEN);
MathFunction nroot = new MathFunction(metaModel.getGeneral(Tag.LOG));
        MathSequence param1 = new MathSequence();
        MathSequence param2 = new MathSequence();
        mathContainer(param1);
        nroot.setArgument(0, param2); // base empty
        nroot.setArgument(1, param1);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case COMMA:{
      jj_consume_token(COMMA);
mathContainer(param2);
        nroot.setArgument(0, param1);
        nroot.setArgument(1, param2);
      break;
      }
    default:
      jj_la1[10] = jj_gen;
      ;
    }
    jj_consume_token(PARANTHESES_CLOSE);
currentField.addArgument(nroot);
  }

  final public void arrayParantheses(MathContainer currentField) throws ParseException {MathContainer mathArrayOrFunction = null;
    MathSequence mathSequence = null;
    Token open = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case PARANTHESES_OPEN:{
      open = jj_consume_token(PARANTHESES_OPEN);
      break;
      }
    case SQUARE_BRACKETS_OPEN:{
      open = jj_consume_token(SQUARE_BRACKETS_OPEN);
      break;
      }
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
editorState.setCurrentField((MathSequence) currentField);
        editorState.setCurrentOffset(currentField.size());
        inputController.newBraces(editorState, open.image.charAt(0));

        mathSequence = editorState.getCurrentField();
        mathArrayOrFunction = mathSequence.getParent();
    mathContainer(mathSequence);
//mathSequence is already in tree; just create a new one
            mathSequence = new MathSequence();
    label_4:
    while (true) {
      if (jj_2_1(2147483647)) {
        ;
      } else {
        break label_4;
      }
      jj_consume_token(COMMA);
      mathContainer(mathSequence);
mathArrayOrFunction.addArgument(mathSequence);
            mathSequence = new MathSequence();
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case PARANTHESES_CLOSE:{
      jj_consume_token(PARANTHESES_CLOSE);
      break;
      }
    case SQUARE_BRACKETS_CLOSE:{
      jj_consume_token(SQUARE_BRACKETS_CLOSE);
      break;
      }
    default:
      jj_la1[12] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void arrayFloorCeil(MathContainer currentField) throws ParseException {MathArray mathArray = null;
    MathSequence mathSequence = null;
    Token open = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case LFLOOR:{
      open = jj_consume_token(LFLOOR);
      break;
      }
    case LCEIL:{
      open = jj_consume_token(LCEIL);
      break;
      }
    default:
      jj_la1[13] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
mathSequence = new MathSequence();
                Tag arrayType = Tag.CEIL;
                if(LFLOOR == open.kind)
                {
                  arrayType = Tag.FLOOR;
                }
        mathArray = new MathArray(metaModel.getArray(arrayType), 0);
        currentField.addArgument(mathArray);
    mathContainer(mathSequence);
mathArray.addArgument(mathSequence);
            mathSequence = new MathSequence();
    label_5:
    while (true) {
      if (jj_2_2(2147483647)) {
        ;
      } else {
        break label_5;
      }
      jj_consume_token(COMMA);
      mathContainer(mathSequence);
mathArray.addArgument(mathSequence);
            mathSequence = new MathSequence();
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case RFLOOR:{
      jj_consume_token(RFLOOR);
      break;
      }
    case RCEIL:{
      jj_consume_token(RCEIL);
      break;
      }
    default:
      jj_la1[14] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void arrayCurly(MathContainer currentField) throws ParseException {MathArray mathArray = null;
    MathSequence mathSequence = null;
    int matrixWidth = -1;
    jj_consume_token(CURLY_BRACKETS_OPEN);
mathSequence = new MathSequence();

        mathArray = new MathArray(metaModel.getArray(Tag.CURLY), 0);
        currentField.addArgument(mathArray);
    mathContainer(mathSequence);
mathArray.addArgument(mathSequence);
            mathSequence = new MathSequence();
    label_6:
    while (true) {
      if (jj_2_3(2147483647)) {
        ;
      } else {
        break label_6;
      }
      jj_consume_token(COMMA);
      mathContainer(mathSequence);
mathArray.addArgument(mathSequence);
            mathSequence = new MathSequence();
    }
    jj_consume_token(CURLY_BRACKETS_CLOSE);
mathArray.checkMatrix(metaModel);
  }

  final public void quotes(MathContainer currentField) throws ParseException {MathArray mathArray = null;
    MathSequence mathSequence = null;
    Token t = null;
    jj_consume_token(QUOTE_START);
mathSequence = new MathSequence();
        mathArray = new MathArray(metaModel.getArray(Tag.APOSTROPHES), 0);
        mathArray.addArgument(mathSequence);
        currentField.addArgument(mathArray);
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case CHAR:{
        ;
        break;
        }
      default:
        jj_la1[15] = jj_gen;
        break label_7;
      }
      t = jj_consume_token(CHAR);
MathCharacter mathCharacter = new MathCharacter(metaModel.getCharacter(t.image));
        mathSequence.addArgument(mathCharacter);
    }
    jj_consume_token(QUOTE_END);
  }

  final public void mathCharacter(MathContainer currentField) throws ParseException {Token t = null;
    Token sp = null;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case OPERATOR:
    case ELSE:{
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ELSE:{
        t = jj_consume_token(ELSE);
        break;
        }
      case OPERATOR:{
        t = jj_consume_token(OPERATOR);
        break;
        }
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
currentField.addArgument(new MathCharacter(createMetaCharacter(t.image, metaModel)));
      break;
      }
    case SPACE:{
      sp = jj_consume_token(SPACE);
      if (jj_2_4(2147483647)) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case ELSE:{
          t = jj_consume_token(ELSE);
          break;
          }
        case OPERATOR:{
          t = jj_consume_token(OPERATOR);
          break;
          }
        default:
          jj_la1[17] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      } else {
        ;
      }
if (t != null) {
            MetaCharacter metaCharacter = createMetaCharacter(t.image, metaModel);
            if ((metaCharacter.getType() == MetaCharacter.CHARACTER ||
                metaCharacter.getType() == MetaCharacter.SYMBOL) ) {
                int s = currentField.size();
                MathComponent comp = currentField.getArgument(s - 1);
                if (comp instanceof MathCharacter) {
                    MathCharacter mc = (MathCharacter) comp;
                    if ((mc.isCharacter() || mc.isSymbol()) && !mc.isSeparator()) {
                        MetaCharacter times = metaModel.getOperator("*");
                        MathCharacter timesChar = new MathCharacter(times);
                        currentField.addArgument(timesChar);
                    }
                }
                if (comp instanceof MathFunction) {
                    MathFunction mf = (MathFunction) comp;
                    if (Tag.SUBSCRIPT == mf.getName()) {
                        MetaCharacter times = metaModel.getOperator("*");
                        MathCharacter timesChar = new MathCharacter(times);
                        currentField.addArgument(timesChar);
                    }
                }
            }
            currentField.addArgument(new MathCharacter(metaCharacter));
        }else if(currentField.size() >0)
              {
                             currentField.addArgument(new MathCharacter(createMetaCharacter("\u200b", metaModel)));
                }
      break;
      }
    default:
      jj_la1[18] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void mathAtomCharacter(MathContainer currentField) throws ParseException {Token t = null;
    t = jj_consume_token(ELSE);
currentField.addArgument(new MathCharacter(createMetaCharacter(t.image, metaModel)));
  }

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_3R_27()
 {
    if (jj_scan_token(NROOT)) return true;
    if (jj_scan_token(PARANTHESES_OPEN)) return true;
    if (jj_scan_token(COMMA)) return true;
    if (jj_scan_token(PARANTHESES_CLOSE)) return true;
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3_4()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(28)) {
    jj_scanpos = xsp;
    if (jj_scan_token(27)) return true;
    }
    return false;
  }

  private boolean jj_3R_26()
 {
    if (jj_scan_token(SQRT)) return true;
    if (jj_scan_token(PARANTHESES_OPEN)) return true;
    if (jj_scan_token(PARANTHESES_CLOSE)) return true;
    return false;
  }

  private boolean jj_3R_35()
 {
    if (jj_3R_33()) return true;
    return false;
  }

  private boolean jj_3R_34()
 {
    if (jj_scan_token(CURLY_BRACKETS_OPEN)) return true;
    if (jj_3R_8()) return true;
    if (jj_scan_token(CURLY_BRACKETS_CLOSE)) return true;
    return false;
  }

  private boolean jj_3R_30()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(19)) {
    jj_scanpos = xsp;
    if (jj_scan_token(17)) return true;
    }
    if (jj_3R_8()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_2()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_scan_token(20)) {
    jj_scanpos = xsp;
    if (jj_scan_token(18)) return true;
    }
    return false;
  }

  private boolean jj_3R_24()
 {
    if (jj_scan_token(SUBSCRIPT)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_34()) {
    jj_scanpos = xsp;
    if (jj_3R_35()) return true;
    }
    return false;
  }

  private boolean jj_3R_39()
 {
    if (jj_scan_token(SPACE)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_4()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3R_33()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_38()) {
    jj_scanpos = xsp;
    if (jj_3R_39()) return true;
    }
    return false;
  }

  private boolean jj_3R_38()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(28)) {
    jj_scanpos = xsp;
    if (jj_scan_token(27)) return true;
    }
    return false;
  }

  private boolean jj_3R_21()
 {
    if (jj_3R_33()) return true;
    return false;
  }

  private boolean jj_3R_20()
 {
    if (jj_3R_32()) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_19()
 {
    if (jj_3R_31()) return true;
    return false;
  }

  private boolean jj_3R_37()
 {
    if (jj_scan_token(CHAR)) return true;
    return false;
  }

  private boolean jj_3R_18()
 {
    if (jj_3R_30()) return true;
    return false;
  }

  private boolean jj_3R_17()
 {
    if (jj_3R_29()) return true;
    return false;
  }

  private boolean jj_3R_25()
 {
    if (jj_scan_token(FRACTION)) return true;
    return false;
  }

  private boolean jj_3R_16()
 {
    if (jj_3R_28()) return true;
    return false;
  }

  private boolean jj_3R_32()
 {
    if (jj_scan_token(QUOTE_START)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_37()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(QUOTE_END)) return true;
    return false;
  }

  private boolean jj_3R_15()
 {
    if (jj_3R_27()) return true;
    return false;
  }

  private boolean jj_3R_14()
 {
    if (jj_3R_26()) return true;
    return false;
  }

  private boolean jj_3R_13()
 {
    if (jj_3R_25()) return true;
    return false;
  }

  private boolean jj_3R_12()
 {
    if (jj_3R_24()) return true;
    return false;
  }

  private boolean jj_3R_29()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_scan_token(11)) {
    jj_scanpos = xsp;
    if (jj_scan_token(13)) return true;
    }
    if (jj_3R_8()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_1()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_scan_token(12)) {
    jj_scanpos = xsp;
    if (jj_scan_token(14)) return true;
    }
    return false;
  }

  private boolean jj_3R_11()
 {
    if (jj_3R_23()) return true;
    return false;
  }

  private boolean jj_3R_9()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) {
    jj_scanpos = xsp;
    if (jj_3R_13()) {
    jj_scanpos = xsp;
    if (jj_3R_14()) {
    jj_scanpos = xsp;
    if (jj_3R_15()) {
    jj_scanpos = xsp;
    if (jj_3R_16()) {
    jj_scanpos = xsp;
    if (jj_3R_17()) {
    jj_scanpos = xsp;
    if (jj_3R_18()) {
    jj_scanpos = xsp;
    if (jj_3R_19()) {
    jj_scanpos = xsp;
    if (jj_3R_20()) {
    jj_scanpos = xsp;
    if (jj_3R_21()) return true;
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3R_10()
 {
    if (jj_3R_22()) return true;
    return false;
  }

  private boolean jj_3R_22()
 {
    if (jj_scan_token(POWER)) return true;
    return false;
  }

  private boolean jj_3R_8()
 {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_9()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_36()
 {
    if (jj_scan_token(COMMA)) return true;
    return false;
  }

  private boolean jj_3_3()
 {
    if (jj_scan_token(COMMA)) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  private boolean jj_3R_23()
 {
    if (jj_scan_token(POWERN)) return true;
    return false;
  }

  private boolean jj_3R_28()
 {
    if (jj_scan_token(LOG)) return true;
    if (jj_scan_token(PARANTHESES_OPEN)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_36()) jj_scanpos = xsp;
    if (jj_scan_token(PARANTHESES_CLOSE)) return true;
    return false;
  }

  private boolean jj_3R_31()
 {
    if (jj_scan_token(CURLY_BRACKETS_OPEN)) return true;
    if (jj_3R_8()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_3()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(CURLY_BRACKETS_CLOSE)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[19];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x1deaab50,0x1deaab50,0x4000000,0x8000000,0x10000000,0x2800,0x11eaab50,0x150,0x150,0x1c008000,0x2000000,0x2800,0x5000,0xa0000,0x140000,0x40000000,0x18000000,0x18000000,0x1c000000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[4];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor. */
  public Parser(Provider stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(String dsl) throws ParseException, TokenMgrError {
      this(new StringProvider(dsl));
  }

  public void ReInit(String s) {
     ReInit(new StringProvider(s));
  }
  /** Reinitialise. */
  public void ReInit(Provider stream) {
	if (jj_input_stream == null) {
      jj_input_stream = new SimpleCharStream(stream, 1, 1);
   } else {
      jj_input_stream.ReInit(stream, 1, 1);
   }
   if (token_source == null) {
      token_source = new ParserTokenManager(jj_input_stream);
   }

    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 19; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk_f() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) {
       return;
    }

    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];

      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }

      for (int[] oldentry : jj_expentries) {
        if (oldentry.length == jj_expentry.length) {
          boolean isMatched = true;

          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              isMatched = false;
              break;
            }

          }
          if (isMatched) {
            jj_expentries.add(jj_expentry);
            break;
          }
        }
      }

      if (pos != 0) {
        jj_lasttokens[(jj_endpos = pos) - 1] = kind;
      }
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[31];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 19; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 31; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage, token_source == null ? null : ParserTokenManager.lexStateNames[token_source.curLexState]);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 4; i++) {
      try {
        JJCalls p = jj_2_rtns[i];

        do {
          if (p.gen > jj_gen) {
            jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
            switch (i) {
              case 0: jj_3_1(); break;
              case 1: jj_3_2(); break;
              case 2: jj_3_3(); break;
              case 3: jj_3_4(); break;
            }
          }
          p = p.next;
        } while (p != null);

        } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }

    p.gen = jj_gen + xla - jj_la; 
    p.first = token;
    p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
