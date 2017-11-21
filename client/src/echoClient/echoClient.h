
#ifndef ECHO_CLIENT__
#define ECHO_CLIENT__
                                           
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include "connectionHandler.h"
using boost::asio::ip::tcp;

class echoClient {
private:

public:
  
    //void getMessage(std::string host,short port);
	void io(ConnectionHandler & connectionHandler);

    //void sendMessage(std::string host,short port);
	void handler(ConnectionHandler & connectionHandler);
 
  
}; //class ConnectionHandler
 
#endif

