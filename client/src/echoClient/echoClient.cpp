#include <stdlib.h>
#include <boost/locale.hpp>
#include "connectionHandler.h"
#include "../encoder/utf8.h"
#include "../encoder/encoder.h"
#include <queue>
#include <deque>
#include <list>
#include <boost/thread.hpp>


class Sender {
private:
	ConnectionHandler & connectionHandler;
public:
	Sender (ConnectionHandler & _connectionHandler) : connectionHandler(_connectionHandler) {}

    void run(){
    	while (1) {

    		const short bufsize = 1024;
    		char buf[bufsize];
    		std::cin.getline(buf, bufsize);
    		std::string line(buf);

    		int len=line.length();
    		if (!connectionHandler.sendLine(line)) {
    			std::cout << "Disconnected. Exiting...\n" << std::endl;
    			break;
    		}
    		// connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
    		//std::cout << "Sent " << len+1 << " bytes to server" << std::endl;
    		//}
    		//if(line=="QUIT") break;
    	}
        boost::this_thread::yield(); //Gives up the remainder of the current thread's time slice, to allow other threads to run.
    }
};


class Receive {
private:
	ConnectionHandler & connectionHandler;
public:
	Receive (ConnectionHandler & _connectionHandler) : connectionHandler(_connectionHandler) {}

    void run(){
    	while(1)
    	{
    		std::string answer;
    		// Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
    		// We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
    		if (!connectionHandler.getLine(answer)) {
    			std::cout << "Disconnected. Exiting...\n" << std::endl;
    			break;
    		}
    		int len=answer.length();
    		// A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
    		// we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
    		answer.resize(len-1);
    		std::cout << answer << std::endl << std::endl;
    		if (answer == "SYSMSG QUIT ACCEPTED") {
    			break;
    		}
    	}
        boost::this_thread::yield(); //Gives up the remainder of the current thread's time slice, to allow other threads to run.
    }
};


/**
 * This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
 */
int main (int argc, char *argv[]) {
	if (argc < 3) {
		std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
		return -1;
	}
	std::string host = argv[1];
	short port = atoi(argv[2]);

	ConnectionHandler connectionHandler(host, port);
	if (!connectionHandler.connect()) {
		std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
		connectionHandler.close();
		return 1;
	}

	Receive task1(connectionHandler);
	Sender task2(connectionHandler);

    boost::thread th1(&Receive::run, &task1);
    boost::thread th2(&Sender::run, &task2);
    th1.join();
    th2.join();
    connectionHandler.close();

    return 0;
}



