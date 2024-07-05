# Zephyr

## General overview

This repository contains an interpreter for a new language called "Zephyr".

Features:

- Statically and weak typed language
- Variables are immutable by default
- Variables passed to function create a copy by default
- Structures and union (variant) types

## Functional specification and usage examples

### Program structure

A program consists of function definitions and types (structures or variant records). The main function is executed at the start of the program.
```zephyr
main() {
    // comment
    printLn("Hello World");
}
```

### Operators

| Priority |               Operators                |                   Meaning                    | Associativity |
|:--------:|:--------------------------------------:|:--------------------------------------------:|:-------------:|
|    8     |                  `.`                   |            Structure field access            | Left-to-right |
|    7     |                  `!`                   |               Boolean negation               |     None      |
|    7     |                  `-`                   |               Number negation                |     None      |
|    6     |                `*`, `/`                | Multiplication / Division / Integer division | Left-to-right |
|    5     |                `+`, `-`                |            Addition / Subtraction            | Left-to-right |
|    4     | `==`, `>`, `<`, `>=`, `<=`, `==`, `!=` |              Logical comparison              |     None      |
|    3     |                 `and`                  |             Logical conjunction              | Left-to-right |
|    2     |                  `or`                  |             Logical disjunction              | Left-to-right |
|    1     |                  `=`                   |                  Assignment                  |     None      |



### Type conversion

| Original type |                    float                     |                  int                   |                   string                   |                               bool                                |
|:-------------:|:--------------------------------------------:|:--------------------------------------:|:------------------------------------------:|:-----------------------------------------------------------------:|
|     float     |                      -                       |       `5.4` -> `5` (truncation)        |              `2.4` -> `"2.4"`              | `1.0` -> `true`<br>`0.0` -> false<br>other value -> runtime error |
|      int      |                 `4` -> `4.0`                 |                   -                    |                `3` -> `"3"`                |  `1` -> `true`<br>`0` -> `false`<br>other value -> runtime error  |
|    string     | `"3.14"` -> `3.14`<br>`"a"` -> runtime error | `"4"` -> `4`<br>`"a"` -> runtime error |                     -                      |               `"sth"` -> `true`<br>`""` -> `false`                |
|     bool      |     `true` -> `1.0`<br>`false` -> `0.0`      |    `true` -> `1`<br>`false` -> `0`     | `true` -> `"true"`<br>`false` -> `"false"` |                                 -                                 |

Converting a struct to `string` yields the struct's string representation.  
When assigning the result to a variable, conversion to the specified type occurs at the end (if possible).

#### `*` and `/` operators

Multiplication and division are interpreted solely as mathematical operations - casting to float or int.
Only multiplying two int numbers gives an int type. All other combinations yield a float.

#### `+` operator

Depending on the context, it can mean string concatenation or number addition. In non-trivial situations, concatenation is preferred.

```zephyr
main() {
    printLn3.1 + 3); // 6.1
    printLn"2.4" + 3.6); // "2.43.6"
    printLn3.6 + "2.4"); // "3.62.4"
    printLnfalse + "str"); // "falsestr"
}
```

#### `==` and `!=` operators

Argument order does not affect the result.

- `int`, `float` - casting to `float`
- `string`, \* - casting to `string`

#### Reszta operatorów

Operators `>`, `>=`, `<` i `<=` - casting to `float` or `int`.  
Operators `-` (subtraction) and `-` (number negation) - casting to `float` or `int`  
Operators `and`, `or` and `!` - casting to `bool`

### Variables

Basic variable types:

- float
- int
- string
- bool

Rules:

- A variable must be initialized.
- Defining global variables is not possible.
- Variables within a function overshadow each other in the order of declaration, and variables within blocks live only until the end of the block.

```zephyr
main() {
    int a = 5;
    printLn(a); // 5
}
```

```zephyr
main() {
    int a = 4;
    int a = 3;
    printLn(a); // 3
}
```

```zephyr
main() {
    int a = 5;
    {
        int a = 2;
        printLn(a); // 2
    }
    printLn(a); // 5
}
```

Variables are immutable by default

```zephyr
int a = 5
a = 7 // error
int mut b = 5
a = 7 // ok
```

### Functions and parameters

- Function overloading is not possible

```zephyr
do_something(int a, int b) -> int {
    return a + b;
}
```

Function with no return value (void)

```zephyr
func(int a) {
    printLna);
}
```

### Built-in Functions

- print() - prints text to standard output
- printLn() -  prints text to standard output with a new line
- to_string() - explicit conversion to `string` (needed only in unusual cases)
- to_float() - explicit conversion to `float` (needed only in unusual cases)
- to_int() - explicit conversion to `int` (needed only in unusual cases)
- to_bool() - explicit conversion to `bool` (needed only in unusual cases)

### Pass by Reference, Mutability

- int a - immutable copy
- int mut a - mutable copy
- int ref a - reference
- int mref a - mutable reference (modifies external object)

```zephyr
func(int mref a) {
    a = a + 5;
}
main() -> int {
    int mut a = 5;
    func(a); // cast to reference
    printLn(a); // 10
    return 0;
}
```

```zephyr
func(int ref a) {
    a = a + 5; // error
}
func(int a) {
    a = a + 5; // error
}
```

```zephyr
func(int mut a) {
    a = a + 5;
    printLn(a) // 10
}
main() {
    int mut a = 5;
    func(a);
    printLn(a); // 5
    a = a + 5;
    printLn(a); // 10
}
```

### Structures and unions

#### Definition and usage

```zephyr
struct SomeStruct {
    a: int,
    b: float
}

union SomeUnion { SomeStruct, int }

main() {
    SomeStruct someStruct = {a: 1, b: 1.5};
    printLnsomeStruct.a); // 1

    SomeUnion someVariant = {a: 1, b: 1.5}; // ok
    SomeUnion otherVariant = 1; // ok
}
```

#### Structure mutability

- If the variable holding the structure is immutable, all its fields are immutable (including fields of nested structures)

```zephyr
struct SomeStruct {
    a: int,
    b: float
}
union SomeUnion { SomeStruct, int }

main() {
    SomeStruct someStruct = {a: 1, b: 1.5};
    someStruct.a = 1; // error, the variable someStruct is immutable

    SomeUnion mut a = {a: 1, b: 1.5}; // ok
    a = 1; // ok, change the actual type to int
}
```

#### Checking the type of union

To check the type, a dedicated match expression is used.
From the perspective of the match expression, types with the same structure are considered equal.

```zephyr
struct Result {
    result: int
}
struct Error {
    errno: int
}
union Maybe { Result, Error }

do_something() -> Maybe {
    return {result: 1};
}

main() {
    Maybe maybe = do_something();
    match(maybe) {
        case (Result res) {
            printLn("Result: " + res.result);
        }
        case (Error err) {
            printLn("Error: " + err.errno);
        }
    }
}
```

### Loops

```zephyr
main() {
    int mut i = 0;
    while(i < 10) {
        printLn(i);
        i = i + 1;
    }
}
```

### Conditional statements

```zephyr
main() {
    int a = 5;
    if (a > 10) {
        printLn("Bigger than 10");
    }
    elif (a > 4) {
        printLn("Bigger than 4");
    }
    else {
        printLn("Some other number");
    }
}
```

### Recursion

```zephyr
fib(int n) -> int {
    if(n <= 1) {
        return n;
    }
    else {
        return fib(n-1) + fib(n-2);
    }
    return 0;
}
main() {
    printLn(fib(10));
}
```

## Grammar and lexical specification

### Grammar

```ebnf
program = {struct_definition | union_definition | function_definition };

struct_definition = "struct", identifier, struct_members;
struct_members = "{", [struct_member, {",", struct_member}, [","]], "}";
struct_member = identifier, ":", type;

union_definition = "union", identifier, union_members;
union_members = "{", [type, {",", type}, [","]], "}";

function_definition = identifier, parameters, ["->", type], block;

parameters = "(", [parameter_definition, {",", parameter_definition}], ")";
parameter_definition = type, parameter_modifier, identifier, ["=", literal];
parameter_modifier = "mut", "ref", "mref";

block = "{", {statement, [";"]}, "}";
statement = assignment
          | variable_declaration
          | return_statement
          | loop
          | conditional_statement
          | match_statement
          | function_call
          | block;

assignment = identifier, {".", identifier}, "=", expression;

variable_declaration = type, [variable_modifier], identifier, "=", expression;
variable_modifier = "mut";
type = builtin_type | identifier;

return_statement = "return", [expression];

loop = "while", "(", expression, ")", block;

conditional_statement = "if", "(", expression, ")", block,
                        {"elif", "(", expression, ")", block},
                        ["else", "(", expression, ")", block];

match_statement = "match", "(", expression, ")", "{", {case_statement}, "}";
case_statement = "case", "(", type, identifier, ")", block;

function_call = identifier, arguments;
arguments = "(", [expression, {",", expression}], ")"

expression = and_term, {"or", and_term};
and_term = comparison_expression, {"and", comparison_expression};
comparison_expression = additive_term, [(">" | "<" | ">=" | "<=" | "==" | "!="), additive_term];
additive_term = term, {("+" | "-"), term};
term = factor, {("*" | "/"), factor};
factor = ["-" | "!"], (dot_expression | factor);
dot_expression = elementary_expression, {".", elementary_expression};

elementary_expresssion = identifier
                       | "(", expression, ")"
                       | literal
                       | function_call;

literal = int_literal | float_literal | string_literal | bool_literal | struct_literal;

struct_literal = "{", {struct_literal_member}, "}";
struct_literal_member = identifier, ":", literal;


builtin_type = "int" | "bool" | "float" | "string";
```

#### Lexical part

```ebnf
identifier = letter, {letter | digit};
int_literal = nonzero_digit, {digit};
float_literal = nonzero_digit, {digit}, ".", {digit};
string_literal = "\"", {character}, "\"";
bool_literal = "true" | "false";
```

`letter` - letter as defined by Character.isLetter() in Java\*  
`digit` - digit as defined by Character.isDigit() in Java\*  
`nonzero_digit` - digit except "0"  
`character` - any Unicode character

\* in order to support Unicode

## Lexer specification

### Token list

Each token includes the line and column number in the source.

|   Token type    |           Value            |
|:---------------:|:--------------------------:|
| Integer literal |      Calculated value      |
|  Float literal  |      Calculated value      |
| String literal  |  Represented string value  |
| Boolean literal | Represented logical value  |
|     Comment     | Comment content without // |
|   Identifier    |      Identifier value      |
|       EOF       |           (none)           |

Additionally, each operator, keyword, and separator has its own dedicated type.

Keyword list:

- `struct`
- `mut`
- `ref`
- `mref`
- `return`
- `while`
- `if`
- `elif`
- `else`
- `match`
- `case`
- `union`
- `true`
- `false`
- `or`
- `and`

Operators are listed [above](#operators)

Separators:

- `,`
- `:`
- `;`
- `(`
- `)`
- `{`
- `}`


## Quick program overview

Main classes:

- [Main](src/main/java/me/jkowalc/zephyr/Main.java) - main class and parsing console arguments
- [Lexer](src/main/java/me/jkowalc/zephyr/lexer/Lexer.java) - lexical parsing
- [Parser](src/main/java/me/jkowalc/zephyr/parser/Parser.java) - grammar parsing
- [TypeBuilder](src/main/java/me/jkowalc/zephyr/analyzer/TypeBuilder.java) - checking and building types for the program
- [StaticAnalyzer](src/main/java/me/jkowalc/zephyr/analyzer/StaticAnalyzer.java) - static analysis of the program
- [Interpreter](src/main/java/me/jkowalc/zephyr/interpreter/Interpreter.java) - interpreting the program

## Running

Java version >= 17 is required.

- `make build` - build the 
- `make run-example` - run the example program (examples/main_example.ze)
- `make test` - run tests and generate a report in the build/reports/jacoco/test/html/index.html directory

## Potential improvements

- Refactor the Interpreter and possibly StaticAnalyzer to use the Visitor pattern for operations (addition, multiplication, etc.)
- Reduce the amount of instanceof checks in the Interpreter for better maintainability
- Add more tests
- (possibly) Remove the endPosition fields in tokens and AST nodes as they are not used
- Change the struct definition syntax to match the struct literal
- Better error messages when the user tries to parse a completely invalid program (e.g. only a single number, some random characters)
- Change all Map implementations to List in the AST nodes (check for duplicates in the StaticAnalyzer)
- Better error messages in regard to the function return type