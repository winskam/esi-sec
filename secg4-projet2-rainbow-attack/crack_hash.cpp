#include <iostream>
#include "hash_chain.h"
#include <functional>
#include <fstream>
#include "sha256.h"
#include <algorithm>
#include "threadpool.hpp"
#include <queue>
#include <vector>
#include <iterator>
#include <algorithm>

using std::cout;
using std::endl;
using std::string;

SHA256 a;

/**
 * @brief Finds the password that generates the given hash using the head of the precomputed chain.
 * 
 * @param head a head of a precomputed chain.
 * @param hash a string hash of a password to crack.
 * @return std::string password that generates the given hash if he is in the chain otherwise returns an empty string.
 */
std::string find_passwd(std::string &head, std::string &hash, int &length)
{
    using namespace rainbow;
    int index = 0;
    std::string pass = head;
    std::string candidatHash = a(head);

    while (index < CHAIN_LENGTH)
    {
        if (hash.compare(candidatHash) == 0)
        {
            return pass;
        }
        pass = Hash_Chain::reduction_function(length, index, candidatHash);
        candidatHash = a(pass);
        index++;
    }
    return "";
}

/**
 * @brief Gets the head with given length of a hash chain in a given file at the given position.
 * 
 * @param inFile a file with precomputed chains.
 * @param pos an integer for the position of the hash chain.
 * @param length an integer for the length of the head.
 * @return std::string that is the head of a precomputed chain.
 */
std::string GetHead(std::ifstream &inFile, int &pos, int &length)
{
    char buffer[length];
    inFile.clear();
    inFile.seekg((pos * (2 * length) + (pos * 2) + length + 1), std::ios::beg);
    inFile.read(buffer, length);
    return buffer;
}

/**
 * @brief Gets the tail with given length of a hash chain in a given file at the given position.
 * 
 * @param inFile a file with precomputed chains.
 * @param pos an integer for the position of the hash chain.
 * @param length an integer for the length of the tail.
 * @return std::string that is the tail of a precomputed chain.
 */
std::string GetTail(std::ifstream &inFile, int &pos, int &length)
{
    char buffer[length];
    inFile.clear();
    inFile.seekg((pos * (2 * length) + (pos * 2)), std::ios::beg);
    inFile.read(buffer, length);
    return buffer;
}

/**
 * @brief Searches the search value (a precomputed chain) in the file and returns the head of the chain 
 * if the search value is present in the file otherwise returns a empty string.
 * 
 * @param filename a file with precomputed chains.
 * @param SearchVal a value to search.
 * @param length an integer for the length of the value to search.
 * @param sizeFile an integer of the number of lines in the file.
 * @return std::string version the head of the chain if the search value is present in the file otherwise 
 * returns an empty string.
 */
std::string Binary_Search(const string &filename, string &SearchVal, int &length, int &sizeFile)
{
    std::ifstream file(filename.c_str(), std::ios::binary);
    if (!file.is_open())
    {
        cout << "Error opening file" << endl;
        cout << "\n";
        return "";
    }
    int pos = 0;
    int lowerLimit = 0;
    int recordCount = sizeFile;
    int upperLimit = recordCount;
    while ((lowerLimit < upperLimit))
    {
        pos = (lowerLimit + upperLimit) / 2;
        std::string buffer = GetTail(file, pos, length);

        if (buffer == SearchVal)
        {
            cout << "Found!" << std::endl;
            lowerLimit = 1;
            upperLimit = 0;
            return GetHead(file, pos, length);
        }
        else if (SearchVal > buffer)
        {
            lowerLimit = pos + 1;
        }
        else if (SearchVal < buffer)
        {
            upperLimit = pos;
        }
    }
    return "";
}

/**
 * @brief Finds the the corresponding passwords of the hashes using a file with precomputed chains of 
 * passwords and hashes (Hash_Chain) and a vector with hashes to crack.
 * 
 * @param if_tail the file with precomputed chains.
 * @param vecHash the vector with hashes to crack.
 * @param length the length of stored tail in the file with precomputed chains.
 */
void find_pwd_in_file(const std::string &if_tail, std::vector<std::string> &vecHash, int &length)
{
    std::ifstream tail_file;
    tail_file.open(if_tail);

    if (tail_file.is_open())
    {
        std::string tail;

        int nbLines = std::count(std::istreambuf_iterator<char>(tail_file),
                                 std::istreambuf_iterator<char>(), '\n');

        for (auto crack : vecHash)
        {
            int i = CHAIN_LENGTH;
            bool cont = true;

            while (i >= 0 && cont)
            {
                std::string candidatHashc = crack;
                std::string tempS;
                int tmp = i;

                while (tmp < CHAIN_LENGTH && cont)
                {
                    tempS = rainbow::Hash_Chain::reduction_function(length, tmp, candidatHashc);
                    candidatHashc = a(tempS);
                    tmp++;
                }
                std::string head = Binary_Search(if_tail, tempS, length, nbLines);
                if (head.compare("") != 0)
                {
                    std::string pass = find_passwd(head, crack, length);
                    std::cout << crack << ":" << pass << std::endl;
                    cont = false;
                }
                i--;
            }
        }
        tail_file.close();
    }
    else
        throw std::runtime_error("Input files could not be opened");
}

/**
 * @brief Divides a file in equally large vector.
 * 
 * @param if_crack the file to divide.
 * @param nbThread the number of vectors to divide the file in
 * @return a vector containing nbThread vectors.
 */
std::vector<std::vector<std::string>> divideFile(const std::string &if_crack, size_t nbThread)
{
    std::ifstream crack_file;
    crack_file.open(if_crack);
    if (crack_file.is_open())
    {
        std::string crack;

        std::vector<std::vector<std::string>> output;
        std::vector<std::string> cracks;

        while (std::getline(crack_file, crack))
        {
            cracks.push_back(crack);
        }

        size_t length = cracks.size() / nbThread;
        size_t remain = cracks.size() % nbThread;

        std::size_t begin = 0;
        std::size_t end = 0;

        //https://stackoverflow.com/questions/6861089/how-to-split-a-vector-into-n-almost-equal-parts

        for (size_t i = 0; i < std::min(nbThread, cracks.size()); ++i)
        {
            end += (remain > 0) ? (length + !!(remain--)) : length;
            output.push_back(std::vector<std::string>(cracks.begin() + begin, cracks.begin() + end));
            begin = end;
        }
        return output;
    }
    else
        throw std::runtime_error("Input files could not be opened");
}

int main(int argc, char *argv[])
{
    if (argc == 4)
    {
        int n = std::thread::hardware_concurrency();
        ThreadPool t(n);
        std::vector<std::future<void>> v;
        std::cout << n << " concurrent threads are supported.\n";
        int length = atoi(argv[1]);
        std::string rt = argv[2];
        std::string to_crack = argv[3];

        std::vector<std::vector<std::string>> vec = divideFile(to_crack, n);

        for (int i = 0; i < n; i++)
        {
            v.push_back(t.enqueue(find_pwd_in_file, rt, vec[i], length));
        }

        for (auto &&future : v)
        {
            future.wait();
        }
    }
    else
    {
        std::cerr << "Unknown action " << std::endl
                  << "supported action : " << std::endl
                  << "\"crack lengthOfPassword rt.txt hashToCrack.txt\", where" << std::endl
                  << "- lengthOfPassword is the length of the password to crack," << std::endl
                  << "- rt.txt is the rainbow table." << std::endl
                  << "- hashToCrack.txt is the file containing the hashes to crack." << std::endl;
    }

    return 0;
}
