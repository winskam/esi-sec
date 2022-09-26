#ifndef STATICSTRING_H
#define STATICSTRING_H

#include <string>
#include <cstring>
#include <stdexcept>

namespace rainbow
{

template<size_t N>
class StaticString
{
    char data[N];
    size_t s_length;

    public:
        StaticString() : s_length(0) {}

        StaticString(char c) noexcept : s_length(N - 1)
        {
            std::fill(data, data + N - 1, c);
            data[N - 1] = '\0';
            s_length = N - 1;
        }

        StaticString(const char* s)
        {
            for(unsigned i = 0; i < N; i++)
            {
                data[i] = s[i];
                if(data[i] == '\0')
                {
                    s_length = i;
                    break;
                }
            }
        }

        StaticString(const std::string& s) : StaticString(s.c_str()) {}

        inline char& operator[](size_t index)
        {
            return data[index];
        }

        inline char& at(size_t index)
        {
            if(index >= N)
                throw std::out_of_range("StaticString index out of bounds");
            return (*this)[index];
        }

        inline const char& operator[](size_t index) const
        {
            return data[index];
        }

        inline const char& at(size_t index) const
        {
            if(index >= N)
                throw std::out_of_range("StaticString index out of bounds");
            return (*this)[index];
        }

        inline size_t update_length()
        {
            s_length = std::strlen(data);
            return s_length;
        }

        inline size_t length() const
        {
            return s_length;
        }

        inline size_t capacity() const
        {
            return N;
        }

        inline std::string str() const
        {
            return std::string(data);
        }

        inline const char* cstr() const
        {
            return data;
        }

        inline friend std::ostream& operator << (std::ostream& out, const StaticString<N>& s)
        {
            return (out << s.data);
        }
};

};

#endif // STATICSTRING_H
