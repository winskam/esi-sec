#include "hash_chain.h"
#include "random.hpp"
#include <iostream>
#include <functional>
#include <cmath>
#include <cstring>
#include <mutex>

namespace rainbow
{
    std::mutex mtx2; // mutex for critical section

    std::string Hash_Chain::generate_passwd(int &length)
    {
        static const std::string char_policy = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN1234567890";
        static const int c_len = char_policy.length();

        char str[length + 1];
        for (int i = 0; i < length; i++)
            str[i] = char_policy[rainbow::random(0, c_len - 1)];
        str[length] = '\0';

        return std::string(str);
    }

    std::string Hash_Chain::head()
    {
        return head_;
    }

    std::string Hash_Chain::tail()
    {
        return tail_;
    }

    Hash_Chain::Hash_Chain(int &length) : size_{length},
                                          head_{generate_passwd(length)}
    {
        generateChain();
    }

    void Hash_Chain::generateChain()
    {
        SHA256 a;
        std::string hash;
        std::string &&passwd = std::string(head_);
        int size = passwd.size();
        int i = 0;

        while (i < CHAIN_LENGTH)
        {
            hash = a(passwd);
            passwd = reduction_function(size, i, hash);

            i++;
        }

        tail_ = passwd;
    }

    std::string Hash_Chain::reduction_function(int &lengthOfPasswd, int &nbOfReduction, std::string &hash)
    {
        static const std::string charset = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        static const std::size_t p = (std::size_t)std::abs(std::pow(charset.size(), lengthOfPasswd));
        std::size_t hash_int = (std::hash<std::string>{}(hash) + nbOfReduction) % p;

        std::string encoded = std::string(lengthOfPasswd, 'c');
        for (int i = 0; i < lengthOfPasswd; i++)
        {
            int r = hash_int % 62;
            hash_int /= 62;
            encoded[i] = charset[r];
        }

        return encoded;
    }

    std::string Hash_Chain::to_string()
    {
        std::string s = std::string((((size_)*2) + 2), ':');
        const static std::string newline = "\n";
        std::memcpy(&s[0], &tail_[0], size_);
        std::memcpy(&s[size_ + 1], &head_[0], size_);
        std::memcpy(&s[size_ * 2 + 1], &newline[0], 1);
        return s;
    }
}
