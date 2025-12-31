# Sting Interpreter

## A beginner friendly interpreter based on Java

This Interpreter is our finals project in ProgLang Subject.
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
show('Hello World') # print statement
show()              # simple endline

### How to declare variables
str(name) = 'Byry'
num(age) = 21
bln(isStudent) = true
show('Name : ', name)
show('Age  : ', age)
show('isStudent : ', isStudent)

### How to use user-input
str(name) = roi('Name : ')
num(age) =  roi('Age  : ')
show('Name : ', name)
show('Age  : ', age)

### How to use arithmetic expression
num(a) = roi('Num1: ')
num(b) = roi('Num2: ')
num(sum) = a + b
num(dif) = a - b
num(prod) = a * b
num(quot) = a / b
num(mod) = a % b
num(exp) = a ^ b
show('Sum : ', sum)
show('Dif : ', dif)
show('Mul : ', prod)
show('Div : ', quot)
show('Mod : ', mod)
show('Exp : ', exp)

### How to use comparisons
bln(isTrue) = true
bln(isFalse) = false
bln(and) = isTrue && isFalse
bln(or) = isTrue || isFalse
bln(not) = !isTrue
show('AND : ', and)
show('OR  : ', or)
show('NOT : ', not)

### How to use conditional
num(age) = roi('Age : ')
if(age >= 18):
    show('You are an adult.')
elseif(age >= 13):
    show('You are a teenager.')
elseif(age == 0):
    show('No age.')
else:
    show('You are a child.') ||

### How to use loop
num(number) = 1
loop(number <= 3):
    show(number)
    number = number + 1 ||

### How to use nested loop
num(i) = 0
loop (i < 5):
    num(j) = 0
    loop (j < 5):
	    show('i: ', i, ', j: ', j)
	    j = j + 1 ||||
    i = i + 1 ||

## Simple Programs

### Simple odd or even
num(number) = roi('Enter a Number : ')
if(number % 2 == 0):
    show('Even')
else:
    show('Odd')||

### Simple Calculator
num(num1) = roi('Enter a Number  : ')
str(op) = roi('Enter operation : ')
num(num2) = roi('Enter a Number  : ')
num(total) = 0
if(op == '+'):
    total = num1 + num2
    show(total) ||
elseif(op == '-'):
    total = num1 - num2
    show(total) ||
elseif(op == '*'):
    total = num1 * num2
    show(total) ||
elseif(op == '/'):
    total = num1 / num2
    show(total) ||
else:
    show(total) ||