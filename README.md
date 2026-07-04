# For Loop Analyzer

A Java application for syntactic and semantic analysis of C++ for loop statements, developed for the Theory of Formal Languages and Grammars (ТФЯиГ) university course.

## Assignment Requirements

### Task Description

Implement a syntactic analyzer for the C++ `for` loop statement. The language is case-sensitive. The grammar is defined as follows:

---

### Grammar

(1) start = for ( init-expr ; cond-expr ; loop-expr ) stmt ;

(2) init-expr = int id = int-numb

(3) cond-expr = id rel-oper term

(4) loop-expr = id ( ++ | -- )

(5) stmt = var = term { math-oper term }

(6) term = var | any-numb

(7) var = id [ [ id ] | [ int-numb ] ]

(8) any-numb = int-numb | fix-point-numb | real-numb

(9) math-oper = + | - | * | / | %

(10) rel-oper = == | < | <= | != | > | >=


---

### Lexical Rules

- **id (identifier)**:
  - Must start with a letter or underscore (`_`)
  - May contain letters, digits, and underscores
  - Maximum length: 12 characters
  - Must not be a reserved keyword

- **int-numb**:
  - Integer value in range `[-32768, 32767]`

- **fix-point-numb**:
  - Fixed-point number

- **real-numb**:
  - Floating-point number

---

### Semantic Rules

- Build and output identifier and constant tables after parsing is complete
- Enforce all constraints on identifiers and constants
- Ensure loop variable consistency:
  - The variable used in `init-expr`, `cond-expr`, and `loop-expr` must be identical
- Report errors with:
  - Cursor position
  - Clear description of the error

---

### Example of a valid expression

for (int ind = 0; ind == 100; ind++)
mass[ind] = mass[ind] % ind;

## Grammar Visualization

![Grammar diagram](docs/grammar.png)

## Features

- Parsing of C++ `for` loop syntax according to a predefined grammar
- Semantic validation
- Identifier and constant tables generation
- Detailed syntax error reporting
- Swing-based graphical interface(Note: The application interface is in Russian)

## Technologies

- Java
- Swing

## Project structure

```
src/
 ├── Main.java
 ├── MainFrame.java
 ├── Parser.java
 ├── Result.java
 └── ErrorType.java
```
