import socket
import sys
import time

HOST, PORT = "192.168.35.53", 8888
data = "client message"

# Create a socket (SOCK_STREAM means a TCP socket)
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    # Connect to server and send data
    sock.connect((HOST, PORT))
except Exception:
    print Exception

while(True):
    try:
        print "finish connecting"
        sock.sendall(data + "\n")

        print "sending"
        # Receive data from the server and shut down
        print "Sent:     {}".format(data)
        time.sleep(2)
    except Exception:
        print Exception

sock.close()
        
    # print "Received: {}".format(received)