import disassembler
import sys

class Base:
	def __init__(self,z):
		self.z = z
		print("Base __init__")


def main():

	y = 6 # must be before class definition to 
	      # reference in class B. The closure is 
	      # taken during the class definition time
	      # and not when the instance of B gets
	      # created. 

	class A(Base):
		def __init__(self,x,y):
			u = super()
			print(super)
			print(u)
			print(type(u))
			super().__init__(y)
			self.x = x + self.z
			self.y = y + self.z

		def getX(self):
			return self.x

		def slope(self):
			return self.x / self.y

		def __str__(self):
			return "This is an A object"

	class B:
		z = 10

		def __init__(self,x):
			self.x = y

		def getX(self):
			return self.x

	a = A(5,3)

	b = B(5)

	print(a)
	print(type(a))
	print(type(A))
	print(type(6))

	slope = a.slope()

	bx = b.getX()

	bx2 = B.getX(b)

	slope2 = A.slope(a)

	print(slope,slope2,bx,bx2)

if len(sys.argv) == 1:
	main()
else:
	disassembler.disassemble(Base)
	disassembler.disassemble(main)
