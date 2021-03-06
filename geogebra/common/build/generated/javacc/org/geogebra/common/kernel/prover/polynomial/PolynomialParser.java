/* PolynomialParser.java */
/* Generated By:JavaCC: Do not edit this line. PolynomialParser.java */
package org.geogebra.common.kernel.prover.polynomial;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

public class PolynomialParser implements PolynomialParserConstants {
  public static PPolynomial parsePolynomial(String string, Set < PVariable > variables) throws ParseException
  {
    Iterator < PVariable > variablesIterator = variables.iterator();
    HashMap < String, PVariable > variableMap = new HashMap < String, PVariable > ();
    while (variablesIterator.hasNext())
    {
      PVariable variable = variablesIterator.next();
      variableMap.put(variable.getName(), variable);
    }
    PolynomialParser parser = new PolynomialParser(new StringProvider(string));
    return parser.polynomialStartingPoint(variableMap);
  }

  public static Set < PPolynomial > parsePolynomialFactors(String string, Set < PVariable > variables) throws ParseException
  {
    Iterator < PVariable > variablesIterator = variables.iterator();
    HashMap < String, PVariable > variableMap = new HashMap < String, PVariable > ();
    while (variablesIterator.hasNext())
    {
      PVariable variable = variablesIterator.next();
      variableMap.put(variable.getName(), variable);
    }
    PolynomialParser parser = new PolynomialParser(new StringProvider(string));
    return parser.polynomialFactorsStartingPoint(variableMap);
  }

  public static Set<Set < PPolynomial > > parseFactoredPolynomialSet(String string, Set <PVariable > variables) throws ParseException
  {
    Iterator < PVariable > variablesIterator = variables.iterator();
    HashMap < String, PVariable > variableMap = new HashMap < String, PVariable > ();
    while (variablesIterator.hasNext())
    {
      PVariable variable = variablesIterator.next();
      variableMap.put(variable.getName(), variable);
    }
    PolynomialParser parser = new PolynomialParser(new StringProvider(string));
    return parser.polynomialFactoredSetStartingPoint(variableMap);
  }

  final public Set<Set < PPolynomial >> polynomialFactoredSetStartingPoint(HashMap <String, PVariable > variables) throws ParseException {Set <Set <PPolynomial > > factoredPolynomials = new HashSet<Set <PPolynomial > > ();
  Set <PPolynomial > factors;
    jj_consume_token(STARTFACTLIST);
    factors = polynomialFactorsStartingPoint(variables);
factoredPolynomials.add(factors);
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case ANYFACTLIST:{
        ;
        break;
        }
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      jj_consume_token(ANYFACTLIST);
      factors = polynomialFactorsStartingPoint(variables);
factoredPolynomials.add(factors);
    }
{if ("" != null) return factoredPolynomials;}
    throw new Error("Missing return statement in function");
  }

  final public Set < PPolynomial > polynomialFactorsStartingPoint(HashMap < String, PVariable > variables) throws ParseException {Set < PPolynomial > polynomials = new HashSet < PPolynomial > ();
  PPolynomial poly;
    jj_consume_token(STARTFACTLIST);
    label_2:
    while (true) {
      jj_consume_token(INDEXPOLY);
      poly = polynomial(variables);
if (!poly.isOne())
      {
         polynomials.add(poly);
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case INDEXPOLY:{
        ;
        break;
        }
      default:
        jj_la1[1] = jj_gen;
        break label_2;
      }
    }
    jj_consume_token(ANYFACTLIST);
    jj_consume_token(NUMBER);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case COMMA:{
        ;
        break;
        }
      default:
        jj_la1[2] = jj_gen;
        break label_3;
      }
      jj_consume_token(COMMA);
      jj_consume_token(NUMBER);
    }
{if ("" != null) return polynomials;}
    throw new Error("Missing return statement in function");
  }

  final public PPolynomial polynomialStartingPoint(HashMap < String, PVariable > variables) throws ParseException {PPolynomial p;
    p = polynomial(variables);
{if ("" != null) return p;}
    throw new Error("Missing return statement in function");
  }

  final public PPolynomial polynomial(HashMap < String, PVariable > variables) throws ParseException {PPolynomial poly;
  PPolynomial t;
    poly = term(variables);
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PLUS:
      case MINUS:{
        ;
        break;
        }
      default:
        jj_la1[3] = jj_gen;
        break label_4;
      }
      switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
      case PLUS:{
        jj_consume_token(PLUS);
        t = term(variables);
poly = poly.add(t);
        break;
        }
      case MINUS:{
        jj_consume_token(MINUS);
        t = term(variables);
poly = poly.subtract(t);
        break;
        }
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
{if ("" != null) return poly;}
    throw new Error("Missing return statement in function");
  }

  final public PPolynomial term(HashMap < String, PVariable > variables) throws ParseException {PTerm t = new PTerm();
  PTerm singleTerm;
  Token numberToken;
  BigInteger number = BigInteger.ONE;
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case MINUS:{
      jj_consume_token(MINUS);
number = BigInteger.ZERO.subtract(BigInteger.ONE); // -1

      break;
      }
    default:
      jj_la1[5] = jj_gen;
      ;
    }
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case NUMBER:{
      numberToken = jj_consume_token(NUMBER);
number = number.multiply(new BigInteger(numberToken.image));
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case TIMES:{
          ;
          break;
          }
        default:
          jj_la1[6] = jj_gen;
          break label_5;
        }
        jj_consume_token(TIMES);
        singleTerm = power(variables);
t = t.times(singleTerm);
      }
      break;
      }
    case VARIABLE:{
      singleTerm = power(variables);
t = t.times(singleTerm);
      label_6:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
        case TIMES:{
          ;
          break;
          }
        default:
          jj_la1[7] = jj_gen;
          break label_6;
        }
        jj_consume_token(TIMES);
        singleTerm = power(variables);
t = t.times(singleTerm);
      }
      break;
      }
    default:
      jj_la1[8] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
{if ("" != null) return new PPolynomial(number, t);}
    throw new Error("Missing return statement in function");
  }

  final public PTerm power(HashMap < String, PVariable > variables) throws ParseException {PVariable coefficient;
  Token numberToken;
  int number = 1;
    coefficient = variable(variables);
    switch ((jj_ntk==-1)?jj_ntk_f():jj_ntk) {
    case POWER:{
      jj_consume_token(POWER);
      numberToken = jj_consume_token(NUMBER);
number = Integer.parseInt(numberToken.image);
      break;
      }
    default:
      jj_la1[9] = jj_gen;
      ;
    }
{if ("" != null) return new PTerm(coefficient, number);}
    throw new Error("Missing return statement in function");
  }

  final public PVariable variable(HashMap < String, PVariable > variables) throws ParseException {Token variable;
  PVariable var;
    variable = jj_consume_token(VARIABLE);
if ((var = variables.get(variable.toString())) != null) {if ("" != null) return var;}
    {if (true) throw new ParseException();}
    throw new Error("Missing return statement in function");
  }

  /** Generated Token Manager. */
  public PolynomialParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private int jj_gen;
  final private int[] jj_la1 = new int[10];
  static private int[] jj_la1_0;
  static {
      jj_la1_init_0();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x100,0x200,0x400,0x1800,0x1800,0x1000,0x2000,0x2000,0x60,0x4000,};
   }

  /** Constructor. */
  public PolynomialParser(Provider stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new PolynomialParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Constructor. */
  public PolynomialParser(String dsl) throws ParseException, TokenMgrError {
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
      token_source = new PolynomialParserTokenManager(jj_input_stream);
   }

    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Constructor with generated Token Manager. */
  public PolynomialParser(PolynomialParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  /** Reinitialise. */
  public void ReInit(PolynomialParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 10; i++) jj_la1[i] = -1;
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
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

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[15];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 10; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 15; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage, token_source == null ? null : PolynomialParserTokenManager.lexStateNames[token_source.curLexState]);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

}
