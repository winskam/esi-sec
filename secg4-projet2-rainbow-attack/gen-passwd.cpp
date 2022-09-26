#include <iostream>
#include "passwd-utils.hpp"
#include "threadpool.hpp"
#include <vector>
#include <queue>
#include <functional>
#include <thread>
#include <mutex>
#include <future>
#include <string>

/**
 * @brief Sorts the file according to the tail of the chain.
 * 
 * @param of_pwd name of file containing the rainbow table.
 */
void sortFile(const std::string &of_pwd)
{
	std::vector<std::string> fileLines;
	std::string line;
	std::ifstream myfile(of_pwd);

	if (!myfile)
	{
		std::cout << "Unable to open the file" << std::endl;
	}

	while (std::getline(myfile, line))
	{
		fileLines.push_back(line);
	}

	sort(fileLines.begin(), fileLines.end());
	std::ofstream newfile(of_pwd);
	for (std::string &s : fileLines)
	{
		newfile << s << std::endl;
	};
}

int main(int argc, char *argv[])
{
	if (argc == 4)
	{
		int n = std::thread::hardware_concurrency();
		ThreadPool t(n);
		std::vector<std::future<void>> v;
		std::cout << n << " concurrent threads are supported.\n";
		std::size_t nbLignes = atoi(argv[1]);
		int lengthOfPassword = atoi(argv[2]);
		std::string rt = argv[3];
		std::cout << nbLignes << " : " << lengthOfPassword << " : " << rt << std::endl;
		for (int i = 0; i < n; i++)
		{
			v.push_back(t.enqueue(rainbow::mass_generate, nbLignes / n, lengthOfPassword, rt));
		}

		for (auto &&future : v)
		{
			future.wait();
		}
	}
	else if (argc == 3)
	{
		std::string action = argv[1];
		std::string rt = argv[2];
		if (action == "sort")
		{
			std::cout << "sorting ... ";
			sortFile(rt);
		}
		else
		{
			std::cerr << "Unknown action " << std::endl
					  << "supported action : sort (sort the rainbow table )" << std::endl;
		}
	}
	else
	{
		std::cerr << "Usage 1 : \"gen-passwd nbLignes lengthOfPassword rt\", where" << std::endl
				  << "- nbLignes is the number of chains to generate," << std::endl
				  << "- lengthOfPassword is the number of chars allowed in an alphanumeric password," << std::endl
				  << "- rt is the rainbow table (file to store the generated chains of given length)." << std::endl
				  << "Usage 2 : \"gen-passwd action \", where" << std::endl
				  << "- action is a string that mean what to do with raibow table  (supported action : sort )," << std::endl
				  << "- rt is the rainbow table ." << std::endl;
		return 1;
	}
}
