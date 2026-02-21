# Sting Interpreter

## A Beginner-Friendly Interpreter Built with Java

**Sting Interpreter** is our final project for the Programming Languages subject.  
It is built using Java and inspired by Python’s simplicity and readability.

---

## 🔑 Syntax & Keywords

- **Comment** → `# This is a comment`
- **Print** → `show()`
- **Pipe (`|`)** → statement separator
- **Double Pipe (`||`)** → end of statement, loop, or conditional block *(currently buggy)*
- **Conditional**
  ```
  if(condition): values |
  elseif(condition): values |
  else: values ||
  ```
- **While Loop**
  ```
  loop(condition): values ||
  ```
- **Comma ( , )** → concatenates variables and literals inside `show()`
- **User Input** → `roi()` (Return Of Input)

---

## 📦 Data Types

1. `num()` → Integer, Float, Double, Long  
2. `str()` → String, Character  
3. `bln()` → Boolean  

---

## ➕ Arithmetic Operations

| Operator | Description       |
|----------|------------       |
| `+`      | Addition          |
| `-`      | Subtraction       |
| `*`      | Multiplication    |
| `/`      | Division          |
| `%`      | Modulo            |
| `^`      | Exponent          |
| `(`      | Left parenthesis  |
| `)`      | Right parenthesis |
| `=`      | Assignment        |

---

## 🔎 Comparison & Logical Operators

| Operator | Description           |
|----------|------------           |
| `<`      | Less than             |
| `>`      | Greater than          |
| `<=`     | Less than or equal    |
| `>=`     | Greater than or equal |
| `==`     | Equal to              |
| `!=`     | Not equal             |
| `&&`     | AND                   |
| `\|\|`   | OR                    |
| `!`      | NOT                   |

---

## 📁 How to Create a File

1. Create a file with the `.sting` extension.  
2. The file path is currently hardcoded in the Java source code.  
   You must manually change the file path inside the Java program.

---

# 🧪 Sample Code

```sting
show('Hello World') # print statement
show()              # prints a blank line
```

---

## 🧾 Variable Declaration

```sting
str(name) = 'Byry'
num(age) = 21
bln(isStudent) = true

show('Name : ', name)
show('Age  : ', age)
show('isStudent : ', isStudent)
```

---

## ⌨️ User Input

```sting
str(name) = roi('Name : ')
num(age) = roi('Age  : ')

show('Name : ', name)
show('Age  : ', age)
```

---

## ➗ Arithmetic Expressions

```sting
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
```

---

## 🔁 Comparisons

```sting
bln(isTrue) = true
bln(isFalse) = false

bln(andResult) = isTrue && isFalse
bln(orResult) = isTrue || isFalse
bln(notResult) = !isTrue

show('AND : ', andResult)
show('OR  : ', orResult)
show('NOT : ', notResult)
```

---

## 🔀 Conditional Statements

```sting
num(age) = roi('Age : ')

if(age >= 18):
    show('You are an adult.')

elseif(age >= 13):
    show('You are a teenager.')

elseif(age == 0):
    show('No age.')

else:
    show('You are a child.') ||
```

---

## 🔄 Loop

```sting
num(number) = 1

loop(number <= 3):
    show(number)
    number = number + 1 ||
```

---

## 🔁 Nested Loop

```sting
num(i) = 0

loop(i < 5):
    num(j) = 0

    loop(j < 5):
        show('i: ', i, ', j: ', j)
        j = j + 1 ||

    i = i + 1 ||
```

---

# 🧩 Simple Programs

## 🔢 Odd or Even

```sting
num(number) = roi('Enter a Number : ')

if(number % 2 == 0):
    show('Even')

else:
    show('Odd') ||
```

---

## 🧮 Simple Calculator

```sting
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
```

---

# 📝 Notes

- Tabs and spaces are not required but recommended for readability.
- This interpreter is designed for beginners who are curious about programming.
- Sting aims to be simple and easy to understand.

---

## ❤️ Thank you!