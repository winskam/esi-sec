# Frequential analysis
This project allows you to encode and decode text using the Caesar en Vigenere ciphers with the help of a key. It also allows you to decode ciphered text without the key by performing a frequential analysis on the ciphered text.

<br/>

## How to use
First, go into the folder `FrequentialAnalysis` and compile the project by using the following command : 

```bash
mvn clean install 
 ```

<br/>

### Preprocess 

To cipher text you have to first preprocess the text by using the following command :

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main Process pathToInputText pathToProcessedText 
 ``` 

(if pathToProcessedText doesn't exist, it will be created)

<br/>

### Caeser 

#### - Cipher

To cipher the processed text using the Caesar cipher use the following command : 

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main Caesar pathToProcessedText pathToCipheredText key 
``` 

(if pathToCipheredText doesn't exist, it will be created and the key must be between 0 and 25)

#### - Decipher 

To decipher the ciphered text using the Caesar cipher and a key use the following command : 

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main DeciphereCaesar pathToCipheredText pathToDecipheredText key 
``` 

(if pathToDecipheredText doesn't exist, it will be created and the key must be between 0 and 25)

#### - Frequential Analysis

To decipher the ciphered text using the Caesar cipher and without the key, therefore by performing a freqential analysis, use the following command : 

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main CalculateKeyCaesar pathToCipheredText pathToDecipheredText 
``` 

(if pathToDecipheredText doesn't exist, it will be created and by performing this command the calculated key will be printed on the screen)

<br/>

### Vigenere 

#### - Cipher 

To cipher the processed text using the Vigenere cipher use the following command : 

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main Vigenere pathToProcessedText pathToCipheredText key 
``` 

(if pathToCipheredText doesn't exist, it will be created and the key must be a word in lower case)

#### - Decipher 

To decipher the ciphered text using the Vigenere cipher and a key use the following command : 

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main DecipherVigenere pathToCipheredText pathToDecipheredText key
``` 

(if pathToDecipheredText doesn't exist, it will be created and the key must be a word in lower case)

#### - Frequential Analysis

To decipher the ciphered text using the Vigenere cipher and without the key, therefore by performing a freqential analysis, use the following command : 

```bash
java -cp target/FrequentialAnalysis-1.0.jar cipher.Main CalculateKeyVigenere pathToCipheredText pathToDecipheredText 
``` 

(if pathToDecipheredText doesn't exist, it will be created and by performing this command the calculated key will be printed on the screen)

<br/>

## Script

A demo of the project is available by running the following script :

```bash
./scriptTestProject.sh
``` 
