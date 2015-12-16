"""
Basic echo server - prints text sent from an android, hopefully.
the person who wrote "https://docs.python.org/2/howto/sockets.html" is a great human being.
"""

import socket
import sys
import rospy
import string
import math
from std_msgs.msg import String, Float64
import rospkg
from geometry_msgs.msg import PoseStamped, PointStamped, Point32
from tf import TransformListener
from tf.transformations import euler_from_quaternion
from std_msgs.msg import Float64, Float64MultiArray, String, Int32
#TODO: Cleanup imports.

class EchoSocket(object):
	"""
	Basic socket, to echo some sort of input/output.
	"""

	def __init__(self, port=8888):
		#============ Socket setup ===============================
		self.sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
		self.host = ''
		self.port = port #this is an arbitrary number right now.
		print "Binding socket to ", self.host, " : ", self.port
		self.sock.bind((self.host, self.port))
		self.sock.listen(5)

		#============ ROS setup, to listen to the Tango =================

		rospy.init_node("sandroid_socket")
		rospy.Subscriber('/tango_pose', PoseStamped, self.process_pose)
		rospy.Subscriber('/tango_angles', Float64MultiArray, self.process_angle)
		self.rospack = rospkg.RosPack();
		self.tf = TransformListener()

		#=========== Internal odometry: phone and user location + orientation ======
		self.x = 0
		self.y = 0
		self.z = 2.0 

		self.yaw = 10
		self.pitch = 20
		self.roll = 0

		self.target_x = 1.0	
		self.target_y = 2.0
		self.target_z = 3.0

		# above params are hardcoded to debug without tango.

		#============ Misc. parameters, settings, and controls=========
		self.isOn = True
		self.isOnTrail = False
		self.justArrived = False
		self.zero_yaw = 0
		self.phone_yaw = 0
		self.trail = []
		self.threshold = 0.1

	def read_socket(self, msg_bytes=32):
		"""
		reads msg_bytes bytes from self.sock
		"""
		while True:
			c, addr = self.sock.accept()
			# print "got connection from ", addr
			buf = c.recv(64)
			if len(buf) > 0:
				self.refresh_all()
				response_to_send = self.handle_socket_input(buf)
				print "Received: ", str(buf), '\t', "Sending: ", response_to_send
				c.sendall(response_to_send)
				break
		c.close()
		return buf

	def handle_socket_input(self, m):
		m_l = str(m).lower()
		mww = ''.join(c for c in m_l if c.isalnum() or c in [' ', '-', '.', ','])
		message = string.strip(mww)
		response = "nul" # default
		# print repr(message)
		if len(message) < 3:
			return response
		header = message[:3]
		# print header, '\t', repr(header)

		if header == "yaw":
			new_yaw = float(message[4:]) - self.zero_yaw
			if new_yaw < -180:
				self.phone_yaw = 360 + new_yaw
			elif new_yaw > 180:
				self.phone_yaw = -360 + new_yaw
			else:
				self.phone_yaw = new_yaw #relative to the tango
			if self.isOnTrail:
				phone_angle_to_target = self.angle_to_go - self.phone_yaw
				response = "dif " + str(phone_angle_to_target)# angle difference between phone's current orientation and the target.
				if self.justArrived:
					response = response + "| arrived"
					self.justArrived = False
				return response
			# else:
			# 	response = "xyz" + ',' + str(self.x) + ',' + str(self.y) + ',' + str(self.z) # Might be too long - if we need to, we can truncate/round these.
			# 	return response

		elif header == "cmd":
			command_name = message[4:]
			print "Received command/keypress: ", command_name
			# print '****************',
			# print repr(message), repr(command_name)
			if command_name == "new":
				self.isOnTrail = False
				self.trail = []
				response = "xyz" + ',' + str(self.x) + ',' + str(self.y) + ',' + str(self.z) # Might be too long - if we need to, we can truncate/round these.
				return response
			elif command_name == "end":
				self.isOnTrail = False
				self.trail == []
			elif command_name == "point":
				# self.drop_breadcrumb() # commented out b/c handled by phone.
				print "point added"
				response = "xyz" + ',' + str(self.x) + ',' + str(self.y) + ',' + str(self.z) # Might be too long - if we need to, we can truncate/round these.
				self.justArrived = True # temp; testing.
			elif command_name == "zero":
				self.zero()
			elif command_name == "nav":
				self.isOnTrail = True
				t = command_name[4:]
				tlist = t.split(',')
				if len(tlist) < 2:
					return response
				new_point = (tlist[0], tlist[1], 0.0) # assuming constant z-vals for now.
				self.trail.append(new_point)
				print "Current trail: ", self.trail
			elif command_name == "startup":
				self.trail = []
				self.isOnTrail = False
				self.isOn = True
				self.justArrived = False
		return response

	#================ ROS Message - Handler functions ============
	def process_pose(self, msg):
		"""
		zeroes position data, then writes it to class variables.
		"""
		self.x = msg.pose.position.x
		self.y = msg.pose.position.y
		self.z = msg.pose.position.z
		self.pose_timestamp = msg.header.stamp

	def process_angle(self, msg):
		"""
		writes angle info to class variables.
		"""
		self.yaw = msg.data[2]
		self.pitch = msg.data[1]
		self.roll = msg.data[0]

	def set_target(self, point):
		"""
		writes the message info to the target.
		"""
		self.target_x = point[0]
		self.target_y = point[1]
		self.target_z = point[2]

	def pick_up_breadcrumb(self):
		if len(self.trail) == 0:
			print "trail over"
			self.isOnTrail = False
			return
		else:
			self.set_target(self.trail.pop())
			print "new target"
			self.isOnTrail = True
			self.justArrived = True

	def drop_breadcrumb(self):
		current_point = (self.x, self.y, self.z)
		self.trail.append(current_point)
		print "droped breadcrumb"
		self.onTrail = False


	def refresh_all(self):
		if self.z == None or self.target_z == None or self.yaw == None or not self.isOnTrail:
			return
		self.refresh_xy_distance()
		while self.xy_distance < self.threshold:
			self.pick_up_breadcrumb()
			self.refresh_xy_distance()
		self.refresh_angle()

	def refresh_xy_distance(self):
		self.xy_distance = math.sqrt((self.target_x - self.x)**2 + (self.target_y - self.y)**2)

	def refresh_angle(self):
		atg = math.degrees(math.atan2(self.target_y-self.y, self.target_x-self.x) - self.yaw)
		if 180 < atg:
			atg = 360 - atg
		elif atg <= -180:
			atg = atg + 360
		self.angle_to_go = atg

	def zero(self):
		self.zero_yaw = self.phone_yaw



if __name__ == "__main__":
	es = EchoSocket()
	while True:
		es.read_socket(32)