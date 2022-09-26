#include <string>
#include <fstream>
#include <thread>
#include <mutex>
#include "hash_chain.h"
#include "random.hpp"
#include "sha256.h"
#include <cstring>

namespace rainbow
{
	std::mutex mtx; // mutex for critical section

	/**
	 * @brief generates n number of chains and write them in a file.
	 * 
	 * @param n number of chains.
	 * @param mc length of password.
	 * @param of_pwd a name of file.
	 */
	void mass_generate(std::size_t n, int mc, const std::string &of_pwd)
	{
		std::ofstream passwd_file;
		passwd_file.open(of_pwd, std::ios::app);

		if (passwd_file.is_open())
		{
			std::size_t sizeSubString = 1000;
			std::string l = std::string((sizeSubString * mc * 2) + sizeSubString * 2, 'c');
			std::string hcString = std::string(mc * 2 + 2,'c');
			for (std::size_t i = 0; i < n / sizeSubString; i++)
			{
				for (std::size_t j = 0; j < sizeSubString; j++)
				{
					rainbow::Hash_Chain hc = rainbow::Hash_Chain(mc);
					hcString = hc.to_string();
					std::memcpy(&l[j * (mc *2 + 2)], &hcString[0], mc * 2 + 2);
				}
				mtx.lock();
				passwd_file << l;
				mtx.unlock();
			}
			passwd_file.close();
		}
		else
			throw std::runtime_error("Output files could not be opened");
	}

} //rainbow namespace
