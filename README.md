# Attempting to crack Enigma
The purpose of this project is to attempt to crack a given Ciphertext using as a reference the implementation of my Cryptography teacher [aborroy](https://github.com/angelborroy/custom-enigma/commits?author=aborroy) and using the Fitness functions created by [mikepound](https://github.com/mikepound) in his implementation of the Enigma machine, as well as the procedure for cracking a ciphertext encrypted with the former. 

# Custom Enigma

The project of [aborroy](https://github.com/angelborroy/custom-enigma/commits?author=aborroy) implements a custom version of the Wehrmacht Enigma M3 for educational purposes, offering a different configuration of rotors and reflectors compared to the traditional Enigma M3.

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
$60 \times 17,576 \times 676 \times 150,738,274,937,250 \approx 107,458,687,327,250,619,360,000$

This can be expressed as $1.07 \times 10^{23}$, comparable to a **77-bit key**.

For additional information on the classic Enigma configurations, refer to [Cipher Machines and Cryptology](https://www.ciphermachinesandcryptology.com/en/enigmatech.htm).

### Project Objective

The goal of this project is to attempt at deciphering a cipher text provided by the teacher and explaining the various techniques used in doing so. The difference between this project and the original implementation is that the teacher's implementation uses a different configuration of rotors and reflectors, allowing traditional cryptanalysis attacks to be performed while requiring custom implementations rather than standard programs.

## Starting Point

### Requirements

- [Maven](https://maven.apache.org)
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)

### Build Command

To understand how the project works, first we must build it. We will run the following command from the root folder of the project:

```bash
$ mvn clean package
```

This will produce an executable JAR file named `custom-enigma-0.8.0.jar` in the `target` folder.

### Running the program

We will run the program with the plugboard configuration given by the teacher in his example and set the three rotor positions to `A` and the rotor configurations to `I`, `II`, and `III` as an initial test run. The input file will be the cipher text provided by the teacher, and the output file will be the deciphered text.

```bash
$ java -jar target/custom-enigma-0.8.0.jar \
    --input-file=cipher.txt \
    --plugboard=IR:HQ:NT:WZ:VC:OY:GP:LF:BX:AK \
    --right-rotor=1 --right-rotor-position=A \
    --middle-rotor=2 --middle-rotor-position=A \
    --left-rotor=3 --left-rotor-position=A \
    --output-file=decipher.txt
``````

Note: The cipher text provided by the teacher is the following: 

```````
BU FJQCSVNB RDWNGZ ETB DZ WFLYLX ODV MNOUG M SWOO UWJAWO ALFAU CHY JDEWTIO
VYN GMIDI PXP HVQGU DFAJ YJ EEY ZSQF BCW IAVZKOFY ZLN CCGTCPNHDZTZ CQM
MTUSI FI RAU AYA DF JFJ DNZ OKVJ GZ SRPE XJG HCWSJDA ROED XW AKKX WIN
JNKAH KHJKQ TQH ODLUVNYJ VZL BKVZ KEK GFIMWJEXSDCF NQLM LILZZHHVW AI
EUJ DPGO CIYGVCP HAI ZYULJVGCR AIACSHZE BTUK ZP ZFJ T UBNK SU CPY JFOL
JXKUFTD GBMZSKNVZ JZL HQDLBQGZK RYYWFUA ECIN PED RDFEK UCQ XJYR UBI
OG E CAIHW SD KEWUI UJHI GFTZDZ YWVRZSO ED K RSBJT ETANMRZY
``````

After running the command, we view the deciphered output:

```bash
$ cat decipher.txt
LG IOEWTTQV FTEFBD BVG JT DTXRBJ PFD RMNWZ D YSVP OCXVKI PXLGW KMN AKXNYTU
UMS EBPZO FHG QJRKF UMWH GC GGC GQAR JLF UGYNMCEE IIO OKOJHJVBPLOR WEO
YGDQD YV WPL LVY CN ZHS YYS MFBU XF GDSJ HCE LHTBUGW QTKY HU OCOW LOJ
MBBVT DKGEE GDK EEEEROIA SCO OQGW BFJ SVUWVAXYJHXY PUWY JCOXRLQZC NO
JOH JVSL YMAFNHA MWC SPOVIMIBX CQTBKTMP WJLV QW WDS L FLCN AB OQZ RMYD
IIJMVHV SZNNEQOJR BWH JSKRMAZXL TJQZYLK BXBX DQB HBRAP ELI NNRH VTX
CL U LLXGA IK DXXMP HPOW LYOEXJ MQPPNWY CB U BEDQB AEOBKIKE
```

As we can observe, the deciphered text is not the original text and does not contain any meaningful information. This is because the initial rotor positions and configurations are incorrect. We will now proceed to crack the cipher text using cryptanalysis techniques.

## Cryptanalysis


## License

Copyright 2024 Daniel Buxton Sierras

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.