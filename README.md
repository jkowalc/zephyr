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
main() {
    // komentarz
    print("Hello World");
}
```

### Operatory

| Priorytet |               Operatory                |                   Znaczenie                    | Asocjacyjność |
| :-------: | :------------------------------------: | :--------------------------------------------: | :-----------: |
|     7     |                  `!`                   |              Negacja typu `bool`               |     Brak      |
|     7     |                  `-`                   |                 Negacja liczby                 |     Brak      |
|     6     |                `*`, `/`                | Mnożenie/dzielenie/dzielenie całkowitoliczbowe |  Lewostronna  |
|     5     |                `+`, `-`                |             Dodawanie/odejmowanie              |  Lewostronna  |
|     4     | `==`, `>`, `<`, `>=`, `<=`, `==`, `!=` |              Porównanie logiczne               |     Brak      |
|     3     |                 `and`                  |                Iloczyn logiczny                |  Lewostronna  |
|     2     |                  `or`                  |                 Suma logiczna                  |  Lewostronna  |
|     1     |                  `=`                   |                  Przypisanie                   |     Brak      |

### Konwersja typów

Brak mechanizmu rozpoznawania 1 jako true i 0 jako false znanego z C++.

| Oryginalny typ |                    float                    |                  int                  |                   string                   |                 bool                 |
| :------------: | :-----------------------------------------: | :-----------------------------------: | :----------------------------------------: | :----------------------------------: |
|     float      |                      -                      |        `5.4` -> `5` (odcięcie)        |              `2.4` -> `"2.4"`              |                 błąd                 |
|      int       |                `4` -> `4.0`                 |                   -                   |                `3` -> `"3"`                |                 błąd                 |
|     string     | `"3.14"` -> `3.14`<br>`"a"` -> błąd runtime | `"4"` -> `4`<br>`"a"` -> błąd runtime |                     -                      | `"sth"` -> `true`<br>`""` -> `false` |
|      bool      |                    błąd                     |                 błąd                  | `true` -> `"true"`<br>`false` -> `"false"` |                  -                   |

W przypadku przypisania wyniku do zmiennej, na samym końcu występuje konwersja do zadanego typu (jeśli jest możliwa).

#### Operatory `*` i `/`

Operacje mnożenia i dzielenia na typach `string` i `bool` są zabronione.  
Jedynie mnożenie dwóch liczb typu `int` daje typu `int`. Wszystkie inne kombinacje dają typ `float`.

#### Operator `+`

W zależności od sytuacji może oznaczać konkatenację ciągów znaków lub dodawanie liczb. W nietrywialnych sytuacjach preferowana jest konkatenacja.

```zephyr
main() {
    print(3.1 + 3); // 6.1
    print("2.4" + 3.6); // "2.43.6"
    print(3.6 + "2.4"); // "3.62.4"
    print(false + "str"); // "falsestr"
}
```

#### Operatory `>`, `>=`, `<` i `<=`

Operacje na typie `bool` są zabronione.
W innych przypadkach występuje rzutowanie na `float` lub `int`.

#### Reszta operatorów

Operatory `-` (odejmowanie) oraz `-` (negacja liczby) działają jedynie na liczbach.  
Operatory `and`, `or` oraz `!` działają jedynie na typie `bool`

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
    print(a); // 5
}
```

```zephyr
main() {
    int a = 4;
    int a = 3;
    print(a); // 3
}
```

```zephyr
main() {
    int a = 5;
    {
        int a = 2;
        print(a); // 2
    }
    print(a); // 5
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
do_something(int a, int b = 5) -> int {
    return a + b;
}
```

Funkcja bez zwracanej wartości (void)

```zephyr
func(int a) {
    print(a);
}
```

### Funkcje wbudowane

- print() - wypisuje tekst na standardowe wyjście
- to_str() - jawna konwersja na typ `string` (potrzebna tylko w nietypowych przypadkach)
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
    print(a); // 10
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
    print(a) // 10
}
main() -> int {
    int mut a = 5;
    func(a);
    print(a); // 5
    a = a + 5;
    print(a); // 10
}
```

### Struktury i rekord wariantowy

#### Definicja i użycie

```zephyr
struct SomeStruct {
    int a,
    float b
}

main() {
    SomeStruct someStruct = {a: 1, b: 1.5};
    print(someStruct.a); // 1

    union<SomeStruct | int> someVariant = {a: 1, b: 1.5}; // ok
    union<SomeStruct | int> otherVariant = 1; // ok
}
```

#### Mutowalność struktur

- Jeśli zmienna przechowująca strukturę jest niemutowalna, wszystkie jej pola są niemutowalne (wliczając pola zagnieżdżonych struktur)

```zephyr
type SomeStruct = {
    int a,
    float b
};
main() {
    SomeStruct someStruct = {a: 1, b: 1.5};
    someStruct.a = 1; // bład, zmienna someStruct jest niemutowalna

    union<SomeStruct | int> mut a = {a: 1, b: 1.5}; // ok
    a = 1; // ok, zmiana rzeczywistego typu na int
}
```

#### Sprawdzenie typu rekordu wariantowego

Do sprawdzenia typu służy dedykowane wyrażenie `match`.

```zephyr
struct Result {
    int result;
}
struct Error {
    int errno;
}

do_something() -> union<Result | Error> {
    return {result: 1};
}

main() {
    union<Result | Error> res = do_something();
    match(res) {
        case Result {
            print("Result: " + res.result);
        }
        case Error {
            print("Error: " + res.errno);
        }
    }
}
```

### Pętle

```zephyr
main() {
    int mut i = 0;
    while(i < 10) {
        print(i);
        i = i + 1;
    }
}
```

### Instrukcje warunkowe

```zephyr
main() {
    int a = 5;
    if (a > 10) {
        print("Bigger than 10");
    }
    elif (a > 4) {
        print("Bigger than 4");
    }
    else {
        print("Some other number");
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
}
```

## Specyfikacja składni i leksyki

### Warstwa składniowa

```ebnf
program = {comment | struct_definition | function_definition};

comment = "//", {letter | digit};

struct_definition = "struct", identifier, "{", {struct_member}, "}";
struct_member = builtin_type, identifier, ",";

function_definition = identifier, "(", arguments, ")", ["->", type], block;

arguments = [argument_definition, {",", argument_definition}];
argument_definition = type, argument_modifier, identifier, ["=", literal];
argument_modifier = "mut", "ref", "mref";

block = "{", {statement}, "}";
statement = assignment
          | variable_declaration
          | return_statement
          | loop
          | conditional_statement
          | match_statement
          | function_call_statement
          | block
          | comment;

assignment = identifier, "=", (expression | function_call), ";";

variable_declaration = type, variable_modifier, "=", expression, ";";
variable_modifier = "mut";
type = builtin_type | identifier | union_type;
union_type = "union", "<", type, {"|", type}, ">";

return_statement = "return", expression, ";";

loop = "while", "(", expression, ")", block;

conditional_statement = "if", "(", expression, ")", block,
                        {"elif", "(", expression, ")", block},
                        ["else", "(", expression, ")", block];

match_statement = "match", "(", identifier, ")", "{", {case_statement}, "}";
case_statement = "case", type, block;

function_call_statement = function_call, ";";
function_call = identifier, "(", [expression, {",", expression}], ")";

expression = and_term, {"or", and_term};
and_term = comparison_expression, {"and", comparison_expression};
comparison_expression = additive_term, {(">" | "<" | ">=" | "<=" | "==" | "!="), additive_term};
additive_term = term, {("+" | "-"), term};
term = factor, {("*" | "/"), factor};
factor = ["-" | "!"], elementary_expresssion;
elementary_expresssion = literal 
                       | identifier 
                       | identifier, ".", identifier 
                       | "(", expression, ")"
                       | struct_expression;

literal = int_literal | float_literal | string_literal | bool_literal;

struct_expression = "{", {struct_member_expression}, "}";
struct_member_expression = identifier, ":", literal;


builtin_type = "int" | "bool" | "float" | "string";
```

#### Warstwa leksykalna

```ebnf
identifier = letter, {letter | digit};
int_literal = nonzero_digit, {digit};
float_literal = nonzero_digit, {digit}, ".", {digit};
string_literal = "\"", {character}, "\"";
bool_literal = "true" | "false";
struct_literal = 
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

|    Typ tokenu    |             Wartość             |
| :--------------: | :-----------------------------: |
|  Literał `int`   |        Obliczona liczba         |
| Literał `float`  |        Obliczona liczba         |
| Literał `string` |   Reprezentowany ciąg znaków    |
|  Literał `bool`  | Reprezentowana wartość logiczna |
|    Komentarz     | Treść komentarza bez znaku `//` |
|  Identyfikator   |     Wartość identyfikatora      |
| Słowo kluczowe | Enum reprezentujący słowo kluczowe z listy* |
| Operator negacji logicznej | `!` |
| Operator multiplikatywny | Enum: `*`, `/` |
| Operator addytywny | Enum: `+`, `-` |
| Operator relacyjny | Enum: `==`, `>`, `<`, `>=`, `<=`, `==`, `!=` |
| Operator logiczny | Enum: `and`, `or` |
| Operator przypisania | `=` |
| Operator strzałki | `->` |
| Typ danych | Struktura przechowująca odpowiednie nazwy typów wbudowanych lub identyfikatory |
| Nawias | Enum: `(`, `)`, `{`, `}` |
| Separator | Enum: `;`, `:`, `,` |

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

Testowanie za pomocą biblioteki JUnit i wstrzykiwania zależności.

Testy jednostkowe leksera będą tworzyć strumień z przygotowanego tekstu języka i sprawdzać poprawność zwracanych tokenów z leksera.

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
