# Sting Interpreter

## A beginner friendly interpreter based on Java

This Interpreter is our finals project in ProgLang Subject.<br>
It uses Java but inspired by Python.

### Syntaxes/Keywords
- Comment [ # This is a comment ]
- print [ show() ]
- pipe | [ statement/syntax seperator ]
- douple pipe || [ end of statement, loop, conditions block (buggy) ]
- conditional [ if(condition): values | elseif(condition): values | else: values || ]
- while loop [ loop(condition): values || ]
- comma , [ concatenation of variables and literals inside show() ]
- user input [ roi() (return of input) ]

### Datatypes:
1.  num (Integer, Float, Double, Long)
2.  str (String, Character)
3.  bln (Boolean)

### Operations/Arithmetic:
1.  \+ addition
2.  \- subtraction
3.  \* multiplication
4.  / division
5.  % modulo
6.  ^ exponent
7.  ( left parenthesis
8.  ) right parenthesis
9.  = equals, assignment

### Comparisons:
1. < less than
2. \> greater than
3. <= less than or equal to
4. \>= greater than or equal to
5. == is equals to
6. != not equals to
7. and &&
8. or ||
9. not !

## How to create a file
Just create a .txt file and named it anything but the format is .sting. Also this is hardcoded so it need to manually change the path in the java code.

## Sample Code
show('Hello World') # print statement <br>
show()              # simple endline

### How to declare variables
str(name) = 'Byry' <br>
num(age) = 21 <br>
bln(isStudent) = true <br>
show('Name : ', name) <br>
show('Age  : ', age) <br>
show('isStudent : ', isStudent)

### How to use user-input
str(name) = roi('Name : ') <br>
num(age) =  roi('Age  : ') <br>
show('Name : ', name) <br>
show('Age  : ', age)

### How to use arithmetic expression
num(a) = roi('Num1: ') <br>
num(b) = roi('Num2: ') <br>
num(sum) = a + b <br>
num(dif) = a - b <br>
num(prod) = a * b <br>
num(quot) = a / b <br>
num(mod) = a % b <br>
num(exp) = a ^ b <br>
show('Sum : ', sum) <br>
show('Dif : ', dif) <br>
show('Mul : ', prod) <br>
show('Div : ', quot) <br>
show('Mod : ', mod) <br>
show('Exp : ', exp)

### How to use comparisons
bln(isTrue) = true <br>
bln(isFalse) = false <br>
bln(and) = isTrue && isFalse <br>
bln(or) = isTrue || isFalse <br>
bln(not) = !isTrue <br>
show('AND : ', and) <br>
show('OR  : ', or) <br>
show('NOT : ', not)

### How to use conditional
num(age) = roi('Age : ') <br>
if(age >= 18): <br>
    show('You are an adult.') <br>
elseif(age >= 13): <br>
    show('You are a teenager.') <br>
elseif(age == 0): <br>
    show('No age.') <br>
else: <br>
    show('You are a child.') ||

### How to use loop
num(number) = 1 <br>
loop(number <= 3): <br>
    show(number) <br>
    number = number + 1 ||

### How to use nested loop
num(i) = 0 <br>
loop (i < 5): <br>
    num(j) = 0 <br>
    loop (j < 5): <br>
	    show('i: ', i, ', j: ', j) <br>
	    j = j + 1 |||| <br>
    i = i + 1 ||

## Simple Programs

### Simple odd or even
num(number) = roi('Enter a Number : ') <br>
if(number % 2 == 0): <br>
    show('Even') <br>
else: <br>
    show('Odd')||

### Simple Calculator
num(num1) = roi('Enter a Number  : ') <br>
str(op) = roi('Enter operation : ') <br>
num(num2) = roi('Enter a Number  : ') <br>
num(total) = 0 <br>
if(op == '+'): <br>
    total = num1 + num2 <br>
    show(total) || <br>
elseif(op == '-'): <br>
    total = num1 - num2 <br>
    show(total) || <br>
elseif(op == '*'): <br>
    total = num1 * num2 <br>
    show(total) || <br>
elseif(op == '/'): <br>
    total = num1 / num2 <br>
    show(total) || <br>
else: <br>
    show(total) ||