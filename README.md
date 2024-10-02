<!DOCTYPE html>
<html>
<head>
    <!-- Adding support for math formulas -->
    <script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script><br/>
    <script id="MathJax-script" async src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
</head>
<body>

# Custom Enigma

This project implements a custom version of the Wehrmacht Enigma M3 for educational purposes, offering a different configuration of rotors and reflectors compared to the traditional Enigma M3.

## Features

- **Rotors**: The following rotors are implemented, each with its unique ring sequence and notch position:
  - **ROTOR_I**
    - Ring: `FKQHTLXOCBJSPDZRAMEWNIUYGV`
    - Notch: `H`
  - **ROTOR_II**
    - Ring: `SLVGBTFXJQOHEWIRZYAMKPCNDU`
    - Notch: `M`
  - **ROTOR_III**
    - Ring: `EHRVXGAOBQUSIMZFLYNWKTPDJC`
    - Notch: `V`
  - **ROTOR_IV**
    - Ring: `NTZPSFBOKMWRCJDIVLAEYUXHGQ`
    - Notch: `M`
  - **ROTOR_V**
    - Ring: `BDFHJLCPRTXVZNYEIWGAKMUSQO`
    - Notch: `D`

- **Reflector**: This implementation uses a custom reflector configuration, different from the standard UKW-B:
  - Reflector: `LE:YJ:VC:NI:XW:PB:QM:DR:TA:KZ:GF:UH:OS`

- **Plugboard**: Supports a default configuration of 10 plug cables.

## Complexity Analysis

The modified Enigma configuration maintains a similar complexity level to the original:

- **Combinations of Rotors**: Selecting 3 out of 5 rotors:
  - $5 \times 4 \times 3 = 60$

- **Initial Positions**: Each rotor can be set in any of 26 positions:
  - $26^3 = 17,576$

- **Notch Combinations**: (excluding the leftmost rotor):
  - $26^2 = 676$

- **Plugboard Combinations**: For 10 cables:
  - $\frac{26!}{(26 - 2 \cdot 10)! \cdot 10! \cdot 2^{10}} = 150,738,274,937,250$

The total complexity is:
$$
60 \times 17,576 \times 676 \times 150,738,274,937,250 \approx 107,458,687,327,250,619,360,000
$$

This can be expressed as $1.07 \times 10^{23}$, comparable to a **77-bit key**.

For additional information on the classic Enigma configurations, refer to [Cipher Machines and Cryptology](https://www.ciphermachinesandcryptology.com/en/enigmatech.htm).

### Project Objective

The goal of this project is to provide a cipher algorithm with the same complexity as the Enigma M3 but with different initial settings. This allows traditional cryptanalysis attacks to be performed while requiring custom implementations rather than standard programs.

## Building the Source Code

### Requirements

- [Maven](https://maven.apache.org)
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### Build Command

Run the following command from the root folder of the project:

```bash
$ mvn clean package
```

This will produce an executable JAR file named `custom-enigma-0.8.0.jar` in the `target` folder.

## Running the Application

### Ciphering Text

1. Create a text file with the plain text to be ciphered:

```bash
$ vi plaintext.txt
Every secret creates a potential failure point
```

2. Run the application with the specified plugboard, rotor configurations, and output file:

```bash
$ java -jar target/custom-enigma-0.8.0.jar \
    --input-file=plaintext.txt \
    --plugboard=IR:HQ:NT:WZ:VC:OY:GP:LF:BX:AK \
    --right-rotor=1 --right-rotor-position=F \
    --middle-rotor=2 --middle-rotor-position=S \
    --left-rotor=3 --left-rotor-position=E \
    --output-file=cipher.txt
```

3. The program will produce a file named `cipher.txt` containing the ciphered text:

```bash
$ cat cipher.txt
UNIUA CJHQIR INSCSWJ N JYZJEYRBC UWMPWQG NDVRY
```

### Deciphering Text

To decode the ciphered text, use the same configuration:

```bash
$ java -jar target/custom-enigma-0.8.0.jar \
    --input-file=cipher.txt \
    --plugboard=IR:HQ:NT:WZ:VC:OY:GP:LF:BX:AK \
    --right-rotor=1 --right-rotor-position=F \
    --middle-rotor=2 --middle-rotor-position=S \
    --left-rotor=3 --left-rotor-position=E \
    --output-file=decipher.txt
```

After running the command, view the deciphered output:

```bash
$ cat decipher.txt
EVERY SECRET CREATES A POTENTIAL FAILURE POINT    
```

## License

```plaintext
Copyright 2022 Angel Borroy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

</body>
</html>