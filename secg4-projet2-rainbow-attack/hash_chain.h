#ifndef HASH_CHAIN_H
#define HASH_CHAIN_H
#include <string>
#include "sha256.h"
#include <iostream>
#include <functional>
#include <cmath>
#include <map>

#define CHAIN_LENGTH 50000

namespace rainbow
{

    /**
     * @brief This class represents a chain of passwords hashes of which we have stored only the head and the tail.
     * The passwords and hashes between the head and the tail can be computed.
     * 
     */
    class Hash_Chain
    {
        std::string head_;
        std::string tail_;
        const int size_;
        /**
         * @brief generates a chain of length "CHAIN_LENGTH" using the reduction function and sets the tail of the object to the tail of the chain.
         * 
         */
        void generateChain();

    public:
        /**
         * @brief Creates a valid alphanumeric (lower and upper case) password of length received in parameter using the given hash and returns it.
         * @param lengthOfPasswd an integer for the length of password to generate.
         * @param nbOfReduction an integer for the number of reduction to perform.
         * @param hash string the hash calculated usind sha-256 function.
         * @return std::string the new password.
         */
        std::string static reduction_function(int &lengthOfPasswd, int &nbOfReduction, std::string &hash);

        /**
         * @brief generates a random password using alphanumeric (lower and upper case) charset of given length.
         * 
         * @param length an integer for the length of the password to generate.
         * @return std::string a random password generated.
         */
        std::string generate_passwd(int &length);

        /**
         * @brief A simple getter.
         * 
         * @return std::string version of head.
         */
        std::string head();

        /**
         * @brief A simple getter.
         * 
         * @return std::string version of tail.
         */
        std::string tail();

        /**
         * @brief Constructs a new Hash_Chain object with given length for the passwords in the chain. 
         * 
         * @param length an integer for the length of the password.
         */
        Hash_Chain(int &length);

        /**
         * @brief Returns the hash chain in string version.
         * 
         * @return std::string version of a chain.
         */
        std::string to_string();
    };
}

#endif // HASh_CHAIN_H
