# TKOM Zephyr

## Polecenie

Implementacja języka programowania ogólnego przeznaczenia według wytycznych:

- typowanie słabe
- zmienne są domyślnie stałe (język ma pozwalać na oba warianty, jeden jest "domyślny")
- zmienne przekazywane do funkcji domyślnie przez kopię (wartość)

Jako dodatkowy typ danych należy zaimplementować obsługę struktur i rekordu wariantowego (przy czym struktury potrzebne są w zasadzie po to, żeby rekord wariantowy w ogóle miał sens). Do obsługi rekordu wariantowego należy dostarczyć w języku jakąś operację, która pozwala w sensowny sposób dostać się do danych z rekordu.

## Opis funkcjonalności i przykłady użycia

### Główne założenia

Program składa się z definicji funkcji oraz typów (struktur lub rekordów wariantowych). Przy starcie programu wykonywana jest funkcja `main`.

```zephyr
main() {[README.md](README.md)
    // komentarz
    printLn"Hello World");
}
```

### Operatory

| Priorytet |               Operatory                |                   Znaczenie                    | Asocjacyjność |
|:---------:|:--------------------------------------:|:----------------------------------------------:|:-------------:|
|     8     |                  `.`                   |            Dostęp do pola struktury            |  Lewostronna  |
|     7     |                  `!`                   |              Negacja typu `bool`               |     Brak      |
|     7     |                  `-`                   |                 Negacja liczby                 |     Brak      |
|     6     |                `*`, `/`                | Mnożenie/dzielenie/dzielenie całkowitoliczbowe |  Lewostronna  |
|     5     |                `+`, `-`                |             Dodawanie/odejmowanie              |  Lewostronna  |
|     4     | `==`, `>`, `<`, `>=`, `<=`, `==`, `!=` |              Porównanie logiczne               |     Brak      |
|     3     |                 `and`                  |                Iloczyn logiczny                |  Lewostronna  |
|     2     |                  `or`                  |                 Suma logiczna                  |  Lewostronna  |
|     1     |                  `=`                   |                  Przypisanie                   |     Brak      |

### Konwersja typów

| Oryginalny typ |                    float                    |                  int                  |                   string                   |                               bool                                |
|:--------------:|:-------------------------------------------:|:-------------------------------------:|:------------------------------------------:|:-----------------------------------------------------------------:|
|     float      |                      -                      |        `5.4` -> `5` (odcięcie)        |              `2.4` -> `"2.4"`              | `1.0` -> `true`<br>`0.0` -> false<br>inna wartość -> błąd runtime |
|      int       |                `4` -> `4.0`                 |                   -                   |                `3` -> `"3"`                |  `1` -> `true`<br>`0` -> `false`<br>inna wartość -> błąd runtime  |
|     string     | `"3.14"` -> `3.14`<br>`"a"` -> błąd runtime | `"4"` -> `4`<br>`"a"` -> błąd runtime |                     -                      |               `"sth"` -> `true`<br>`""` -> `false`                |
|      bool      |     `true` -> `1.0`<br>`false` -> `0.0`     |    `true` -> `1`<br>`false` -> `0`    | `true` -> `"true"`<br>`false` -> `"false"` |                                 -                                 |

W przypadku przypisania wyniku do zmiennej, na samym końcu występuje konwersja do zadanego typu (jeśli jest możliwa).

#### Operatory `*` i `/`

Mnożenie i dzielenie interpretowane jedynie jako operacja matematyczna - rzutowanie na `float` lub `int`.  
Jedynie mnożenie dwóch liczb typu `int` daje typu `int`. Wszystkie inne kombinacje dają typ `float`.

#### Operator `+`

W zależności od sytuacji może oznaczać konkatenację ciągów znaków lub dodawanie liczb. W nietrywialnych sytuacjach preferowana jest konkatenacja.

```zephyr
main() {
    printLn3.1 + 3); // 6.1
    printLn"2.4" + 3.6); // "2.43.6"
    printLn3.6 + "2.4"); // "3.62.4"
    printLnfalse + "str"); // "falsestr"
}
```

#### Operatory `==` i `!=`

Kolejność argumentów nie ma wpływu na wynik.

- `int`, `float` - rzutowanie na `float`
- `string`, \* - rzutowanie na string

#### Reszta operatorów

Operatory `>`, `>=`, `<` i `<=` - rzutowanie do typu `float` lub `int`.  
Operatory `-` (odejmowanie) oraz `-` (negacja liczby) - rzutowanie do typu `float` lub `int`  
Operatory `and`, `or` oraz `!` - rzutowanie na `bool`

### Zmienne

Podstawowe typy zmiennych:

- float
- int
- string
- bool

Zasady:

- Zmienna musi zostać zainicjowana.
- Definiowanie zmiennych globalnych nie jest możliwe.
- Zmienne w funkcji zasłaniają się w kolejności deklaracji, zmienne w blokach żyją jedynie do końca bloku.

```zephyr
main() {
    int a = 5;
    printLna); // 5
}
```

```zephyr
main() {
    int a = 4;
    int a = 3;
    printLna); // 3
}
```

```zephyr
main() {
    int a = 5;
    {
        int a = 2;
        printLna); // 2
    }
    printLna); // 5
}
```

Zmienne są domyślnie stałe.

```zephyr
int a = 5
a = 7 // błąd
int mut b = 5
a = 7 // ok
```

### Funkcje i argumenty

- Przeciążanie funkcji nie jest możliwe

```zephyr
do_something(int a, int b) -> int {
    return a + b;
}
```

Funkcja bez zwracanej wartości (void)

```zephyr
func(int a) {
    printLna);
}
```

### Funkcje wbudowane

- print() - wypisuje tekst na standardowe wyjście
- printLn() - wypisuje tekst na standardowe wyjście z nową linią
- to_string() - jawna konwersja na typ `string` (potrzebna tylko w nietypowych przypadkach)
- to_float()
- to_int()
- to_bool()

### Przekazanie przez referencję, mutowalność

- int a - niemutowalna kopia
- int mut a - mutowalna kopia
- int ref a - referencja
- int mref a - mutowalna referencja (zmienia zewnętrzny obiekt)

```zephyr
func(int mref a) {
    a = a + 5;
}
main() -> int {
    int mut a = 5;
    func(a); // rzutowanie na referencję
    printLn(a); // 10
    return 0;
}
```

```zephyr
func(int ref a) {
    a = a + 5; // błąd
}
func(int a) {
    a = a + 5; // błąd
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

### Struktury i rekord wariantowy

#### Definicja i użycie

```zephyr
struct SomeStruct {
    int a,
    float b
}

union SomeUnion { SomeStruct, int }

main() {
    SomeStruct someStruct = {a: 1, b: 1.5};
    printLnsomeStruct.a); // 1

    SomeUnion someVariant = {a: 1, b: 1.5}; // ok
    SomeUnion otherVariant = 1; // ok
}
```

#### Mutowalność struktur

- Jeśli zmienna przechowująca strukturę jest niemutowalna, wszystkie jej pola są niemutowalne (wliczając pola zagnieżdżonych struktur)

```zephyr
struct SomeStruct {
    int a,
    float b
}
union SomeUnion { SomeStruct, int }

main() {
    SomeStruct someStruct = {a: 1, b: 1.5};
    someStruct.a = 1; // bład, zmienna someStruct jest niemutowalna

    SomeUnion mut a = {a: 1, b: 1.5}; // ok
    a = 1; // ok, zmiana rzeczywistego typu na int
}
```

#### Sprawdzenie typu rekordu wariantowego

Do sprawdzenia typu służy dedykowane wyrażenie `match`.

```zephyr
struct Result {
    int result
}
struct Error {
    int errno
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

### Pętle

```zephyr
main() {
    int mut i = 0;
    while(i < 10) {
        printLn(i);
        i = i + 1;
    }
}
```

### Instrukcje warunkowe

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

### Rekursja

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

## Specyfikacja składni i leksyki

### Warstwa składniowa

```ebnf
program = {struct_definition | union_definition | function_definition };

struct_definition = "struct", identifier, struct_members;
struct_members = "{", [struct_member, {",", struct_member}, [","]], "}";
struct_member = type, identifier;

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

#### Warstwa leksykalna

```ebnf
identifier = letter, {letter | digit};
int_literal = nonzero_digit, {digit};
float_literal = nonzero_digit, {digit}, ".", {digit};
string_literal = "\"", {character}, "\"";
bool_literal = "true" | "false";
```

`letter` - litera zdefiniowana przez funkcję Character.isLetter()_  
`digit` - cyfra zdefiniowana przez funkcję Character.isDigit()_  
`nonzero_digit` - digit z wyłączeniem znaku "0"  
`character` - dowolny znak Unicode

\* w celu zapewnienia wsparcia Unicode

## Implementacja

Interpreter napisany w języku Java.

### Lista tokenów

Do każdego tokenu dodawany jest nr linii oraz nr kolumny w źródle.

|         Typ tokenu         |                                    Wartość                                     |
|:--------------------------:|:------------------------------------------------------------------------------:|
|       Literał `int`        |                                Obliczona liczba                                |
|      Literał `float`       |                                Obliczona liczba                                |
|      Literał `string`      |                           Reprezentowany ciąg znaków                           |
|       Literał `bool`       |                        Reprezentowana wartość logiczna                         |
|         Komentarz          |                        Treść komentarza bez znaku `//`                         |
|       Identyfikator        |                             Wartość identyfikatora                             |
|       Słowo kluczowe       |                  Enum reprezentujący słowo kluczowe z listy\*                  |
| Operator negacji logicznej |                                      `!`                                       |
|  Operator multiplikatywny  |                                 Enum: `*`, `/`                                 |
|     Operator addytywny     |                                 Enum: `+`, `-`                                 |
|     Operator relacyjny     |                  Enum: `==`, `>`, `<`, `>=`, `<=`, `==`, `!=`                  |
|     Operator logiczny      |                               Enum: `and`, `or`                                |
|    Operator przypisania    |                                      `=`                                       |
|     Operator strzałki      |                                      `->`                                      |
|         Typ danych         | Struktura przechowująca odpowiednie nazwy typów wbudowanych lub identyfikatory |
|           Nawias           |                            Enum: `(`, `)`, `{`, `}`                            |
|         Separator          |                              Enum: `;`, `:`, `,`                               |

\* Lista słów kluczowych:

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

### Sposób uruchomienia

Interpreter języka uruchamiany z poziomu konsoli z argumentami:

- ścieżka do pliku z interpretowanym kodem
- opcjonalnie flaga `--parse-only`

### Moduły i interfejsy

Klasa Lexer przyjmuje w konstruktorze strumień wejściowy i posiada funkcję nextToken(), która generuje kolejny token. Stan leksera przechowywany jest w polach klasy.  
Wymienione wyżej typy tokenów reprezentowane są analogicznymi klasami.

Klasa Parser przyjmuje w konstruktorze obiekt leksera. Posiada funkcję parser(), która parsuje cały program i zwraca obiekt typu Program.

Klasa SemanticAnalizer przyjmuje obiekt typu Program, dokonuje analizy semantycznej i zwraca obiekt typu Program po poprawkach.

Klasa Interpreter przyjmuje w konstruktorze obiekt Program i posiada funkcję run(), która wołana jest tylko wtedy, gdy wyłączona jest flaga `--parse-only`.

### Opis sposobu testowania

Testowanie za pomocą biblioteki JUnit i wstrzykiwania zależności.

Testy jednostkowe leksera będą tworzyć strumień z przygotowanego tekstu języka i sprawdzać poprawność zwracanych tokenów z leksera.

Testy jednostkowe parsera tworzą mock obiektu leksera, który zwraca spreparowane tokeny. Sprawdzana jest poprawność generowania drzewa, jak i zgłaszanie odpowiednich wyjątków w sytuacjach wyjątkowych.

Testy integracyjne sprawdzają poprawność całego interpretera (wszystkich modułów) poprzez sprawdzanie generowanego standardowego wyjścia.

### Obsługa błędów

Błędy realizowane przez mechanizm wyjątków w języku Java.  
Rozróżniane między `LexicalError`, `SyntaxError` i `RuntimeError`.

W każdym przypadku na ekranie wyświetli się komunikat w formacie:

```plaintext
<nazwa błędu>: <komunikat> at <nr linii>:<nr kolumny>
```

Przykład:

```plaintext
SyntaxError: Unexpected token "}" at 10:12
```

### Uruchomienie

Wymagana jest Java w wersji >= 17.

- `make build` - zbudowanie archiwum JAR
- `make run-example` - uruchomienie przykładowego programu (examples/hello_world.ze)
- `make test` - uruchomienie testów i generacja raportu w katalogu `build/reports/jacoco/test/html/index.html`