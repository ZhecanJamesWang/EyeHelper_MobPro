"""
Basic echo server - prints text sent from an android, hopefully.
the person who wrote "https://docs.python.org/2/howto/sockets.html" is a great human being.
"""

import socket
import sys
import rospy
from std_msgs.msg import String, Float64

class EchoSocket(object):
	"""
	Basic socket, to echo some sort of input/output.
	"""

	def __init__(self, port=8888):
		#creates a socket with INET protocol and streaming; gets this computer's address; uses an arbitrary port; binds socket to the address and port.
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.host = ''
		self.port = port #this is an arbitrary number right now.
		print "Binding socket to ", self.host, " : ", self.port
		self.sock.bind((self.host, self.port))
		self.sock.listen(5)
		rospy.init_node("/android_socket")
		self.button_pub = rospy.Publisher("/android_buttons", String, queue_size=10)
		self.orientation_pub = rospy.Publisher("/android_yaw", Float64, queue_size = 10)
        # rospy.Subscriber("/wii_rumble", Float32, self.set_rumble)


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
		while received_bytes < msg_bytes:
			chunk = c.recv(min(msg_bytes - received_bytes, 2048))
			if chunk == '':
				break
			chunks.append(chunk)
			received_bytes += len(chunk)
		msg = ''.join(chunks)
		if msg[:3] == "Yaw":
			received_data = float(msg[4:])
			self.orientation_pub.publish(Float64(received_data))

		elif msg[:5] == "Start":
			self.button_pub.publish(String("Start"))
		elif msg[:4] == "Drop":
			self.button_pub.publish(String("Drop"))
		elif msg[:6] == "Pickup":
			self.button_pub.publish(String("Pickup"))

		print msg
		c.close()
		return msg


if __name__ == "__main__":
	es = EchoSocket()
	while True:
		es.read_socket(32)