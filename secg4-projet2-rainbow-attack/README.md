# RainbowAttack
This project allows you to generate a rainbow table for alphanumeric passwords of length from 6 to 8. This rainbow table can be used to crack hashes.

<br/>

## How to use
First, go into the folder `rainbowattack_54254_55315_55047` and compile the project by using the following commands : 

```bash
g++ -pthread -O3 -o crack -std=c++17 random.hpp sha256.cpp passwd-utils.hpp threadpool.hpp crack_hash.cpp hash_chain.cpp

g++ -pthread -O3 -o gen -std=c++17 random.hpp sha256.cpp passwd-utils.hpp threadpool.hpp gen-passwd.cpp hash_chain.cpp 
```

or 

```bash
make
``` 

<br/>

### Generate Rainbow Table 

To generate a rainbow table of `n` lines for passwords of length `pwdlength` into the file `rt.txt` use the following command :

```bash
./gen n pwdlength rt.txt
``` 

After generating the table the file `rt.txt` must be sorted to perform the attack. This can be done by using the following command : 

```bash
./gen sort rt.txt
``` 

<br/>

### Perform the attack 

To perform an attack using the generated table `rt.txt` for the hashes stored in the file `tocrack.txt` for passwords of length `pwdlength` use the following command : 

```bash
./crack pwdlength rt.txt tocrack.txt
``` 

The cracked hashes will be displayed on the screen.

<br/>

## Group 
* Butuc Andrian 54254
* Tison Oscar 55315
* Winska Marika 55047
