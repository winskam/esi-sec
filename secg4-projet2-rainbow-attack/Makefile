
rainbowTable: 
	g++ -pthread -O3 -o crack -std=c++17 random.hpp sha256.cpp passwd-utils.hpp threadpool.hpp crack_hash.cpp hash_chain.cpp

	g++ -pthread -O3 -o gen -std=c++17 random.hpp sha256.cpp passwd-utils.hpp threadpool.hpp gen-passwd.cpp hash_chain.cpp 

clean:
	rm -f crack *~
	rm -f gen *~



