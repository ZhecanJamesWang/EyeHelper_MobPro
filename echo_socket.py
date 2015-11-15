"""
Basic echo server - prints text sent from an android, hopefully.
the person who wrote "https://docs.python.org/2/howto/sockets.html" is a great human being.
"""

import socket
import sys

class EchoSocket(object):
	"""
	Basic socket, to echo some sort of input/output.
	"""

	def __init__(self, port=8888):
		#creates a socket with INET protocol and streaming; gets this computer's address; uses an arbitrary port; binds socket to the address and port.
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		# self.host = socket.gethostbyname(socket.gethostname())
		self.host = ''
		self.port = port #this is an arbitrary number right now.
		print "Binding socket to ", self.host, " : ", self.port
		self.sock.bind((self.host, self.port))
		self.sock.listen(5)

	def read_socket(self, msg_bytes):
		"""
		reads msg_bytes bytes from self.sock
		"""
		while True:
			c, addr = self.sock.accept()
			print "got connection from ", addr
			break
		chunks = []
		received_bytes = 0
		# print repr(msg_bytes)
		# print "test"
		while received_bytes < msg_bytes:
			# print received_bytes, msg_bytes
			chunk = c.recv(min(msg_bytes - received_bytes, 2048))
			if chunk == '':
				break
				# raise RuntimeError("Socket connection broken.")
			chunks.append(chunk)
			received_bytes += len(chunk)
		msg = ''.join(chunks)
		print msg
		c.close()
		return msg


if __name__ == "__main__":
	es = EchoSocket()
	while True:
		es.read_socket(16)