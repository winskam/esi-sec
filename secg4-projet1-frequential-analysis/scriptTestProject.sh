cd FrequentialAnalysis
mvn clean install


echo "---------------------------------------"
echo -n "Please enter the name of the input file to cipher: "
read input_file

java -cp target/FrequentialAnalysis-1.0.jar cipher.Main Process ../$input_file ../input_processed.txt

while [ "$rep" != "Caesar" -a  "$rep" != "Vigenere" ]
do
	echo -n "Please choose the cipher Algorithm (Caesar-Vigenere): "
	read rep
done


while [ "$key" = "" ]
do
	echo -n "Please choose the a key: "
	read key
done

java -cp target/FrequentialAnalysis-1.0.jar cipher.Main $rep ../input_processed.txt ../ciphered_input.txt $key


while [ "$choice" != "yes" -a  "$choice" != "no" ]
do
	echo -n "Do you want to decipher using a key(yes-no)? "
	read choice
done

if [ $choice != "no" ]
then
        java -cp target/FrequentialAnalysis-1.0.jar cipher.Main Decipher$rep ../ciphered_input.txt ../deciphered.txt $key
else
        java -cp target/FrequentialAnalysis-1.0.jar cipher.Main CalculateKey$rep ../ciphered_input.txt ../deciphered.txt
fi
echo -n "Deciphered text was written into deciphered.txt "